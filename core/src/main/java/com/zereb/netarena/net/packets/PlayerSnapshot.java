package com.zereb.netarena.net.packets;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.zereb.netarena.Player;

public class PlayerSnapshot {
    public Vector2 position = new Vector2();
    public Vector2 vel = new Vector2();
    public float life;
    public int id;
    public Rectangle box;

    public String animation;
    public int animationIndex;
    public boolean looping;
    public String name;
    public float ping;
    public int kill;
    public int death;

    public PlayerSnapshot(){}

    public PlayerSnapshot(PlayerSnapshot playerSnapshot){
        position.set(playerSnapshot.position);
        vel.set(playerSnapshot.vel);
        life = playerSnapshot.life;
        id = playerSnapshot.id;
        box = new Rectangle(playerSnapshot.box);
        animation = playerSnapshot.animation;
        animationIndex = playerSnapshot.animationIndex;
        looping = playerSnapshot.looping;
        name = playerSnapshot.name;
        ping = playerSnapshot.ping;
        kill = playerSnapshot.kill;
        death = playerSnapshot.death;
    }

    @Override
    public String toString() {
        return "PlayerSnapshot{" +
                "position=" + position +
                ", life=" + life +
                ", id=" + id +
                ", box=" + box +
                ", animation='" + animation + '\'' +
                ", animationIndex=" + animationIndex +
                ", looping=" + looping +
                '}';
    }
}
