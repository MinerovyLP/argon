package dev.lvstrng.argon.utils;

import dev.lvstrng.argon.event.events.PlayerTickListener;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SchedulerUtils implements PlayerTickListener {

    private long x = 0;
    private final Queue<ScheduledTask> tasks = new ConcurrentLinkedQueue<>();

    @Override
    public void onPlayerTick() {
        for (int i = 0; i < 50; i++) {
            x++;
            ScheduledTask task;
            while ((task = tasks.peek()) != null && x >= task.executeAt) {
                try {
                    task.runnable.run();
                } catch (Exception ignored) {}
                tasks.poll();
            }

            if (tasks.isEmpty()) {
                x = 0;
                break;
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
        tasks.add(new ScheduledTask(func, x + ms));
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
