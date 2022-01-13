package com.zereb.netarena;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.zereb.netarena.utils.AnimationHandler;
import com.zereb.netarena.utils.ResourceManager;

import static com.zereb.netarena.Main.FRAME_TIME;

public class PlayerRender implements Debuggable {

    private TextureRegion frame;
    private final TextureAtlas charset;
    public final AnimationHandler<TextureRegion> animationHandler;
    public static final String A_RUN = "walk";
    public static final String A_ATTACK = "attack";
    public static final String A_ATTACK2 = "attack2";
    public static final String A_ATTACK3 = "attack3";
    public static final String A_IDLE = "idle";
    public static final String A_CAST = "cast";

    private final Player player;

    public PlayerRender(Player player) {
        this.player = player;
        charset = ResourceManager.INSTANCE.loadAtlas("entity/cloack.atlas");
        frame = new TextureRegion(charset.getRegions().first());
        animationHandler = new AnimationHandler<>();

        animationHandler.add(A_RUN, new Animation<>(FRAME_TIME, charset.findRegions(A_RUN)));
        animationHandler.add(A_ATTACK, new Animation<>(FRAME_TIME, charset.findRegions(A_ATTACK)));
        animationHandler.add(A_ATTACK2, new Animation<>(FRAME_TIME, charset.findRegions(A_ATTACK2)));
        animationHandler.add(A_ATTACK3, new Animation<>(FRAME_TIME, charset.findRegions(A_ATTACK3)));
        animationHandler.add(A_IDLE, new Animation<>(FRAME_TIME, charset.findRegions(A_IDLE)));
        animationHandler.add(A_CAST, new Animation<>(FRAME_TIME, charset.findRegions(A_CAST)));
        animationHandler.setCurrent(A_RUN, true);
    }

    public void render(SpriteBatch batch) {
        frame = animationHandler.getFrame(Gdx.graphics.getDeltaTime());

        if (player.vel.x > 0) player.facingState = Player.FacingState.RIGHT;
        else if (player.vel.x < 0) player.facingState = Player.FacingState.LEFT;

        if (player.facingState == Player.FacingState.LEFT && !frame.isFlipX()) frame.flip(true, false);
        if (player.facingState == Player.FacingState.RIGHT && frame.isFlipX()) frame.flip(true, false);

        batch.draw(frame, player.position.x - frame.getRegionWidth() / 2f, player.position.y - frame.getRegionHeight() / 2f);
    }

    public void debug(ShapeRenderer shapeRenderer) {
        if (!Main.isDebug) return;
        shapeRenderer.rect(player.box.x, player.box.y, player.box.width, player.box.height);
    }


}
