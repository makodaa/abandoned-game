package com.teammerge.abandoned.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.teammerge.abandoned.entities.Player;

public class RestScreen extends Table {
    Background background;
    Player player;

    public RestScreen(Player player) {
        this.player = player;
        background = new Background("images/plain_white_background.png");
        background.setColor(0,0,0, 178);
        setSize(1280, 800);
        setBackground(background);


//        Creating actors
        Table topBarTable = new VisTable();
        Label label = new VisLabel("Take a Rest");
        label.setSize(128, 64);
        TextButton closeButton = new VisTextButton("X");
        closeButton.setSize(64,64);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setVisible(false);
            }
        });
        topBarTable.align(Align.topLeft);
        topBarTable.add(closeButton).minSize(64,64).spaceRight(18);
        topBarTable.add(label);

        TextButton oneHourButton = new VisTextButton("1 Hour");
        oneHourButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                player.setMinutes(player.minutes + 1);
                player.setEnergy(player.energy + 5);
                setVisible(false);
            }
        });
        TextButton fourHourButton = new VisTextButton("4 Hours");
        fourHourButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                player.setMinutes(player.minutes + 4);
                player.setEnergy(player.energy + 20);
                setVisible(false);
            }
        });

        TextButton twelveHourButton = new VisTextButton("12 Hours");
        twelveHourButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                player.setMinutes(player.minutes + 4);
                player.setEnergy(player.energy + 48);
                setVisible(false);
            }
        });

//        Finalization, arranging actors

        align(Align.topLeft);
        add(topBarTable).fillX().spaceBottom(18);
        row().expandX().fillX().fillY();
        add(oneHourButton).height(64);
        row().expandX().fillX().fillY();
        add(fourHourButton).height(64);
        row().expandX().fillX().fillY();
        add(twelveHourButton).height(64);
        row().expandX().fillX().fillY();
    }
}
