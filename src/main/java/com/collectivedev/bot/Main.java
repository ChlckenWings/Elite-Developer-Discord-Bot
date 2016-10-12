package com.collectivedev.bot;

import com.collectivedev.bot.json.JsonConfiguration;
import com.collectivedev.bot.logging.BotLogger;
import com.collectivedev.bot.persist.Database;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        Main.getInstance().start();
    }

    private static Main instance;

    private final JsonConfiguration config;
    private final Database database;
    private final BotLogger logger;

    private Main() {
        instance = this;

        this.logger = new BotLogger("Bot", "bot.log");

        this.config = new JsonConfiguration("config.json");
        this.config.copyDefaults("defaultconfig.json");

        this.database = new Database(this.config.get("database").getAsJsonObject());
        this.database.createTables();
    }

    private void start() {

    }

    public static Main getInstance() {
        if(instance == null) {
            instance = new Main();
        }

        return instance;
    }

    public Database getDatabase() {
        return database;
    }

    public BotLogger getLogger() {
        return logger;
    }

    private ExecutorService service;

    public ExecutorService getExecutorService() {
        if(service == null) {
            String name = "Bot";
            service = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat(name + "Pool #%1$d")
                    .setThreadFactory(r -> new Thread(new ThreadGroup(name), r)).build());
        }

        return service;
    }
}