package com.collectivedev.bot.command;

import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;

public abstract class Command {

    private final String name;
    private String usage;
    private String description;

    private int power;

    public Command(String name) {
        this(name, "No usage is available for this command", "No description has been set for this command");
    }

    public Command(String name, String usage, String description) {
        this.name = name;
        this.usage = usage;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public abstract void execute(Message message, Guild guild, String[] args);
}