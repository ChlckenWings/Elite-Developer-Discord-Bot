package com.collectivedev.bot.command.classes;

import com.collectivedev.bot.command.Command;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;

public class EchoCommand extends Command {

    public EchoCommand() {
        super("echo", "<string>", "Echo a string back\nThis command is purely for testing.");
    }

    @Override
    public void execute(Message message, Guild guild, String[] args) {
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < args.length; i++) {
            builder.append(args[i]).append(" ");
        }

        message.getChannel().sendMessageAsync("(Echo): \n```" + builder.toString() + "```", null);
    }
}