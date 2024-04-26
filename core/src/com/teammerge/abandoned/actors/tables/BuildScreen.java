package com.teammerge.abandoned.actors.tables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.teammerge.abandoned.actors.drawables.BackgroundDrawable;
import com.teammerge.abandoned.entities.Campfire;
import com.teammerge.abandoned.entities.FishBasketTrap;
import com.teammerge.abandoned.entities.Player;
import com.teammerge.abandoned.screens.GameScreen;

public class BuildScreen extends Table {

    Player player;
    GameScreen screen;
    BackgroundDrawable backgroundDrawable;
    Campfire campfire;
    FishBasketTrap fishBasketTrap;
    public BuildScreen(GameScreen screen, Player player) {
        this.screen = screen;
        this.player = player;
        backgroundDrawable = new BackgroundDrawable("images/plain_white_background.png");
        backgroundDrawable.setColor(0, 0, 0, 205);
        setSize(1280, 800);
        setBackground(backgroundDrawable);


        Table topBarTable = new Table();
        Label label = new VisLabel("Build");
        label.setSize(128, 72);
        TextButton closeButton = new VisTextButton("X");
        closeButton.setSize(72, 72);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                remove();
            }
        });
        topBarTable.align(Align.left);
        topBarTable.setSize(topBarTable.getPrefWidth(), topBarTable.getPrefHeight());
        topBarTable.add(closeButton).size(72).spaceRight(18);
        topBarTable.add(label);


        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/RobotoCondensed-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 18;
        BitmapFont titleFont = generator.generateFont(parameter);

        Skin skin = new Skin();
        Pixmap pixmap = new Pixmap(10, 10, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(skin.newDrawable("white", new Color(0.5f, 0.5f, 0.5f, 0.2f)), skin.newDrawable("white", new Color(0.5f, 0.5f, 0.5f, 0.5f)), skin.newDrawable("white", new Color(0.5f, 0.5f, 0.5f, 0.5f)), titleFont);

        TextButton campfireButton = new TextButton("Campfire", buttonStyle);
        campfireButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                screen.getStage().addActor(new CampfireScreen(screen, player));
                remove();
            }
        });
        TextButton fishTrapButton = new TextButton("Fish Trap", buttonStyle);
        fishTrapButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                screen.getStage().addActor(new FishTrapScreen(screen, player));
                remove();
            }
        });

        TextButton deadfallTrapButton = new TextButton("Deadfall Trap", buttonStyle);
        deadfallTrapButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                screen.getStage().addActor(new DeadfallTrapScreen(screen, player));
                remove();
            }
        });

        add(topBarTable).top().fillX().height(72);
        row();
        add(campfireButton).fillX().size(135, 63);
        row();
        add(fishTrapButton).fillX().size(135, 63);
        row();
        add(deadfallTrapButton).fillX().size(135, 63).expand().fill();
    }
}
