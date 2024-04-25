package com.teammerge.abandoned.actors.tables;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.teammerge.abandoned.actors.drawables.BackgroundDrawable;
import com.teammerge.abandoned.entities.Player;
import com.teammerge.abandoned.enums.Direction;
import com.teammerge.abandoned.records.Index;
import com.teammerge.abandoned.screens.GameScreen;
import com.teammerge.abandoned.utilities.wfc.classes.Area;
import com.teammerge.abandoned.utilities.wfc.classes.Utils;

public class TravelScreen extends Table {
    BackgroundDrawable backgroundDrawable;
    Player player;
    GameScreen screen;
    int distanceBetweenAreas;

    public TravelScreen(Player player, GameScreen screen) {
        this.player = player;
        this.screen = screen;
        backgroundDrawable = new BackgroundDrawable("images/plain_white_background.png");
        backgroundDrawable.setColor(0,0,0, 205);
        setSize(1280, 800);
        setBackground(backgroundDrawable);

        // Creating actors
        Label label = new VisLabel("Move somewhere:");
        label.setSize(128, 64);

        Table topBarTable = new VisTable();
        topBarTable.align(Align.topLeft);
        topBarTable.add(createCloseButton()).minSize(64,64).spaceRight(18);
        topBarTable.add(label);

        align(Align.topLeft);
        add(topBarTable).fillX();
        row().expandX().fillX().fillY();

        var mapHeight = screen.getMap().length;
        var mapWidth = screen.getMap()[0].length;

        var playerPosition = player.getPosition();
        var y = playerPosition.y();
        var x = playerPosition.x();

        if (y - 1 >= 0) {
            add(createMoveOption(Direction.UP)).height(64);
            row().expandX().fillX().fillY();
        }

        if (x + 1 < mapWidth) {
            add(createMoveOption(Direction.RIGHT)).height(64);
            row().expandX().fillX().fillY();
        }


        if (y + 1 < mapHeight) {
            add(createMoveOption(Direction.DOWN)).height(64);
            row().expandX().fillX().fillY();
        }

        if (x - 1 >= 0) {
            add(createMoveOption(Direction.LEFT)).height(64);
            row().expandX().fillX().fillY();
        }
    }

    private TextButton createCloseButton() {
        TextButton closeButton = new VisTextButton("X");
        closeButton.setSize(64,64);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                closeScreen();
            }
        });

        return closeButton;
    }

    private Actor createMoveOption(Direction direction) {
        Index currentIndex = player.getPosition();
        Area currentArea = screen.getMap()[currentIndex.y()][currentIndex.x()];

        Index targetIndex = player.getPosition().add(direction.getVector());
        Area targetArea = screen.getMap()[targetIndex.y()][targetIndex.x()];
        TextButton choice = new VisTextButton("Move " + direction.getCardinalName() + ": " + targetArea.getName());

        /*
        * Disables button when statistics are too low
        * Computation 2 energy per distance
        */
        distanceBetweenAreas = Math.abs(targetArea.getDistance() - currentArea.getDistance());
        if (player.getEnergy() < (distanceBetweenAreas * 2) + 10){
            choice.setText("Not Enough Energy");
            choice.setDisabled(true);
        }

        /*
        * Conceal area name when dark
        * TODO: Work on UI
        * */
        if (player.getMinutes() % 24 < 6 || 18 < player.getMinutes() % 24) {
            choice.setText("??? | It's too hard to see.");
            choice.getLabel().setColor(Color.RED);
        }

        /// TODO: Work on the layout of the buttons.
        /// TODO: Work on the UI of this screen.
        {
            choice.setColor(Color.BLUE);
        }
        choice.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                DialogScreen dialog = new DialogScreen("Arrived at " + targetArea.getName(),"text");

                if (player.getMinutes() % 24 < 6 || 18 < player.getMinutes() % 24) {
                    if (Utils.random.nextDouble() > 0.90) {
                        dialog = new DialogScreen("Arrived at " + targetArea.getName(), "You got injured along the way because it was too dark.");
                        player.setCondition(player.getCondition() - Utils.random.nextInt(5, 11));
                    }
                }

                LoadingScreen loadingScreen = new LoadingScreen(screen, "Travelling to " + targetArea.getName(), dialog);
                screen.showLoadingScreen(loadingScreen);
                player.setMinutes(player.getMinutes() + (distanceBetweenAreas / 5));
                player.setEnergy(player.getEnergy() - (distanceBetweenAreas * 2));
                for (int i = player.getMinutes(); i < 3 * (player.getMinutes() + (distanceBetweenAreas / 5)); i++) {
                    player.decay();
                }
                screen.move(direction);
                closeScreen();
            }
        });

        return choice;
    }

    private void closeScreen() {
        screen.getActiveScreens().remove(TravelScreen.class);
        setVisible(false);
    }
}
