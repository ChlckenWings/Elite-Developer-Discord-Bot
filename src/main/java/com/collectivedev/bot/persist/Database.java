package com.collectivedev.bot.persist;

import com.google.gson.JsonObject;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Stream;

public class Database {

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
}