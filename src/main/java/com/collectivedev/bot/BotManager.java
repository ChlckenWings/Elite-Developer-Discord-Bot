package com.collectivedev.bot;

import com.collectivedev.bot.command.Command;
import com.collectivedev.bot.persist.Database;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class BotManager {

    private final Map<String, Command> commands = new LinkedHashMap<>();
    private final Map<String, Map<String, Integer>> commandPower = new LinkedHashMap<>();
    private final Map<String, Map<String, Integer>> rolePower = new LinkedHashMap<>();

    public boolean canUse(User user, Guild guild, String command) {
        return getUserPower(user, guild) >= getPowerForCommand(guild.getId(), command);
    }

    public int getUserPower(User user, Guild guild) {
        return guild.getRolesForUser(user).stream()
                .map(r -> rolePower.get(guild.getId()).get(r.getId()))
                .max(Integer::compare)
                .get();
    }

    public void updateCommandPower(String server, String command, int power) {
        updateCommandPower0(server, command, power);

        Main.getInstance().getTaskManager().runAsync(() -> {
            PreparedStatement ps = null;

            try (Connection conn = Main.getInstance().getDatabase().getConnection()) {
                ps = conn.prepareStatement("INSERT INTO `commands`(`server_id`, `command_name`, `power`) " +
                        "VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE `power`=VALUES(`power`);");

                ps.setString(1, server);
                ps.setString(2, command.toLowerCase());
                ps.setInt(3, power);

                ps.executeUpdate();

                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                Database.close(ps);
            }
        });
    }

    private void updateCommandPower0(String server, String command, int power) {
        Map<String, Integer> current = commandPower.get(server);

        current.put(command.toLowerCase(), power);

        commandPower.put(server, current);
    }

    public void updateRolePower(String server, String role, int power) {
        updateRolePower0(server, role, power);

        Main.getInstance().getTaskManager().runAsync(() -> {
            PreparedStatement ps = null;

            try (Connection conn = Main.getInstance().getDatabase().getConnection()) {
                ps = conn.prepareStatement("INSERT INTO `roles`(`server_id`, `role_id`, `power`) " +
                        "VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE `power`=VALUES(`power`)");

                ps.setString(1, server);
                ps.setString(2, role);
                ps.setInt(3, power);

                ps.executeUpdate();

                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                Database.close(ps);
            }
        });
    }

    private void updateRolePower0(String server, String role, int power) {
        Map<String, Integer> current = rolePower.get(server);

        current.put(role, power);

        rolePower.put(server, current);
    }

    public Command getCommand(String name) {
        return commands.get(name.toLowerCase());
    }

    public int getPowerForCommand(String server, String name) {
        return commandPower.entrySet().stream()
                .filter(e -> e.getKey().equals(server))
                .findFirst().orElse(null).getValue().get(name.toLowerCase());
    }

    public void registerCommand(Command command) {
        commands.put(command.getName().toLowerCase(), command);
    }

    /**
     * Populate the map with powers from the database
     */
    void init() {
        initRoles();
        initCommands();
    }

    private void initRoles() {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try (Connection conn = Main.getInstance().getDatabase().getConnection()) {
            ps = conn.prepareStatement("SELECT * FROM `roles`;");

            rs = ps.executeQuery();

            while(rs.next()) {
                String server = rs.getString("server_id");
                String role = rs.getString("role_id");
                int power = rs.getInt("power");

                updateRolePower0(server, role, power);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.close(rs, ps);
        }
    }

    private void initCommands() {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try (Connection conn = Main.getInstance().getDatabase().getConnection()) {
            ps = conn.prepareStatement("SELECT * FROM `commands`;");

            rs = ps.executeQuery();

            while(rs.next()) {
                String server = rs.getString("server_id");
                String command = rs.getString("command_name");
                int power = rs.getInt("power");

                updateCommandPower0(server, command, power);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.close(rs, ps);
        }
    }

    public Map<String, Command> getCommands() {
        return commands;
    }
}