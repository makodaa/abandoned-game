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
import com.teammerge.abandoned.actors.drawables.BackgroundDrawable;
import com.teammerge.abandoned.entities.Player;
import com.teammerge.abandoned.screens.GameScreen;

public class StructuresScreen extends Table {

    Player player;
    GameScreen screen;
    BackgroundDrawable backgroundDrawable;
    public StructuresScreen(GameScreen screen, Player player) {
        this.screen = screen;
        this.player = player;
        backgroundDrawable = new BackgroundDrawable("images/plain_white_background.png");
        backgroundDrawable.setColor(0, 0, 0, 205);
        setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        setBackground(backgroundDrawable);

        FreeTypeFontGenerator mediumGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/RobotoCondensed-Medium.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 36;
        BitmapFont topBarMediumFont = mediumGenerator.generateFont(parameter);
        parameter.size = 24;
        BitmapFont textRegularFont = mediumGenerator.generateFont(parameter);


        // Load Skin, Drawable, and Icons
        Skin skin = new Skin();
        Pixmap pixmap = new Pixmap(10, 10, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));
        skin.add("close_icon", new Texture(Gdx.files.internal("images/icons/close.png")));
        skin.add("campfire_icon", new Texture(Gdx.files.internal("images/icons/structures/campfire.png")));
        skin.add("fish_trap_icon", new Texture(Gdx.files.internal("images/icons/structures/fish_trap.png")));
        skin.add("deadfall_trap_icon", new Texture(Gdx.files.internal("images/icons/structures/deadfall_trap.png")));

        Table topBarTable = new Table();

        Label titlelabel = new Label("STRUCTURES", new Label.LabelStyle(topBarMediumFont,Color.WHITE));

        ImageButton closeButton = new ImageButton(skin.newDrawable("close_icon"));
        closeButton.pad(18.0f);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                remove();
            }
        });
        topBarTable.add(titlelabel).expandX().fillX().right();
        topBarTable.add(closeButton).right();

//        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(skin.newDrawable("white", new Color(0.5f, 0.5f, 0.5f, 0.2f)), skin.newDrawable("white", new Color(0.5f, 0.5f, 0.5f, 0.5f)), skin.newDrawable("white", new Color(0.5f, 0.5f, 0.5f, 0.5f)), textRegularFont);

        ImageTextButton.ImageTextButtonStyle campfireButtonStyle = new ImageTextButton.ImageTextButtonStyle();
        campfireButtonStyle.font = textRegularFont;
        campfireButtonStyle.fontColor = Color.WHITE;
        campfireButtonStyle.up = skin.newDrawable("white",new Color(0.5f,0.5f,0.5f,0.2f));
        campfireButtonStyle.down = skin.newDrawable("white",new Color(0.5f,0.5f,0.5f,0.5f));
        campfireButtonStyle.checked = campfireButtonStyle.down;
        campfireButtonStyle.imageUp = skin.newDrawable("campfire_icon");
        campfireButtonStyle.imageDown = campfireButtonStyle.imageUp;

        ImageTextButton.ImageTextButtonStyle fishTrapButtonStyle = new ImageTextButton.ImageTextButtonStyle();
        fishTrapButtonStyle.font = textRegularFont;
        fishTrapButtonStyle.fontColor = Color.WHITE;
        fishTrapButtonStyle.up = skin.newDrawable("white",new Color(0.5f,0.5f,0.5f,0.2f));
        fishTrapButtonStyle.down = skin.newDrawable("white",new Color(0.5f,0.5f,0.5f,0.5f));
        fishTrapButtonStyle.checked = fishTrapButtonStyle.down;
        fishTrapButtonStyle.imageUp = skin.newDrawable("fish_trap_icon");
        fishTrapButtonStyle.imageDown = fishTrapButtonStyle.imageUp;

        ImageTextButton.ImageTextButtonStyle deadfallTrapButtonStyle = new ImageTextButton.ImageTextButtonStyle();
        deadfallTrapButtonStyle.font = textRegularFont;
        deadfallTrapButtonStyle.fontColor = Color.WHITE;
        deadfallTrapButtonStyle.up = skin.newDrawable("white",new Color(0.5f,0.5f,0.5f,0.2f));
        deadfallTrapButtonStyle.down = skin.newDrawable("white",new Color(0.5f,0.5f,0.5f,0.5f));
        deadfallTrapButtonStyle.checked = deadfallTrapButtonStyle.down;
        deadfallTrapButtonStyle.imageUp = skin.newDrawable("deadfall_trap_icon");
        deadfallTrapButtonStyle.imageDown = deadfallTrapButtonStyle.imageUp;

        ImageTextButton campfireButton = new ImageTextButton("CAMPFIRE",campfireButtonStyle);
        campfireButton.clearChildren();
        campfireButton.add(campfireButton.getImage()).spaceBottom(27);
        campfireButton.row();
        campfireButton.add(campfireButton.getLabel());
        campfireButton.getLabel().setAlignment(Align.bottom);
        campfireButton.pad(90);
        campfireButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                screen.getStage().addActor(new CampfireScreen(screen, player));
                remove();
            }
        });
        ImageTextButton fishTrapButton = new ImageTextButton("FISH TRAP",fishTrapButtonStyle);
        fishTrapButton.clearChildren();
        fishTrapButton.add(fishTrapButton.getImage()).spaceBottom(27);
        fishTrapButton.row();
        fishTrapButton.add(fishTrapButton.getLabel());
        fishTrapButton.pad(90);
        fishTrapButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                screen.getStage().addActor(new FishTrapScreen(screen, player));
                remove();
            }
        });

        ImageTextButton deadfallTrapButton = new ImageTextButton("DEADFALL TRAP",deadfallTrapButtonStyle);
        deadfallTrapButton.clearChildren();
        deadfallTrapButton.add(deadfallTrapButton.getImage()).spaceBottom(27);
        deadfallTrapButton.row();
        deadfallTrapButton.add(deadfallTrapButton.getLabel());
        deadfallTrapButton.pad(90);
        deadfallTrapButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                screen.getStage().addActor(new DeadfallTrapScreen(screen, player));
                remove();
            }
        });

        pad(27.0f);
        defaults().spaceBottom(9);
        align(Align.top);
        add(topBarTable).colspan(3).fillX().expandX().right();
        row().expandY();
        add(campfireButton).fillX().size(300,300);
        add(fishTrapButton).fillX().size(300,300);
        add(deadfallTrapButton).fillX().size(300,300).expandY();
        row();
    }
}
