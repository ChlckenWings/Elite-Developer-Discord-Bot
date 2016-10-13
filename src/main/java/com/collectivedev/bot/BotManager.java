package com.collectivedev.bot;

import com.collectivedev.bot.command.Command;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;

import java.util.LinkedHashMap;
import java.util.Map;

public class BotManager {

    private final Map<String, Command> commandsByServer = new LinkedHashMap<>();
    private final Map<String, Integer> rolePower = new LinkedHashMap<>();

    public boolean canUse(User user, Guild guild, Command command) {
        return true;
    }

    public boolean canUse(User user, Guild guild, String command) {
        return canUse(user, guild, getCommand(guild.getId(), command));
    }

    public int getUserPower(User user, Guild guild) {
        return guild.getRolesForUser(user).stream()
                .map(r -> rolePower.get(r.getName().toLowerCase()))
                .max(Integer::compare)
                .get();
    }

    public void updatePower(String command, int power) {

    }

    public Command getCommand(String server, String name) {
        return commandsByServer.entrySet().stream()
                .filter(e -> e.getKey().equals(server) && e.getValue().getName().equalsIgnoreCase(name))
                .findFirst().orElse(null).getValue();
    }

    public void registerCommand(String server, Command command) {
        commandsByServer.put(server, command);
    }
}