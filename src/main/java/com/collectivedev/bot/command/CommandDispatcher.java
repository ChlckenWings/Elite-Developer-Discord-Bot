package com.collectivedev.bot.command;

import com.collectivedev.bot.Main;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class CommandDispatcher extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String message = event.getMessage().getRawContent();

        if(!message.startsWith(Main.getInstance().getJsonConfiguration().getString("commandChar"))) {
            return;
        }

        Command command = produceCommand(message);

        if(command != null) {
            dispatch(command, event);
        } else {
            event.getMessage().getChannel().sendMessageAsync("What are you talking about? I don't know that command", null);
        }
    }

    private void dispatch(Command command, GuildMessageReceivedEvent event) {
        if(!Main.getInstance().getBotManager().canUse(event.getAuthor(), event.getGuild(), command.getName())) {
            event.getMessage().getChannel().sendMessageAsync("No no... no permissions...", null);
            return;
        }

        command.execute(event.getMessage(), event.getGuild(), buildArgs(event.getMessage().getRawContent()));
    }

    private String[] buildArgs(String string) {
        if(string.contains(" ")) {
            return string.substring(string.indexOf(" ") + 1).split(" ");
        } else {
            return new String[0];
        }
    }

    private Command produceCommand(String message) {
        int length = Main.getInstance().getJsonConfiguration().getString("commandChar").length();

        // has args
        if(message.contains(" ")) {
            return Main.getInstance().getBotManager().getCommand(message.substring(length, message.indexOf(" ")));
        } else {
            // does not have args
            return Main.getInstance().getBotManager().getCommand(message.substring(length, message.length()));
        }
    }
}