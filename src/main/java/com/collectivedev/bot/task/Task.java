package com.collectivedev.bot.task;

import com.collectivedev.bot.Main;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class Task implements Runnable {

    private final TaskManager manager;
    private final int id;
    private final Runnable task;

    private final AtomicBoolean running = new AtomicBoolean(true);

    public Task(TaskManager manager, int id, Runnable task) {
        this.manager = manager;
        this.id = id;
        this.task = task;
    }

    @Override
    public void run() {
        while(running.get()) {
            try {
                task.run();
            } catch (Throwable t) {
                Main.getInstance().getLogger().log(Level.SEVERE, String.format("Task %s encountered an exception", this), t);
            }

            cancel();
        }
    }

    public void cancel() {
        boolean wasRunning = running.getAndSet(false);

        if(wasRunning) {
            manager.cancel0(this);
        }
    }

    public int getId() {
        return id;
    }
}