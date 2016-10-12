package com.collectivedev.bot.logging;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

    private final DateFormat date = new SimpleDateFormat("HH:mm:ss");

    LogFormatter() { }

    @Override
    public String format(LogRecord record) {
        StringBuilder formatted = new StringBuilder();

        formatted.append(date.format(record.getMillis()));
        formatted.append(" [");
        formatted.append(record.getLevel().getLocalizedName());
        formatted.append("] ");
        formatted.append(formatMessage(record));
        formatted.append("\n");

        if(record.getThrown() != null) {
            try(
                    StringWriter writer = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(writer)
            ) {
                record.getThrown().printStackTrace(printWriter);
                formatted.append(writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return formatted.toString();
    }
}