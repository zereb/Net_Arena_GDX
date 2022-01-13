package com.zereb.netarena.utils;


import com.badlogic.gdx.utils.TimeUtils;

import java.util.concurrent.TimeUnit;

public class Cooldown {
    private long COOLDOWN_TIME;
    private long last_time;

    public Cooldown(long COOLDOWN_TIME){
        this.COOLDOWN_TIME = COOLDOWN_TIME;
    }

    public boolean fire(){
        if (timeSinceLastUse() > COOLDOWN_TIME){
            last_time = System.currentTimeMillis();
            return true;
        }

        return false;
    }

    public void fire(Runnable runnable){
        if (fire())
            runnable.run();
    }

    public void fireWithoutCooldown(Runnable runnable){
        if (timeSinceLastUse() > COOLDOWN_TIME)
            runnable.run();
    }

    public long timeSinceLastUse(){
        return TimeUtils.timeSinceMillis(last_time);
    }

    public void reset(){
        last_time = System.currentTimeMillis();
    }

    public float percent(){
        return (float) timeSinceLastUse() / (float) COOLDOWN_TIME;
    }

    public boolean isOnCooldown(){
        return (TimeUtils.timeSinceMillis(last_time) < COOLDOWN_TIME);
    }

    public void COOLDOWN_TIME(long newCooldown){
        COOLDOWN_TIME = newCooldown;
    }


}
