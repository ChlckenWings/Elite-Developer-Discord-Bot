package com.collectivedev.bot;

import com.collectivedev.bot.command.CommandDispatcher;
import com.collectivedev.bot.command.classes.EchoCommand;
import com.collectivedev.bot.json.JsonConfiguration;
import com.collectivedev.bot.persist.Database;
import com.collectivedev.bot.task.TaskManager;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;

import javax.security.auth.login.LoginException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws Exception {
        Main.getInstance().start();
    }

    private static Main instance;

    private JDA jda;

    private JsonConfiguration config;
    private Database database;
    private final BotManager botManager;
    private final TaskManager taskManager;

    private Main() {
        instance = this;

        this.botManager = new BotManager();
        this.taskManager = new TaskManager();
    }

    private void start() throws LoginException, InterruptedException {
        this.config = new JsonConfiguration("config.json");

        this.config.copyDefaults("defaultconfig.json");

        this.jda = new JDABuilder().setBotToken(config.getString("token")).buildBlocking();

        this.jda.addEventListener(new CommandDispatcher());

        this.database = new Database(this.config.get("database").getAsJsonObject());

        this.database.createTables();
        this.registerCommands();
        this.botManager.init();

        this.jda.getGuilds().forEach(guild ->
                botManager.getCommands().keySet().forEach(key ->
                        initDefaultCommands(guild.getId(), key)
                )
        );
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

    public BotManager getBotManager() {
        return botManager;
    }

    public JsonConfiguration getJsonConfiguration() {
        return config;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    private ExecutorService service;

    public ExecutorService getExecutorService() {
        if(service == null) {
            String name = "Bot";
            service = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat(name + " Pool #%1$d")
                    .setThreadFactory(r -> new Thread(new ThreadGroup(name), r)).build());
        }

        return service;
    }

    public JDA getJda() {
        return jda;
    }

    private void registerCommands() {
        botManager.registerCommand(new EchoCommand());
    }

    private void initDefaultCommands(String server, String command) {
        PreparedStatement ps = null;

        try (Connection conn = Main.getInstance().getDatabase().getConnection()) {
            ps = conn.prepareStatement("INSERT IGNORE INTO `commands`(`server_id`, `command_name`, `power`) VALUES(?, ?, ?);");

            ps.setString(1, server);
            ps.setString(2, command.toLowerCase());
            ps.setInt(3, 100);

            ps.executeUpdate();

            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Database.close(ps);
        }
    }
}