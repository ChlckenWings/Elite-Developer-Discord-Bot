package com.collectivedev.bot.persist;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.JsonObject;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Stream;

public class Database implements AbstractDatabase {

    private final HikariDataSource source;

    public Database(String uri, String username, String password) {
        final HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:mysql://" + uri);
        config.setUsername(username);
        config.setPassword(password);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        config.setMaximumPoolSize((Runtime.getRuntime().availableProcessors() * 2) + 1);

        this.source = new HikariDataSource(config);
    }

    public Database(JsonObject o) {
        this(
                o.get("uri").getAsString(),
                o.get("username").getAsString(),
                o.get("password").getAsString()
        );
    }

    public Connection getConnection() {
        try {
            return source.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void close(AutoCloseable... list) {
        Stream.of(list).filter(c -> c != null).forEach(c -> {
            try {
                c.close();
            } catch(Exception ignored) {}
        });
    }

    public void createTables() {
        try(
                Connection conn = getConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(Database.class.getResourceAsStream("init.sql")))
        ) {
            new ScriptRunner(conn).runScript(reader);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Table<String, String, Character> getAllChannelIDs() {
        Table<String, String, Character> table = HashBasedTable.create();

        PreparedStatement ps = null;
        ResultSet rs = null;

        try(Connection conn = getConnection()) {
            ps = conn.prepareStatement("SELECT * FROM `channels`;");

            rs = ps.executeQuery();

            while(rs.next()) {
                table.put(rs.getString("server_id"), rs.getString("channel_id"), rs.getString("channel_type").toCharArray()[0]);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs, ps);
        }

        return table;
    }

    @Override
    public Table<String, String, Character> getAllRoleIDs() {
        Table<String, String, Character> table = HashBasedTable.create();

        PreparedStatement ps = null;
        ResultSet rs = null;

        try(Connection conn = getConnection()) {
            ps = conn.prepareStatement("SELECT * FROM `roles`;");

            rs = ps.executeQuery();

            while(rs.next()) {
                table.put(rs.getString("server_id"), rs.getString("role_id"), rs.getString("role_type").toCharArray()[0]);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs, ps);
        }

        return table;
    }

    @Override
    public Map<String, Character> getChannelsForServer(String server) {
        return getAllChannelIDs().row(server);
    }

    @Override
    public Map<String, Character> getRolesForServer(String server) {
        return getAllRoleIDs().row(server);
    }

    @Override
    public char getChannelType(String server, String channel) {
        return getChannelsForServer(server).get(channel);
    }

    @Override
    public char getRoleType(String server, String role) {
        return getRolesForServer(server).get(role);
    }
}