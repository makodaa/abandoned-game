package com.teammerge.abandoned.actors.tables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.teammerge.abandoned.actors.drawables.BackgroundDrawable;
import com.teammerge.abandoned.entities.Player;
import com.teammerge.abandoned.screens.GameScreen;

public class BaseScreen extends Table {
    BackgroundDrawable backgroundDrawable;
    GameScreen screen;
    Player player;

    public BaseScreen(Player player, GameScreen screen) {
        this.screen = screen;
        this.player = player;
        backgroundDrawable = new BackgroundDrawable("images/plain_white_background.png");
        backgroundDrawable.setColor(0,0,0, 205);
        setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        setBackground(backgroundDrawable);

        FreeTypeFontGenerator mediumGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/RobotoCondensed-Medium.ttf"));
        FreeTypeFontGenerator regularGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/RobotoCondensed-Medium.ttf"));
        FreeTypeFontGenerator lightGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/RobotoCondensed-Light.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 36;
        BitmapFont topBarMediumFont = mediumGenerator.generateFont(parameter);
        parameter.size = 27;
        BitmapFont titleMediumFont = mediumGenerator.generateFont(parameter);
        parameter.size = 24;
        BitmapFont textLightFont = lightGenerator.generateFont(parameter);
        parameter.size = 18;
        BitmapFont textRegularFont = regularGenerator.generateFont(parameter);


        // Load Skin, Drawable, and Icons
        Skin skin = new Skin();
        Pixmap pixmap = new Pixmap(10, 10, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));
        skin.add("close_icon", new Texture(Gdx.files.internal("images/icons/close.png")));

        Table topBarTable = new Table();

        Label titlelabel = new Label("BASE", new Label.LabelStyle(topBarMediumFont,Color.WHITE));

        ImageButton closeButton = new ImageButton(skin.newDrawable("close_icon"));
        closeButton.pad(18);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                remove();
            }
        });
        topBarTable.add(titlelabel).expandX().fillX().right();
        topBarTable.add(closeButton).size(72).right();


        Table journalTable = new Table();
        Label entryNumberLabel = new Label("DAY " + player.getMinutes() / 24, new Label.LabelStyle(titleMediumFont,Color.WHITE));
        Label entryLabel = new Label("", new Label.LabelStyle(textLightFont,Color.WHITE));
        entryLabel.setWrap(true);
        journalTable.align(Align.topLeft);
//        journalTable.add(entryNumberLabel).left();
//        journalTable.row().expandX().fillX();
//        journalTable.add(entryLabel).left();


        Table restTable = new Table();
        Label restLabel = new Label("SLEEP / MEDITATE", new Label.LabelStyle(titleMediumFont,Color.WHITE));
        TextButton oneHourButton = createTimeTextButton(skin,textRegularFont,"1 HOUR", 1);
        TextButton fourHourButton = createTimeTextButton(skin, textRegularFont,"4 HOURS", 4);
        TextButton twelveHourButton = createTimeTextButton(skin, textRegularFont,"12 HOURS", 12);

        restTable.defaults().spaceBottom(9.0f);
        restTable.align(Align.topLeft);
        restTable.add(restLabel).left();
        restTable.row();
        restTable.add(oneHourButton).size(500, 63);
        restTable.row();
        restTable.add(fourHourButton).size(500, 63);
        restTable.row();
        restTable.add(twelveHourButton).size(500, 63);
        restTable.row();

//        Finalization, arranging actors

        pad(18.0f);
        defaults().spaceBottom(9);
        align(Align.topLeft);
        add(topBarTable).colspan(2).expandX().fillX().right();
        row();
        add(journalTable).size(600).left();
        add(restTable).top().left();

    }

    private TextButton createTimeTextButton(Skin skin, BitmapFont font, String text, int timeForward) {
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(skin.newDrawable("white",new Color(0.5f,0.5f,0.5f, 0.2f)), skin.newDrawable("white",new Color(0.5f,0.5f,0.5f, 0.5f)), skin.newDrawable("white",new Color(0.0f,0.0f,0.0f, 0.0f)), font);
        TextButton textButton = new TextButton(text,buttonStyle);
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
                for (int i = player.getMinutes(); i < player.getMinutes() + Math.sqrt((timeForward)); i++) {
                    player.decay();
                }
                remove();
            }
        });
        if (80 < player.getEnergy()) {
            textButton.setDisabled(true);
            textButton.setText("You don't feel tired");
        }
        return textButton;
    }

}
