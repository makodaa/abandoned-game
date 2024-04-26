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

import java.util.Collections;

public class DeadfallTrapScreen extends Table {
    GameScreen screen;
    Player player;
    
    com.teammerge.abandoned.entities.DeadfallTrap deadfallTrap;
    BackgroundDrawable backgroundDrawable;

    Label baitRemainingLabel;

    public DeadfallTrapScreen(GameScreen screen, Player player) {
        this.screen = screen;
        this.player = player;
        this.deadfallTrap = screen.getSmallAnimalTrap();
        backgroundDrawable = new BackgroundDrawable("images/plain_white_background.png");
        backgroundDrawable.setColor(0,0,0, 205);
        backgroundDrawable = new BackgroundDrawable("images/plain_white_background.png");
        backgroundDrawable.setColor(0,0,0, 205);
        setSize(1280, 800);
        setBackground(backgroundDrawable);

        FreeTypeFontGenerator mediumGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/RobotoCondensed-Medium.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 36;
        BitmapFont topBarMediumFont = mediumGenerator.generateFont(parameter);
        parameter.size = 45;
        BitmapFont titleMediumFont = mediumGenerator.generateFont(parameter);
        parameter.size = 24;
        BitmapFont textMediumFont = mediumGenerator.generateFont(parameter);
        BitmapFont textRegularFont = mediumGenerator.generateFont(parameter);


        // Load Skin, Drawable, and Icons
        Skin skin = new Skin();
        Pixmap pixmap = new Pixmap(10, 10, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));
        skin.add("close_icon", new Texture(Gdx.files.internal("images/icons/close.png")));

        Table topBarTable = new Table();

        Label titlelabel = new Label("STRUCTURE: DEADFALL TRAP", new Label.LabelStyle(topBarMediumFont,Color.WHITE));

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


        Table baitInformationLabel = new Table();
        Label baitRemainingLabel = new Label("BAIT REMAINING", new Label.LabelStyle(textMediumFont,Color.DARK_GRAY));
        baitRemainingLabel.setAlignment(Align.left);
        this.baitRemainingLabel = new Label("" + deadfallTrap.getBaitRemaining(), new Label.LabelStyle(textMediumFont,Color.WHITE));
        this.baitRemainingLabel.setAlignment(Align.left);
        baitInformationLabel.add(baitRemainingLabel).left();
        baitInformationLabel.row();
        baitInformationLabel.add(this.baitRemainingLabel).left();

        Table deadfallTrapDescriptionTable = new Table();
        Label deadfallTrapLabel = new Label("DEADFALL TRAP", new Label.LabelStyle(titleMediumFont,Color.WHITE));
        Label buildTimeDescriptionLabel = new Label("TAKES 1HR TO BUILD", new Label.LabelStyle(textMediumFont,Color.DARK_GRAY));
        Label deadfallTrapDescriptionLabel = new Label(
                "A baited deadfall trap. Place in any appropriate area to potentially catch small animals while away",new Label.LabelStyle(textRegularFont,Color.WHITE));
        deadfallTrapDescriptionLabel.setWrap(true);
        deadfallTrapDescriptionTable.add(deadfallTrapLabel).left().row();
        deadfallTrapDescriptionTable.add(buildTimeDescriptionLabel).left().row();
        deadfallTrapDescriptionTable.add(deadfallTrapDescriptionLabel).width(600).left().row();

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(skin.newDrawable("white",new Color(0.5f,0.5f,0.5f, 0.2f)), skin.newDrawable("white",new Color(0.5f,0.5f,0.5f, 0.5f)), skin.newDrawable("white",new Color(0.0f,0.0f,0.0f, 0.0f)), textRegularFont);
        buttonStyle.disabledFontColor = Color.DARK_GRAY;
        buttonStyle.disabled = skin.newDrawable("white",new Color(0.2f,0.1f,0.1f,0.3f));

        Label recipeDescriptionLabel = new Label("REQUIRES: STICK * 4, ROPE * 1",new Label.LabelStyle(textMediumFont,Color.WHITE));

        TextButton buildButton = new TextButton("Build", buttonStyle);
        if (Collections.frequency(player.getInventory(),"stick") < 4 || Collections.frequency(player.getInventory(),"rope") < 1)
            buildButton.setDisabled(true);

        buildButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                deadfallTrap.build(player);
                player.setMinutes(player.getMinutes() - 1);
                player.decay();
                screen.showLoadingScreen(new LoadingScreen(
                        screen,
                        "Building Deadfall Trap",
                        new DialogScreen(
                                "",
                                "Deadfall constructed successfully."
                        )));
            }
        });

        TextButton addBaitButton = new TextButton("Add Bait", buttonStyle);
        if (Collections.frequency(player.getInventory(),"bait") < 1 || !deadfallTrap.isBuilt()) addBaitButton.setDisabled(true);
        addBaitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                deadfallTrap.addBait(player);
            }
        });

        pad(18.0f);
        defaults().spaceBottom(9);
        add(topBarTable).top().fillX().right();
        row();
        add(baitInformationLabel).left();
        row().expandX().fillX();
        add(deadfallTrapDescriptionTable).spaceBottom(36);
        row();
        add(recipeDescriptionLabel).spaceBottom(9);
        row();
        add(buildButton).fillX().size(400, 45);
        row().expand(false,false);
        add(addBaitButton).fillX().size(400, 45);
    }

    public Label getBaitRemainingLabel() {
        return baitRemainingLabel;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        getBaitRemainingLabel().setText(deadfallTrap.getBaitRemaining());
    }
}
