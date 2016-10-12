package com.collectivedev.bot.logging;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.LogRecord;

public class LogDispatcher extends Thread {

    private final BotLogger logger;
    private final BlockingQueue<LogRecord> queue = new LinkedBlockingQueue<>();

    LogDispatcher(BotLogger logger) {
        super("Logging Thread");
        this.logger = logger;
    }

    @Override
    public void run() {
        while(!isInterrupted()) {
            LogRecord record;
            try {
                record = queue.take();
            } catch (InterruptedException e) {
                continue;
            }

            logger.doLog(record);
        }

        for(LogRecord record : queue) {
            logger.log(record);
        }
    }

    public void queue(LogRecord record) {
        if(!isInterrupted()) {
            queue.add(record);
        }
    }
}