package net.rbcraft.hideandseek.game.counter;

public interface Counter {

    void end();

    void countdown(int time);

    int getCounterTime();

    boolean isStop();

}
