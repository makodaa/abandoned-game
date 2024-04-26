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
import com.teammerge.abandoned.entities.Campfire;
import com.teammerge.abandoned.entities.Player;
import com.teammerge.abandoned.screens.GameScreen;

import java.util.Collections;

public class CampfireScreen extends Table {
    GameScreen screen;
    Player player;

    Campfire campfire;

    BackgroundDrawable backgroundDrawable;

    TextButton buildCampfire, useMatches, useFireStarter, addTinder, addFirewood, addHardwood;

    Label hoursLabel;
    public CampfireScreen(GameScreen screen, Player player) {
        this.screen = screen;
        this.player = player;
        this.campfire = screen.getCampfire();
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

        Label titlelabel = new Label("STRUCTURE: CAMPFIRE", new Label.LabelStyle(topBarMediumFont,Color.WHITE));

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

        Table timeInformationTable = new Table();
        Label hoursRemainingLabel = new Label("HOURS REMAINING", new Label.LabelStyle(textMediumFont,Color.DARK_GRAY));
        hoursRemainingLabel.setAlignment(Align.left);
        hoursLabel = new Label("" + campfire.getSecondsRemaining() / 60, new Label.LabelStyle(textMediumFont,Color.WHITE));
        hoursLabel.setAlignment(Align.left);
        timeInformationTable.add(hoursRemainingLabel).left();
        timeInformationTable.row();
        timeInformationTable.add(hoursLabel).left();

        Table campfireDescriptionTable = new Table();
        Label campfireTitleLabel = new Label("CAMPFIRE", new Label.LabelStyle(titleMediumFont,Color.WHITE));
        Label buildTimeDescriptionLabel = new Label("TAKES 1HR TO BUILD", new Label.LabelStyle(textMediumFont,Color.DARK_GRAY));
        Label campfireDescriptionLabel = new Label(
                "A simple, hopefully functioning campfire constructed by yours truly. Will allow you to cook raw items, sanitize dirty water, and keep yourself warm.",new Label.LabelStyle(textRegularFont,Color.WHITE));
        campfireDescriptionLabel.setWrap(true);
        campfireDescriptionTable.add(campfireTitleLabel).left().row();
        campfireDescriptionTable.add(buildTimeDescriptionLabel).left().row();
        campfireDescriptionTable.add(campfireDescriptionLabel).width(600).left().row();

        Label recipeDescriptionLabel = new Label("REQUIRES: FIREWOOD * 3",new Label.LabelStyle(textMediumFont,Color.WHITE));


        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(skin.newDrawable("white",new Color(0.5f,0.5f,0.5f, 0.2f)), skin.newDrawable("white",new Color(0.5f,0.5f,0.5f, 0.5f)), skin.newDrawable("white",new Color(0.0f,0.0f,0.0f, 0.0f)), textRegularFont);
        buttonStyle.disabledFontColor = Color.DARK_GRAY;
        buttonStyle.disabled = skin.newDrawable("white",new Color(0.2f,0.1f,0.1f,0.3f));

        buildCampfire = new TextButton("Build", buttonStyle);
        if (Collections.frequency(player.getInventory(),"firewood") < 3) buildCampfire.setDisabled(true);
        buildCampfire.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                campfire.build(player);
                screen.showLoadingScreen(new LoadingScreen(screen, "Building a campfire",
                        new DialogScreen("","Campfire constructed successfully")));
                removeActor(buildCampfire);
                removeActor(recipeDescriptionLabel);
                row().expand(false,false);
                add(useMatches).size(400,45).row();
                add(useFireStarter).size(400,45).expandY().fillY();
                if (0 < Collections.frequency(player.getInventory(),"matches") && 0 < Collections.frequency(player.getInventory(),"tinder" )) useMatches.setDisabled(false);
                if (0 < Collections.frequency(player.getInventory(),"fire_starter") && 0 <Collections.frequency(player.getInventory(),"tinder" )) useFireStarter.setDisabled(false);
            }
        });

        useFireStarter = new TextButton("Light (Fire Starter)",buttonStyle);
        if (!campfire.isBuilt() || 0 < campfire.getSecondsRemaining() || Collections.frequency(player.getInventory(),"fire_starter" ) == 0 || Collections.frequency(player.getInventory(),"tinder" ) == 0) useFireStarter.setDisabled(true);
        useFireStarter.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screen.showLoadingScreen(new LoadingScreen(screen,"Lighting the campfire",
                        campfire.lightByFireStarter(player)
                                ? new DialogScreen("","Let's hope this works.")
                                : new DialogScreen("","Nothing Happened.")));
                if (0< campfire.getSecondsRemaining()) {
                    removeActor(useFireStarter);
                    removeActor(useMatches);
                    row().expand(false,false);
                    add(addTinder).size(400,45).row();
                    add(addFirewood).size(400,45).row();
                    add(addHardwood).size(400,45).expandY().fillY();
                    if (0 < Collections.frequency(player.getInventory(),"tinder")) addTinder.setDisabled(false);
                    if (0 < Collections.frequency(player.getInventory(),"firewood")) addFirewood.setDisabled(false);
                    if (0 < Collections.frequency(player.getInventory(),"hardwood")) addHardwood.setDisabled(false);
                }
            }
        });

        useMatches = new TextButton("Light (Matches)", buttonStyle);
        if (!campfire.isBuilt() || 0 < campfire.getSecondsRemaining() || Collections.frequency(player.getInventory(),"matches") == 0 || Collections.frequency(player.getInventory(),"tinder" ) == 0) useMatches.setDisabled(true);
        useMatches.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screen.showLoadingScreen(new LoadingScreen(screen,"Lighting the campfire",
                        campfire.lightByMatches(player)
                                ? new DialogScreen("","Let's hope this works.")
                                : new DialogScreen("","Nothing Happened.")));
                if (0< campfire.getSecondsRemaining()) {
                    row().expand(false,false);
                    removeActor(useFireStarter);
                    removeActor(useMatches);
                    add(addTinder).size(400,45).row();
                    add(addFirewood).size(400,45).row();
                    add(addHardwood).size(400,45).expandY().fillY();
                    if (0 < Collections.frequency(player.getInventory(),"tinder")) addTinder.setDisabled(false);
                    if (0 < Collections.frequency(player.getInventory(),"firewood")) addFirewood.setDisabled(false);
                    if (0 < Collections.frequency(player.getInventory(),"hardwood")) addHardwood.setDisabled(false);
                }
            }
        });

        addTinder = new TextButton("Add Tinder\n(+30m)",buttonStyle);
        if (!campfire.isBuilt() || campfire.getSecondsRemaining() == 0 || Collections.frequency(player.getInventory(),"tinder") == 0 ) addTinder.setDisabled(true);
        addTinder.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                campfire.addTinder(player);
                if (!campfire.isBuilt() || campfire.getSecondsRemaining() == 0 || Collections.frequency(player.getInventory(),"tinder") == 0 ) addTinder.setDisabled(true);
            }
        });

        addFirewood = new TextButton("Add Firewood\n(+2h)",buttonStyle);
        if (!campfire.isBuilt() || campfire.getSecondsRemaining() == 0 || Collections.frequency(player.getInventory(),"firewood") == 0) addFirewood.setDisabled(true);
        addFirewood.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                campfire.addFirewood(player);
                if (!campfire.isBuilt() || campfire.getSecondsRemaining() == 0 || Collections.frequency(player.getInventory(),"firewood") == 0) addFirewood.setDisabled(true);
            }
        });

        addHardwood = new TextButton("Add Hardwood\n(+4h)",buttonStyle);
        if (!campfire.isBuilt() || campfire.getSecondsRemaining() == 0 || Collections.frequency(player.getInventory(),"hardwood") == 0) addHardwood.setDisabled(true);
        addHardwood.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                campfire.addHardwood(player);
                if (!campfire.isBuilt() || campfire.getSecondsRemaining() == 0 || Collections.frequency(player.getInventory(),"hardwood") == 0) addHardwood.setDisabled(true);
            }
        });



        pad(18.0f);
        defaults().spaceBottom(9);
        align(Align.topLeft);
        add(topBarTable).expandX().fillX().right();
        row();
        add(timeInformationTable).left();
        row().expand().fill();
        add(campfireDescriptionTable);
        row();
        if (!campfire.isBuilt()){
            add(buildCampfire).size(400,45);
            row();
            add(recipeDescriptionLabel).expandY().fillY();
        }
        if(campfire.isBuilt() && 0 == campfire.getSecondsRemaining()) {
            add(useMatches).size(400,45);
            row();
            add(useFireStarter).size(400,45).expandY().fillY();
        }
        if(campfire.isBuilt() && 0 < campfire.getSecondsRemaining()) {
            add(addTinder).size(400,45).row();
            add(addFirewood).size(400,45).row();
            add(addHardwood).size(400,45).expandY().fillY();
        }
    }

    public Label getHoursLabel() {
        return hoursLabel;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        getHoursLabel().setText(""+ campfire.getSecondsRemaining() / 60);
    }
}
