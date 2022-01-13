package com.zereb.netarena;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.zereb.netarena.net.packets.PlayerSnapshot;
import com.zereb.netarena.utils.AnimationHandler;
import com.zereb.netarena.utils.Cooldown;
import com.zereb.netarena.utils.Utils;

import static com.zereb.netarena.Main.FRAME_TIME;

public class Player {
    public final Vector2 position = new Vector2();
    public final Vector2 vel = new Vector2();
    private final Vector2 lookVec = new Vector2();

    public final Rectangle box;
    private float life;
    public final String name;
    public FacingState facingState = FacingState.RIGHT;
    public int kill;
    public int death;

    public Cooldown respawnCooldown = new Cooldown(3000);
    public boolean isDead;


    public PlayerRender render;
    public AnimationHandler<TextureAtlas.TextureAtlasData.Region> animationHandler;
    private static final TextureAtlas.TextureAtlasData atlasData;

    public boolean isNextAttackChain = false;

    private final String TAG;
    public static float SPEED = 120f;
    static {
        atlasData = new TextureAtlas.TextureAtlasData();
        atlasData.load(Gdx.files.internal("entity/cloack.atlas"), Gdx.files.internal("entity/cloack.png"), false);
    }

    public enum FacingState {
        UP, DOWN, LEFT, RIGHT
    }

    public Player(String name, float x, float y) {
        this.name = name;
        box = new Rectangle(0, 0, 10, 10);
        position.x = x;
        position.y = y;
        life = 100;
        TAG = "Player " + name;

    }

    public void update(float delta) {
        if (isDead){
            animationHandler.setCurrent(PlayerRender.A_IDLE);
            return;
        }
        if (life <= 0){
            death++;
            isDead = true;
            respawnCooldown.reset();
        }

        life -= delta;
        System.out.println(life);

        if (Math.abs(vel.x) > Math.abs(vel.y)) {
            if (vel.x > 0) facingState = FacingState.RIGHT;
            if (vel.x < 0) facingState = FacingState.LEFT;
        } else {
            if (vel.y > 0) facingState = FacingState.UP;
            if (vel.y < 0) facingState = FacingState.DOWN;
        }


        if (isNextAttackChain && animationHandler.isFinished()) {
            Gdx.app.log(TAG, String.valueOf(isNextAttackChain));
            Gdx.app.log(TAG, animationHandler.toString());

            if (animationHandler.isCurrent(PlayerRender.A_ATTACK))
                animationHandler.setCurrent(PlayerRender.A_ATTACK2);

            if (animationHandler.isCurrent(PlayerRender.A_ATTACK2))
                animationHandler.setCurrent(PlayerRender.A_ATTACK3);

            isNextAttackChain = false;
        }

        if (animationHandler.isPlaying(PlayerRender.A_ATTACK)) {
            if (animationHandler.frameIndex() > 2)
                position.add(lookVec.x * delta * SPEED, lookVec.y * delta * SPEED);
        }
        else if (animationHandler.isPlaying(PlayerRender.A_ATTACK2)) {
            if (animationHandler.frameIndex() > 2)
                position.add(lookVec.x * delta * SPEED, lookVec.y * delta * SPEED);
        }
        else if (animationHandler.isPlaying(PlayerRender.A_ATTACK3)) {
            if (animationHandler.frameIndex() < 3)
            position.add(lookVec.x * delta * SPEED / 2, lookVec.y * delta * SPEED / 2);
        }
        else if (vel.len() > 0) {
            animationHandler.setCurrent(PlayerRender.A_RUN, true);
            position.add(vel.x * delta, vel.y * delta);
            lookVec.set(vel);
        } else animationHandler.setCurrent(PlayerRender.A_IDLE, false);


        TextureAtlas.TextureAtlasData.Region frame = animationHandler.getFrame(delta);
        box.width = frame.width;
        box.height = frame.height;

        lookVec.nor();

        box.setCenter(position);
    }


    public void attack() {
        if (animationHandler.isPlaying(PlayerRender.A_ATTACK) || animationHandler.isPlaying(PlayerRender.A_ATTACK2))
            isNextAttackChain = true;
        else
            animationHandler.setCurrent(PlayerRender.A_ATTACK, false);
    }


    public PlayerSnapshot createSnapshot(int id, float ping) {
        PlayerSnapshot snapshot = new PlayerSnapshot();
        snapshot.box = new Rectangle(box);
        snapshot.id = id;
        snapshot.life = life;
        snapshot.position = new Vector2(position);
        snapshot.animationIndex = animationHandler.frameIndex();
        snapshot.animation = animationHandler.current();
        snapshot.looping = animationHandler.isLooping();
        snapshot.name = name;
        snapshot.vel = new Vector2(vel);
        snapshot.kill = kill;
        snapshot.death = death;
        snapshot.ping = ping;

        vel.setZero();
        return snapshot;
    }

    public void applySnapshot(PlayerSnapshot snapshot) {
        box.set(snapshot.box.x, snapshot.box.y, snapshot.box.width, snapshot.box.height);
        position.set(snapshot.position);
        vel.set(snapshot.vel);
        life = snapshot.life;
        if (snapshot.animation != null) {
            render.animationHandler.setCurrent(snapshot.animation, snapshot.looping);
        }
    }

    public void initHeadlessAnimation() {
        Array<TextureAtlas.TextureAtlasData.Region> regions = atlasData.getRegions();
        animationHandler = new AnimationHandler<>();

        animationHandler.add(PlayerRender.A_RUN, new Animation<>(FRAME_TIME, Utils.findRegions(PlayerRender.A_RUN, regions), Animation.PlayMode.LOOP));
        animationHandler.add(PlayerRender.A_ATTACK, new Animation<>(FRAME_TIME, Utils.findRegions(PlayerRender.A_ATTACK, regions)));
        animationHandler.add(PlayerRender.A_ATTACK2, new Animation<>(FRAME_TIME, Utils.findRegions(PlayerRender.A_ATTACK2, regions)));
        animationHandler.add(PlayerRender.A_ATTACK3, new Animation<>(FRAME_TIME, Utils.findRegions(PlayerRender.A_ATTACK3, regions)));
        animationHandler.add(PlayerRender.A_IDLE, new Animation<>(FRAME_TIME, Utils.findRegions(PlayerRender.A_IDLE, regions), Animation.PlayMode.NORMAL));
        animationHandler.add(PlayerRender.A_CAST, new Animation<>(FRAME_TIME, Utils.findRegions(PlayerRender.A_CAST, regions)));
        animationHandler.setCurrent(PlayerRender.A_RUN);

    }


    public void life(float life) {
        this.life = life;
    }

    public float life() {
        return life;
    }


}
