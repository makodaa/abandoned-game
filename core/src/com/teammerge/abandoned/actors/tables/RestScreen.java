package com.teammerge.abandoned.actors.tables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.teammerge.abandoned.actors.drawables.BackgroundDrawable;
import com.teammerge.abandoned.entities.Player;
import com.teammerge.abandoned.screens.GameScreen;

public class RestScreen extends Table {
    BackgroundDrawable backgroundDrawable;
    GameScreen screen;
    Player player;

    public RestScreen(Player player, GameScreen screen) {
        this.screen = screen;
        this.player = player;
        backgroundDrawable = new BackgroundDrawable("images/plain_white_background.png");
        backgroundDrawable.setColor(0,0,0, 205);
        setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        setBackground(backgroundDrawable);


//        Creating actors
        Table topBarTable = new Table();
        Label label = new VisLabel("Take a Rest");
        label.setSize(128, 64);
        TextButton closeButton = new VisTextButton("X");
        closeButton.setSize(64,64);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                remove();
            }
        });
        topBarTable.align(Align.topLeft);
        topBarTable.add(closeButton).minSize(64,64).spaceRight(18);
        topBarTable.add(label);


        TextButton oneHourButton = createTimeTextButton("1 Hour", 1);
        TextButton fourHourButton = createTimeTextButton("4 Hours", 4);
        TextButton twelveHourButton = createTimeTextButton("12 Hours", 12);

//        Finalization, arranging actors

        align(Align.topLeft);
        add(topBarTable).fillX();
        row().expandX().fillX().fillY();
        add(oneHourButton).height(64);
        row().expandX().fillX().fillY();
        add(fourHourButton).height(64);
        row().expandX().fillX().fillY();
        add(twelveHourButton).height(64);
        row().expandX().fillX().fillY();
    }

    private TextButton createTimeTextButton(String text, int timeForward) {
        TextButton textButton = new VisTextButton(text);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                LoadingScreen loadingScreen = new LoadingScreen(
                        screen,
                        "Taking a rest...",
                        new DialogScreen(
                                "",
                                "You slept for " + timeForward + " hour" + (timeForward <= 1 ? "" : "s")));

                screen.showLoadingScreen(loadingScreen);
                player.setMinutes(player.getMinutes() + timeForward);
                player.setEnergy(player.getEnergy() + (5 * timeForward));
                for (int i = player.getMinutes(); i < player.getMinutes() + timeForward; i++) {
                    player.decay();
                }
                remove();
            }
        });
        return textButton;
    }

}
