package com.teammerge.abandoned.actors.tables;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.teammerge.abandoned.actors.drawables.BackgroundDrawable;
import com.teammerge.abandoned.entities.Campfire;
import com.teammerge.abandoned.entities.Player;
import com.teammerge.abandoned.screens.GameScreen;

public class CampfireScreen extends Table {
    GameScreen screen;
    Player player;

    BackgroundDrawable backgroundDrawable;
    public CampfireScreen(GameScreen screen, Player player, Campfire campfire) {
        this.screen = screen;
        this.player = player;
        backgroundDrawable = new BackgroundDrawable("images/plain_white_background.png");
        backgroundDrawable.setColor(0,0,0, 205);
        setSize(1280, 800);
        setBackground(backgroundDrawable);

        Table topBarTable = new Table();
        Label label = new VisLabel("Campfire");
        label.setSize(128, 72);
        TextButton closeButton = new VisTextButton("X");
        closeButton.setSize(72,72);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                remove();
            }
        });
        topBarTable.align(Align.left);
        topBarTable.setSize(topBarTable.getPrefWidth(),topBarTable.getPrefHeight());
        topBarTable.add(closeButton).size(72).spaceRight(18);
        topBarTable.add(label);

        TextButton buildCampfire = new VisTextButton("Build (1h)");
        buildCampfire.setSize(135,135);
        buildCampfire.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

            }
        });

        TextButton useFireStarter = new VisTextButton("Use Fire Starter");
        useFireStarter.setSize(135,135);
        if (!campfire.isBuilt() || 0 < campfire.getSecondsRemaining()) useFireStarter.setDisabled(true);
        useFireStarter.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

            }
        });

        TextButton useMatches = new VisTextButton("Use Matches\n" + "0" + " Remaining");
        useMatches.setSize(135,135);
        if (!campfire.isBuilt() || 0 < campfire.getSecondsRemaining()) useMatches.setDisabled(true);
        useMatches.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

            }
        });

        TextButton addTinder = new VisTextButton("Add Tinder\n(+30m)");
        addTinder.setSize(135,135);
        if (!campfire.isBuilt() || campfire.getSecondsRemaining() == 0) addTinder.setDisabled(true);
        addTinder.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

            }
        });

        TextButton addFirewood = new VisTextButton("Add Firewood\n(+4h)");
        addFirewood.setSize(135,135);
        if (!campfire.isBuilt() || campfire.getSecondsRemaining() == 0) addFirewood.setDisabled(true);
        addFirewood.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

            }
        });

        TextButton addHardwood = new VisTextButton("Add Hardwood\n(+8h)");
        addHardwood.setSize(135,135);
        if (!campfire.isBuilt() || campfire.getSecondsRemaining() == 0) addHardwood.setDisabled(true);
        addHardwood.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

            }
        });

        add(topBarTable).top().fillX().height(72);
        row().expand().fill();
        add(buildCampfire).fillX().size(135);
        add(useMatches).fillX().size(135);
        add(useFireStarter).fillX().size(135);
        row();
        add(addTinder).fillX().size(135);
        add(addFirewood).fillX().size(135);
        add(addHardwood).fillX().size(135).expandY();
    }
}
