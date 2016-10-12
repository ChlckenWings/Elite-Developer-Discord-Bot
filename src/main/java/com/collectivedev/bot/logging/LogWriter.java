package com.collectivedev.bot.logging;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class LogWriter extends Handler {

    @Override
    public void publish(LogRecord record) {
        if(isLoggable(record)) {
            System.out.println(getFormatter().format(record));
        }
    }

    @Override
    public void flush() { }

    @Override
    public void close() throws SecurityException { }
}