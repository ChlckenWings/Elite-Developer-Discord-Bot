package com.collectivedev.bot.logging;

import java.io.IOException;
import java.util.logging.*;

public class BotLogger extends Logger {

    private final Formatter formatter = new LogFormatter();
    private final LogDispatcher dispatcher = new LogDispatcher(this);

    public BotLogger(String loggerName, String filePattern) {
        super(loggerName, null);

        setLevel(Level.ALL);

        try {
            FileHandler fileHandler = new FileHandler(filePattern, 1 << 24, 8, true);

            fileHandler.setFormatter(formatter);
            addHandler(fileHandler);

            LogWriter logWriter = new LogWriter();
            logWriter.setLevel(Level.INFO);
            logWriter.setFormatter(formatter);
            addHandler(logWriter);
        } catch (IOException e) {
            System.err.println("Could not register logger");
            e.printStackTrace();
        }
    }

    @Override
    public void log(LogRecord record) {
        dispatcher.queue(record);
    }

    void doLog(LogRecord record) {
        super.log(record);
    }
}