package net.rbcraft.hideandseek.game.counter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class TimeCounter {

    private long tick;

    private final LinkedHashMap<Runnable, Long> runnable_pool = new LinkedHashMap<>();

    private final LinkedHashMap<Counter, Long> counter_pool = new LinkedHashMap<>();

    public TimeCounter() {
        tick = 0;
    }

    public void start(int time, Runnable runnable) {
        runnable_pool.put(runnable, tick + time);
    }

    public void start(Counter counter) {
        counter_pool.put(counter, tick);
    }

    public void onTick() {
        tick++;

        //处理Runnable池
        synchronized (runnable_pool) {
            LinkedList<Runnable> completedRunnable = new LinkedList<>();
            for (Runnable runnable : runnable_pool.keySet()) {
                if (tick >= runnable_pool.get(runnable)) completedRunnable.add(runnable);
            }
            if (!completedRunnable.isEmpty()) {
                for (int i = 0; i < completedRunnable.size(); i++) {
                    Runnable runnable = completedRunnable.get(i);
                    runnable.run();
                    runnable_pool.remove(runnable);
                }
                completedRunnable.clear();
            }
        }

        //处理Counter池
        synchronized (counter_pool) {
            LinkedList<Counter> completedCounter = new LinkedList<>();
            for (Counter counter : counter_pool.keySet()) {
                long offset = tick - counter_pool.get(counter);
                if (offset % 20 == 0) counter.countdown((int) (counter.getCounterTime() - offset / 20));
                if (counter.isStop() || offset / 20 >= counter.getCounterTime()) {
                    counter.end();
                    completedCounter.add(counter);
                }
            }
            if (!completedCounter.isEmpty()) {
                for (Counter counter : completedCounter) {
                    counter_pool.remove(counter);
                }
                completedCounter.clear();
            }
        }
    }

    public boolean isCounting(Counter counter) {
        return counter_pool.containsKey(counter);
    }

    public boolean isWaiting(Runnable runnable) {
        return runnable_pool.containsKey(runnable);
    }

    public void clear() {
        counter_pool.clear();
        runnable_pool.clear();
    }

}
