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
import com.teammerge.abandoned.entities.FishBasketTrap;
import com.teammerge.abandoned.entities.Player;
import com.teammerge.abandoned.screens.GameScreen;
import com.teammerge.abandoned.utilities.wfc.classes.Area;
import com.teammerge.abandoned.utilities.wfc.enums.AreaType;

import java.util.Collections;

public class FishTrapScreen extends Table {
    GameScreen screen;
    Player player;
    FishBasketTrap fishBasketTrap;
    BackgroundDrawable backgroundDrawable;

    Label baitRemainingLabel;

    public FishTrapScreen(GameScreen screen, Player player) {
        this.screen = screen;
        this.player = player;
        this.fishBasketTrap = screen.getFishTrap();
        backgroundDrawable = new BackgroundDrawable("images/plain_white_background.png");
        backgroundDrawable.setColor(0,0,0, 205);
        backgroundDrawable = new BackgroundDrawable("images/plain_white_background.png");
        backgroundDrawable.setColor(0,0,0, 205);
        setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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

        Label titlelabel = new Label("STRUCTURE: FISH TRAP", new Label.LabelStyle(topBarMediumFont,Color.WHITE));

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
        this.baitRemainingLabel = new Label("" + fishBasketTrap.getBaitRemaining(), new Label.LabelStyle(textMediumFont,Color.WHITE));
        this.baitRemainingLabel.setAlignment(Align.left);
        baitInformationLabel.add(baitRemainingLabel).left();
        baitInformationLabel.row();
        baitInformationLabel.add(this.baitRemainingLabel).left();

        Table fishTrapDescriptionTable = new Table();
        Label fishTrapLabel = new Label("FISH TRAP", new Label.LabelStyle(titleMediumFont,Color.WHITE));
        Label buildTimeDescriptionLabel = new Label("TAKES 1HR TO BUILD", new Label.LabelStyle(textMediumFont,Color.DARK_GRAY));
        Label fishTrapDescriptionLabel = new Label(
                "A basket-like fish trap. Place in by river streams or ponds to potentially catch fish while away",new Label.LabelStyle(textRegularFont,Color.WHITE));
        fishTrapDescriptionLabel.setWrap(true);
        fishTrapDescriptionTable.add(fishTrapLabel).left().row();
        fishTrapDescriptionTable.add(buildTimeDescriptionLabel).left().row();
        fishTrapDescriptionTable.add(fishTrapDescriptionLabel).width(600).left().row();

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(skin.newDrawable("white",new Color(0.5f,0.5f,0.5f, 0.2f)), skin.newDrawable("white",new Color(0.5f,0.5f,0.5f, 0.5f)), skin.newDrawable("white",new Color(0.0f,0.0f,0.0f, 0.0f)), textRegularFont);
        buttonStyle.disabledFontColor = Color.DARK_GRAY;
        buttonStyle.disabled = skin.newDrawable("white",new Color(0.2f,0.1f,0.1f,0.3f));

        Label recipeDescriptionLabel = new Label("REQUIRES: STICK * 3, ROPE * 2",new Label.LabelStyle(textMediumFont,Color.WHITE));
        Label locationRequirementLabel = new Label("MUST BE IN: VILLAGE, FOREST, FARM",new Label.LabelStyle(textMediumFont,Color.DARK_GRAY));

        Area area = screen.getMap()[player.getPosition().y()][player.getPosition().x()];
        TextButton buildButton = new TextButton("Build", buttonStyle);
        if (Collections.frequency(player.getInventory(),"stick") < 5
                || Collections.frequency(player.getInventory(),"rope") < 2
                || area.getType() != AreaType.VILLAGE
                || area.getType() != AreaType.FOREST
                || area.getType() != AreaType.FARM)
            buildButton.setDisabled(true);

        buildButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                fishBasketTrap.build(player);
                player.setMinutes(player.getMinutes() - 1);
                player.decay();
                screen.showLoadingScreen(new LoadingScreen(screen, "Building Fish Trap",null));
            }
        });

        TextButton addBaitButton = new TextButton("Add Bait", buttonStyle);
        if (Collections.frequency(player.getInventory(),"bait") < 1 || !fishBasketTrap.isBuilt()) addBaitButton.setDisabled(true);
        addBaitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                fishBasketTrap.addBait(player);
            }
        });

        pad(18.0f);
        defaults().spaceBottom(9);
        align(Align.top);
        add(topBarTable).top().fillX().right();
        row();
        add(baitInformationLabel).left();
        row().expandX().fillX();
        add(fishTrapDescriptionTable).spaceBottom(36);
        row();
        add(recipeDescriptionLabel).spaceBottom(9);
        row();
        add(locationRequirementLabel).spaceBottom(36);
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
        getBaitRemainingLabel().setText(fishBasketTrap.getBaitRemaining());
    }
}
