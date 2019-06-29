package com.redsponge.nonviolent.intro;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import com.redsponge.nonviolent.Constants;
import com.redsponge.nonviolent.MenuScreen;
import com.redsponge.nonviolent.game.GameScreen;
import com.redsponge.redengine.assets.Fonts;
import com.redsponge.redengine.screen.AbstractScreen;
import com.redsponge.redengine.transitions.TransitionTemplates;
import com.redsponge.redengine.utils.GameAccessor;

public class IntroScreen extends AbstractScreen {

    private TypingLabel typingLabel;
    private int currentFrame;
    private FitViewport viewport;
    private float timeSinceEnd;
    private Music music;
    private boolean transitioned;

    public IntroScreen(GameAccessor ga) {
        super(ga);
    }

    @Override
    public void show() {
        viewport = new FitViewport(Constants.INTRO_WIDTH, Constants.INTRO_HEIGHT);
        currentFrame = -1;

        typingLabel = new TypingLabel("", new Label.LabelStyle(Fonts.pixelMix32, Color.WHITE));
        typingLabel.setPosition(10, 100);
        typingLabel.setWrap(true);
        typingLabel.setWidth(viewport.getWorldWidth() - 20);

        timeSinceEnd = 10;

        music = Gdx.audio.newMusic(Gdx.files.internal("music/once_upon_a_hand.wav"));
        music.setOnCompletionListener(music -> loadNewFrame());
        music.play();
    }

    @Override
    public void tick(float delta) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            quickSkip();
        }

        typingLabel.act(delta);
        if(typingLabel.hasEnded()) {
            timeSinceEnd += delta;
            if(timeSinceEnd > 3) {
                timeSinceEnd = 0;
                loadNewFrame();
            }
        }
    }

    private void quickSkip() {
        if(typingLabel.hasEnded()) {
            loadNewFrame();
        } else {
            typingLabel.skipToTheEnd();
        }
    }

    private void loadNewFrame() {
        currentFrame++;
        try {
            IntroText newFrame = IntroText.INTROES[currentFrame];
            typingLabel.restart(newFrame.getText());
        } catch (ArrayIndexOutOfBoundsException e) {
            if(!transitioned) {
                ga.transitionTo(new MenuScreen(ga), TransitionTemplates.sineSlide(1));
                transitioned = true;
            }
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        typingLabel.draw(batch, 1);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        music.dispose();
    }
}