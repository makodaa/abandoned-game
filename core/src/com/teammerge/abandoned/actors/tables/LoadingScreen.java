package com.teammerge.abandoned.actors.tables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.teammerge.abandoned.actors.drawables.BackgroundDrawable;
import com.teammerge.abandoned.screens.GameScreen;


/*
 * TODO: Implement action enum to generate loading text
 * */
public class LoadingScreen extends Table {
    BackgroundDrawable backgroundDrawable;
    ProgressBar progressBar;

    DialogScreen dialogScreen;

    private  float elapsedTime;
    private final float duration;

    GameScreen screen;

    public LoadingScreen() {
        this(null,"Awesome Loading Transition",null);
    }

    public LoadingScreen(GameScreen screen, String message, DialogScreen dialogScreen) {

        backgroundDrawable = new BackgroundDrawable("images/plain_white_background.png");
        backgroundDrawable.setColor(0f,0f,0f, 205);
        this.elapsedTime = 0f;
        this.duration = 1.75f;
        this.screen = screen;
        this.dialogScreen = dialogScreen;

        setSize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        setBackground(backgroundDrawable);

        // Creating Blank Skin with Plain White Drawable
        Skin skin = new Skin();
        Pixmap pixmap = new Pixmap(10, 10, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));


        // Creating a ProgressBarStyle with Dark Grey Background and White Knob
        ProgressBar.ProgressBarStyle barStyle = new ProgressBar.ProgressBarStyle(skin.newDrawable("white", Color.DARK_GRAY),  new TextureRegionDrawable(new TextureRegion(new Texture(pixmap))));
        barStyle.knobBefore = barStyle.knob;

        // Creating the Progress Bar
        progressBar = new ProgressBar(0, 1, 0.01f, false, barStyle);
        progressBar.setSize(270, progressBar.getPrefHeight());
        progressBar.setValue(0f);

        // Label for action distinction
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/RobotoCondensed-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 18;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();


        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label label = new Label(message, labelStyle);
        label.setAlignment(Align.center);

        defaults().width(270);
        add(label).center();
        row();
        add(progressBar);
        row();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        elapsedTime += delta;
        float progress = elapsedTime / duration;
        progressBar.setValue(progress);

        if (elapsedTime >= duration) {
            if (screen != null && dialogScreen != null) screen.showDialogScreen(dialogScreen);
            remove();
        }
    }

}
