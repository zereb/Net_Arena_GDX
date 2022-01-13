package com.zereb.netarena.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;

import java.util.HashMap;
import java.util.Objects;

public class AnimationHandler<T> {
    private float timer = 0;
    private boolean looping = true;
    private String current;
    private final HashMap<String, Animation<T>> animations = new HashMap<>();

    public void add(String name, Animation<T> animation) {
        animations.put(name, animation);

    }

    public void setCurrent(String name) {
        if (Objects.equals(current, name)) return;
        assert (animations.containsKey(name)) : "No such animation " + name;
        current = name;
        timer = 0;
        looping = true;
    }

    public void setCurrent(String name, boolean looping) {
        setCurrent(name);
        this.looping = looping;
    }

    public void setAnimationDuration(long duration) {
        animations.get(current).setFrameDuration(duration / ((float) animations.get(current).getKeyFrames().length * 1000));
    }

    public boolean isCurrent(String name) {
        return current.equals(name);
    }

    public boolean isFinished() {
        return animations.get(current).isAnimationFinished(timer);
    }

    public boolean isPlaying(String name) {
        return (isCurrent(name) && !isFinished());
    }

    public int frameIndex() {
        return animations.get(current).getKeyFrameIndex(timer);
    }

    public void setFrameIndex(int index){
        Animation animation = animations.get(current);
        timer = animation.getAnimationDuration() / animation.getKeyFrames().length * index;
    }

    public T getFrame(float delta) {
        timer += delta;
        if (!animations.containsKey(current)) throw new NullPointerException(current + " animation does not exist");
        return animations.get(current).getKeyFrame(timer, looping);
    }


    @Override
    public String toString() {
        return "AnimationHandler{" +
                "timer=" + timer +
                ", looping=" + looping +
                ", current='" + current + '\'' +
                ", isFinished=" + isFinished() +
                ", frame index= " + frameIndex() +
//                ", animations=" + animations +

                "\n }";
    }

    public String current() {
        return current;
    }

    public boolean isLooping() {
        return looping;
    }
}
