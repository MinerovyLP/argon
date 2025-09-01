package dev.lvstrng.argon.utils;

import dev.lvstrng.argon.event.events.PlayerTickListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SchedulerUtils implements PlayerTickListener {

    private long x = 0;
    private final List<ScheduledTask> tasks = new ArrayList<>();

    @Override
    public void onPlayerTick() {
        for (int i = 0; i < 50; i++) {
            x++;
            synchronized (tasks) {
                if (!tasks.isEmpty()) {
                    Iterator<ScheduledTask> iterator = tasks.iterator();
                    while (iterator.hasNext()) {
                        ScheduledTask task = iterator.next();
                        if (x >= task.executeAt) {
                            try { task.runnable.run(); } catch (Exception e) {}
                            iterator.remove();
                     }
                 }

                    if (tasks.isEmpty()) {
                        x = 0;
                        break;
                    }
                }
            }
        }
    }

    /**
     * Schedules a task to run after a given delay in ms.
     *
     * @param func The code to execute
     * @param ms   How many ms to wait before running
     */
    public void schedule(Runnable func, long ms) {
        synchronized (tasks) {
            tasks.add(new ScheduledTask(func, x + ms));
        }
    }

    /**
     * Returns the current "ms" time.
     */
    public long getTime() {
        return x;
    }

    private static class ScheduledTask {
        final Runnable runnable;
        final long executeAt;

        ScheduledTask(Runnable runnable, long executeAt) {
            this.runnable = runnable;
            this.executeAt = executeAt;
        }
    }
}