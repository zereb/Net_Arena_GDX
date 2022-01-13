package com.zereb.netarena.ui;



import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import java.util.Locale;

public class SkillButton extends ImageButton {

    private final CooldownTimer cooldownTimer;
    private final BitmapFont font;
    private final GlyphLayout timer;

    private final float COOLDOWN;
    private float cooldown;


    public SkillButton(Drawable drawable, float cooldown, BitmapFont font) {
        super(drawable);
        this.font = font;
        timer = new GlyphLayout();
        COOLDOWN = cooldown;

        this.cooldownTimer = new CooldownTimer(true, this.getWidth(), this.getHeight());
        cooldownTimer.setPosition(0, 0);
        cooldownTimer.setColor(Color.WHITE);

        addActor(this.cooldownTimer);
    }

    @Override
    public void draw (Batch batch, float parentAlpha) {
        updateImage();
        super.draw(batch, parentAlpha);
        if (cooldown > 0) {
            timer.setText(font, getColldownTimer());
            font.draw(batch, timer,
                    getX() + getWidth() * getScaleX() / 2 - timer.width / 2,
                    getY() + getHeight() * getScaleY() / 2 + timer.height / 2
            );
        }
    }

    public void update(float delta) {
        cooldown -= delta;

        if (cooldown > 0) {
            cooldownTimer.setVisible(true);
            cooldownTimer.update(cooldown / COOLDOWN);
        } else {
            cooldownTimer.setVisible(false);
        }
    }


    public String getColldownTimer(){
        return String.format(Locale.ENGLISH, "%.1f", cooldown);
    }

    public boolean fire(){
        if (cooldown <= 0) {
            cooldown = COOLDOWN;
            return true;
        }

        return false;
    }

}
