package com.teammerge.abandoned.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.teammerge.abandoned.AbandonedGame;
import com.teammerge.abandoned.actors.drawables.BackgroundDrawable;
import com.teammerge.abandoned.actors.tables.*;
import com.teammerge.abandoned.entities.Campfire;
import com.teammerge.abandoned.entities.DeadfallTrap;
import com.teammerge.abandoned.entities.FishBasketTrap;
import com.teammerge.abandoned.entities.Player;
import com.teammerge.abandoned.enums.Direction;
import com.teammerge.abandoned.records.Index;
import com.teammerge.abandoned.records.Item;
import com.teammerge.abandoned.utilities.wfc.classes.Area;
import com.teammerge.abandoned.utilities.wfc.classes.MapCollapse;
import com.teammerge.abandoned.utilities.wfc.classes.Utils;
import com.teammerge.abandoned.utilities.wfc.enums.AreaType;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


public class GameScreen implements Screen, Serializable {
    transient AbandonedGame game;
    transient SpriteBatch batch;
    transient Stage stage;
    transient OrthographicCamera camera;
    transient BitmapFont mediumFont, regular, lightFont;

    transient public BackgroundDrawable day, dusk, night, midnight;

    transient LoadingScreen loadingScreen;
    transient DialogScreen dialogScreen;
    transient ProgressBar conditionBar, fullnessBar, hydrationBar, energyBar;
    transient Label conditionLabel, fullnessLabel, hydrationLabel, energyLabel;
    transient Label currentLocationLabel;
    transient Label headDirectionLabel, daysPassedLabel, hoursBeforeNextPhaseLabel;

    transient Table containerTable;

    Player player;
    Campfire campfire;
    FishBasketTrap fishBasketTrap;
    DeadfallTrap deadfallTrap;
    transient MapCollapse mapGenerator;
    Area[][] map;

    transient AtomicBoolean runSerializingThread;
    transient Thread serializingThread;
    boolean isInTransition, isGameDone;
    int mapHeight, mapWidth;
    int minutes, gameEndingScene, daysPassed, itemsCollected, itemsCrafted, distanceTravelled, injuriesFaced, injuriesTreated;

    transient private HashSet<Integer> activeKeys = new HashSet<>();
    transient private HashMap<Integer, KeyHandler> listeners = new HashMap<>();
    boolean flag;

    transient Music music;

    public static GameScreen fromSerialized(final AbandonedGame game, GameScreen serialized) {
        serialized.serializingThread = serialized.createSerializingThread();
        serialized.game = game;
        serialized.batch = new SpriteBatch();
        serialized.camera = new OrthographicCamera();
        serialized.camera.setToOrtho(false, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        serialized.stage = new Stage(new ScreenViewport());
        serialized.loadingScreen = new LoadingScreen();
        serialized.dialogScreen = new DialogScreen();
        serialized.isInTransition = false;
        serialized.isGameDone = false;

        serialized.mediumFont = serialized.generateFont("fonts/RobotoCondensed-Medium.ttf", 28);
        serialized.lightFont = serialized.generateFont("fonts/RobotoCondensed-Light.ttf", 22);

        serialized.loadBackgrounds(serialized.map[serialized.player.getPosition().y()][serialized.player.getPosition().x()].getType().getBackgroundFolders());


        // Set up container table and actor groups
        serialized.containerTable = new Table();
        serialized.containerTable.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Table timeAreaTable = serialized.createTimeAreaTable();
        Table attributesTable = serialized.createAttributesTable();
        Table actionButtonsTable = serialized.createActionButtonsTable();

        serialized.padTable(serialized.containerTable);
        serialized.containerTable.align(Align.topRight);
        serialized.containerTable.add(timeAreaTable).fillX();
        serialized.containerTable.row().expand();
        serialized.containerTable.add(actionButtonsTable).expandX().fillX();
        serialized.containerTable.row().expand().fill();
        serialized.containerTable.align(Align.bottomLeft);
        serialized.containerTable.add(attributesTable);

        serialized.stage.addActor(serialized.containerTable);
        Gdx.input.setInputProcessor(serialized.stage);

        serialized.mapGenerator = new MapCollapse();

//        transient BitmapFont regular; <-- Unimplemented anywhere.

        serialized.activeKeys = new HashSet<>();
        serialized.listeners = new HashMap<>();

        /// Specials.

        serialized.updateNearestLocation();

        serialized.music = Gdx.audio.newMusic(Gdx.files.internal("music/starting_music.mp3"));
        serialized.music.setVolume(0.8f);
        serialized.music.play();

        return serialized;
    }

    public GameScreen(final AbandonedGame game, int mapWidth, int mapHeight) {
        /// Load the serializing thread
        serializingThread = createSerializingThread();

        // Load camera, stage, and assets
        this.game = game;
        this.mapHeight = mapHeight;
        this.mapWidth = mapWidth;

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        stage = new Stage(new ScreenViewport());
        loadingScreen = new LoadingScreen();
        dialogScreen = new DialogScreen();
        isInTransition = false;
        isGameDone = false;


        // Load Fonts
        mediumFont = generateFont("fonts/RobotoCondensed-Medium.ttf", 28);
        lightFont = generateFont("fonts/RobotoCondensed-Light.ttf", 22);


        // Create Instance of Player and Map
        player = new Player(new Index(mapWidth / 2, mapHeight / 2));
        mapGenerator = new MapCollapse();
        map = mapGenerator.generateMap(mapWidth, mapHeight);
        minutes = player.getMinutes();

        // Load Background/s
        loadBackgrounds(map[player.getPosition().y()][player.getPosition().x()].getType().getBackgroundFolders());

        // Create Instance of Other Entities
        campfire = new Campfire();
        fishBasketTrap = new FishBasketTrap();
        deadfallTrap = new DeadfallTrap();

        //Set up music
        music = Gdx.audio.newMusic(Gdx.files.internal("music/starting_music.mp3"));
        music.setVolume(0.8f);
        music.setLooping(false);
        music.play();


        // Set up container table and actor groups
        containerTable = new Table();
        containerTable.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Table timeAreaTable = createTimeAreaTable();
        Table attributesTable = createAttributesTable();
        Table actionButtonsTable = createActionButtonsTable();

        padTable(containerTable);
        containerTable.align(Align.topRight);
        containerTable.add(timeAreaTable).fillX();
        containerTable.row().expand();
        containerTable.add(actionButtonsTable).expandX().fillX();
        containerTable.row().expand().fill();
        containerTable.align(Align.bottomLeft);
        containerTable.add(attributesTable);

        stage.addActor(containerTable);
        Gdx.input.setInputProcessor(stage);

        updateNearestLocation();

        activeKeys = new HashSet<>();
        listeners = new HashMap<>();

        /// FIXME: Remove this for release.
//        addKeyListener(Input.Keys.M, this::tryForAmbienceTrack);
    }

    private Thread createSerializingThread() {
        GameScreen self = this;
        runSerializingThread = new AtomicBoolean();
        runSerializingThread.set(true);

        Thread thread = new Thread(() -> {
            while (runSerializingThread.get()) {
                System.out.println("Saving file");
                String path = Gdx.files.internal("saves/save_file.txt").path();
                try (FileOutputStream fileOutputStream = new FileOutputStream(path);
                        ObjectOutputStream objectOutputStream  = new ObjectOutputStream(fileOutputStream)) {
                        if (runSerializingThread.get()) {
                            objectOutputStream.writeObject(self);
                        }
                    } catch (IOException e) {
                        System.out.println("Something went wrong saving the state: " + String.join("\n", Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList()));
                        runSerializingThread.set(false);
                };
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    /// If it fails, just terminate.
                    runSerializingThread.set(false);
                }
            }
        });
        thread.start();

        return thread;
    }

    private void loadBackgrounds(String folder) {
        day = new BackgroundDrawable(Gdx.files.internal("images/backgrounds/" + folder + "/day.png").path());
        dusk = new BackgroundDrawable(Gdx.files.internal("images/backgrounds/" + folder + "/dusk.png").path());
        night = new BackgroundDrawable(Gdx.files.internal("images/backgrounds/" + folder + "/night.png").path());
        midnight = new BackgroundDrawable(Gdx.files.internal("images/backgrounds/" + folder + "/midnight.png").path());
    }

    public Area[][] getMap() {
        return this.map;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
        stage.act(delta);
        for (Map.Entry<Integer, KeyHandler> entry : listeners.entrySet()) {
            int key = entry.getKey();
            var handler = entry.getValue();

            if (activeKeys.contains(key)) {
                if (Gdx.input.isKeyPressed(key)) {
                    continue;
                } else {
                    activeKeys.remove(key);
                }
            } else {
                if (Gdx.input.isKeyPressed(key)) {
                    activeKeys.add(key);
                    handler.run();
                } else {
                    continue;
                }
            }
        }

        /*
         * Time stuff (Day, Hours til sundown/ sunrise)
         */

        if (loadingScreen.getStage() == null && dialogScreen.getStage() == null) {
            player.tick(delta * 1000);
            checkForWinLoseConditions();
                if (player.getMinutes() % 8 == 0 && player.getTimeSinceLastSecond() == 0) {
                    checkForStructureEvents();
                    tryForSpoilage();
                }
            if (Arrays.asList(4,16,17,18,19,20,23).contains(player.getMinutes() % 24) && player.getTimeSinceLastSecond() == 0) {
                tryForAmbienceTrack();
            }
            tryForEndCondition();
        }


        updateAttributeGraphics();
        updateLocationGraphics();
        updateDiurnalCycleGraphics();
        if (!flag) {
            if (player.getInventory().contains("backpack")) {
                player.setInventoryCapacity(player.getInventoryCapacity() + 10);
                flag = true;
            }
        }

        if(flag){
            if (!player.getInventory().contains("backpack")){
                player.setInventoryCapacity(player.getInventoryCapacity() - 10);
                flag = false;
            }
        }
    }


    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        /// FIXME: This is a hack.
        try {
            if (lightFont != null) lightFont.dispose();
        } finally {
            try {
                if (mediumFont != null) mediumFont.dispose();
            } finally {
                try {
                    if (batch != null) batch.dispose();
                } finally {
                    try {
                        if (stage != null) stage.dispose();
                    } finally {
                        if (runSerializingThread != null) runSerializingThread.set(false);
                    }
                }
            }
        }
    }

    private Label createLabel(String text, BitmapFont font, Color color) {
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        Label label = new Label(text, labelStyle);
        label.setColor(color);
        return label;
    }

    private Table createAttributesTable() {
        Table table = new Table();
        Table conditionBarGroup = new Table();
        Table fullnessBarGroup = new Table();
        Table hydrationBarGroup = new Table();
        Table energyBarGroup = new Table();

        conditionLabel = createLabel("CONDITION" , lightFont, Color.LIGHT_GRAY);
        fullnessLabel = createLabel("FULLNESS" , lightFont, Color.LIGHT_GRAY);
        hydrationLabel = createLabel("HYDRATION" , lightFont, Color.LIGHT_GRAY);
        energyLabel = createLabel("ENERGY" , lightFont, Color.LIGHT_GRAY);

        Skin skin = new Skin();
        Pixmap pixmap = new Pixmap(10, 18, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));

        ProgressBar.ProgressBarStyle barStyle = new ProgressBar.ProgressBarStyle(skin.newDrawable("white", new Color(0,0,0,0.3f)),  new TextureRegionDrawable(new TextureRegion(new Texture(pixmap))));
        barStyle.knobBefore = barStyle.knob;

        conditionBar = new ProgressBar(0.0f, 100.0f, 1.0f, false, barStyle);
        conditionBar.setSize(290, conditionBar.getPrefHeight());
        conditionBar.setValue(0f);

        fullnessBar = new ProgressBar(0.0f, 100.0f, 1.0f, false, barStyle);
        fullnessBar.setSize(290, fullnessBar.getPrefHeight());
        fullnessBar.setValue(0f);

        hydrationBar = new ProgressBar(0.0f, 100.0f, 1.0f, false, barStyle);
        hydrationBar.setSize(290, hydrationBar.getPrefHeight());
        hydrationBar.setValue(0f);

        energyBar = new ProgressBar(0.0f, 100.0f, 1.0f, false, barStyle);
        hydrationBar.setSize(290, hydrationBar.getPrefHeight());
        hydrationBar.setValue(0f);

        table.defaults().pad(0,18,0,18);

        conditionBarGroup.add(conditionLabel).left();
        conditionBarGroup.row().fillX().expandX();
        conditionBarGroup.add(conditionBar);

        fullnessBarGroup.add(fullnessLabel).left();
        fullnessBarGroup.row().fillX().expandX();
        fullnessBarGroup.add(fullnessBar);

        hydrationBarGroup.add(hydrationLabel).left();
        hydrationBarGroup.row().fillX().expandX();
        hydrationBarGroup.add(hydrationBar);

        energyBarGroup.add(energyLabel).left();
        energyBarGroup.row().fillX().expandX();
        energyBarGroup.add(energyBar);

        table.add(conditionBarGroup).expandX().fillX();
        table.add(fullnessBarGroup).expandX().fillX();
        table.add(hydrationBarGroup).expandX().fillX();
        table.add(energyBarGroup).expandX().fillX();

        return table;
    }

    private Table createTimeAreaTable() {
        Table table = new Table();

        currentLocationLabel = createLabel("Location", mediumFont, new Color(0.5f,0.5f,0.5f, 0.6f));
        currentLocationLabel.setAlignment(Align.right);

        daysPassedLabel = createLabel("Day 0", mediumFont, new Color(0.5f,0.5f,0.5f, 0.6f));
        daysPassedLabel.setAlignment(Align.right);

        Table locationDaysGroupTable = new Table();
        locationDaysGroupTable.add(currentLocationLabel).right();
        locationDaysGroupTable.add(daysPassedLabel).spaceLeft(18.0f).right();

        hoursBeforeNextPhaseLabel = createLabel(" ", mediumFont, Color.WHITE);
        hoursBeforeNextPhaseLabel.setAlignment(Align.right);

        headDirectionLabel = createLabel(" ", mediumFont, Color.WHITE);
        headDirectionLabel.setAlignment(Align.right);

        table.align(Align.topRight);
        table.add(locationDaysGroupTable).right();
        table.row();
        table.add(hoursBeforeNextPhaseLabel).right();
        table.row();
        table.add(headDirectionLabel).right();

        return table;
    }

    public Table createActionButtonsTable() {
        GameScreen self = this;

        // Create Table
        Table table = new Table();

        ///Textures

        Texture restTexture = new Texture(Gdx.files.internal("images/icons/buttons/Sleep.png"));
        Texture travelTexture = new Texture(Gdx.files.internal("images/icons/buttons/Travel.png"));
        Texture scavengeTexture = new Texture(Gdx.files.internal("images/icons/buttons/Scavenge.png"));
        Texture inventoryTexture = new Texture(Gdx.files.internal("images/icons/buttons/Inventory.png"));
        Texture craftTexture = new Texture(Gdx.files.internal("images/icons/buttons/Craft.png"));
        Texture buildTexture = new Texture(Gdx.files.internal("images/icons/buttons/Build.png"));
        Texture woodTexture = new Texture(Gdx.files.internal("images/icons/buttons/Wood.png"));
        Texture waterTexture = new Texture(Gdx.files.internal("images/icons/buttons/Water.png"));


        /// REST BUTTON TEXTURE AND IMPLEMENTATION.

        TextureRegion restTR = new TextureRegion(restTexture);
        TextureRegionDrawable restTRD = new TextureRegionDrawable(restTR);
        ImageButton restButton = new ImageButton(restTRD);
        restButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                BaseScreen overlay = new BaseScreen(player, self);
                stage.addActor(overlay);
                overlay.setVisible(true);
            }
        });


        /// TRAVEL BUTTON TEXTURE AND IMPLEMENTATION.
        TextureRegion travelTR = new TextureRegion(travelTexture);
        TextureRegionDrawable travelTRD = new TextureRegionDrawable(travelTR);
        ImageButton travelButton = new ImageButton(travelTRD);

        travelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                TravelScreen overlay = new TravelScreen(player, self);
                stage.addActor(overlay);
                overlay.setVisible(true);
            }
        });

        ///SCAVENGE BUTTON TEXTURE AND IMPLEMENTATION
        TextureRegion scavengeTR = new TextureRegion(scavengeTexture);
        TextureRegionDrawable scavengeTRD = new TextureRegionDrawable(scavengeTR);
        ImageButton scavengeButton = new ImageButton(scavengeTRD);

        scavengeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // player.scavenge();
                Index position = player.getPosition();
                Area area = map[position.y()][position.x()];
                List<String> extractedItems = area.extract();
                itemsCollected += extractedItems.size();
                player.decay();
                DialogScreen dialog = new DialogScreen("Collected", displayItemsForMessage(extractedItems));
                switch(Utils.random.nextInt(1,10)) {
                    case 1:
//                        If too dark, player has a chance to injure themselves
                        int minuteLeftover = player.getMinutes() % 24;
                        if (!(6 <= minuteLeftover && minuteLeftover <= 18) &&
                                !player.getInventory().contains("flashlight")) {
//                            10% Chance of Injury
                            if (Utils.random.nextDouble() > 0.80) {
                                player.setCondition(player.getCondition() - 5);
                                extractedItems.clear();
                                dialog = new DialogScreen("Scavenge Failed", "It was too dark, and you injured yourself");
                                setInjuriesFaced(getInjuriesTreated() + 1);
                            }
                        }
                        break;
                    case 2:
//                        If in Village, Commercial Bldg, or Mall, might face against dangerous survivors
                        if (Arrays.asList(AreaType.VILLAGE, AreaType.COMMERCIAL_BLDG, AreaType.MALL).contains(area.getType())) {
                            if (Utils.random.nextDouble() > 0.82) {
                                player.setCondition(player.getCondition() - 5);
                                extractedItems.clear();
                                dialog = new DialogScreen("Scavenge Failed", "You've encountered and fought with a violent survivor" +
                                        (!player.getInventory().isEmpty()
                                                ? " and lost " + player.getInventory().remove(Utils.random.nextInt(0,player.getInventory().size()))
                                                : "."
                                        )
                                );
                                setInjuriesFaced(getInjuriesFaced() + 1);
                            }
                        }
                        break;
                    case 3:
//                        If in Forest, Park, Village, Farm, might encounter wild animals
                        if (Arrays.asList(AreaType.FOREST,AreaType.VILLAGE,AreaType.PARK).contains(area.getType())) {
                            if (Utils.random.nextDouble() > 0.90) {
                                player.setCondition(player.getCondition() - 10);
                                extractedItems.clear();
                                dialog = new DialogScreen("Scavenge Failed", "A snake was hiding around the area and bit you.");
                                setInjuriesFaced(getInjuriesFaced() + 1);
                            }
                        }
                        break;
                    case 4:
//                        In commercial buildings and malls, chance for electrocution
                        if (Arrays.asList(AreaType.MALL, AreaType.COMMERCIAL_BLDG, AreaType.HOSPITAL).contains(area.getType())) {
                            if (Utils.random.nextDouble() > 0.82) {
                                player.setCondition(player.getCondition() - 15);
                                extractedItems.clear();
                                dialog = new DialogScreen("Scavenge Failed", "You made contact with some live wires");
                                setInjuriesFaced(getInjuriesFaced() + 1);
                            }
                        }
                        break;
                    case 5:
                        if (Arrays.asList(AreaType.MALL, AreaType.COMMERCIAL_BLDG, AreaType.VILLAGE, AreaType.HOSPITAL).contains(area.getType())) {
                            if (Utils.random.nextDouble() > 0.82) {
                                player.setCondition(player.getCondition() - 15);
                                extractedItems.clear();
                                dialog = new DialogScreen("Scavenge Failed", "You were hit by fallen debris");
                                setInjuriesFaced(getInjuriesFaced() + 1);
                            }
                        }
                        break;
                    case 6:
                        if (Arrays.asList(AreaType.MALL, AreaType.COMMERCIAL_BLDG, AreaType.VILLAGE).contains(area.getType())) {
                            if (Utils.random.nextDouble() > 0.82) {
                                player.setCondition(player.getCondition() - 5);
                                extractedItems.clear();
                                dialog = new DialogScreen("Scavenge Failed", "You fell down a sharp ledge");
                                setInjuriesFaced(getInjuriesFaced() + 1);
                            }
                        }
                        break;
                    case 7:
                        if (player.getEnergy() < 15) {
                            player.setCondition(player.getCondition() - 5);
                            extractedItems.clear();
                            dialog = new DialogScreen("Scavenge Failed", "You collapsed as you tried to scavenge aimlessly");
                            player.setMinutes(player.getMinutes() + 4);
                            for (int i = 0; i < 4; i++) player.decay();
                            setInjuriesFaced(getInjuriesFaced() + 1);
                        }
                        break;
                }

                showLoadingScreen(new LoadingScreen(self,"Looking around for materials...", dialog));
                Sound sound = Gdx.audio.newSound(Gdx.files.internal("sounds/scavenge.wav"));
                sound.play();
                player.addAllItems(extractedItems.toArray(String[]::new));
                player.setMinutes(player.getMinutes() + 1);
                player.decay();
            }
        });
        ///INVENTORY BUTTON TEXTURE AND IMPLEMENTATION
        TextureRegion inventoryTR = new TextureRegion(inventoryTexture);
        TextureRegionDrawable inventoryTRD = new TextureRegionDrawable(inventoryTR);
        ImageButton inventoryButton = new ImageButton(inventoryTRD);
        inventoryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                InventoryScreen overlay = new InventoryScreen(self,player);
                stage.addActor(overlay);
                overlay.setVisible(true);
            }
        });

        ///CAMPFIRE BUTTON TEXTURE AND IMPLEMENTATION
        TextureRegion campfireTR = new TextureRegion(buildTexture);
        TextureRegionDrawable campfireTRD = new TextureRegionDrawable(campfireTR);
        ImageButton campfireButton = new ImageButton(campfireTRD);
        campfireButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                StructuresScreen overlay = new StructuresScreen(self,player);
                stage.addActor(overlay);
                overlay.setVisible(true);
            }
        });
        ///CRAFT BUTTON TEXTURE AND IMPLEMENTATION
        TextureRegion craftTR = new TextureRegion(craftTexture);
        TextureRegionDrawable craftTRD = new TextureRegionDrawable(craftTR);
        ImageButton craftingButton = new ImageButton(craftTRD);
        craftingButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CraftingScreen overlay = new CraftingScreen(player, self);
                stage.addActor(overlay);
                overlay.setVisible(true);
            }
        });

        TextureRegion woodTR = new TextureRegion(woodTexture);
        TextureRegionDrawable woodTRD = new TextureRegionDrawable(woodTR);
        ImageButton woodButton = new ImageButton(woodTRD);
        woodButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int yield = Utils.random.nextInt(0,5);
                if (player.getInventory().contains("axe"))  yield+= 3;
                int bonus = Utils.random.nextInt(0,2);
                for (int i = 0; i < yield; i++) player.getInventory().add("firewood");
                if (player.getInventory().contains("axe") && bonus == 1) player.getInventory().add("hardwood");
                Sound sound = Gdx.audio.newSound(Gdx.files.internal("sounds/wood.mp3"));
                sound.play();
                showLoadingScreen(new LoadingScreen(
                        self,
                        "Gathering Wood",
                        new DialogScreen(
                                "Gathering Completed",
                                0 < yield ?
                                        (0 < bonus && player.getInventory().contains("axe"))
                                                ? "You found " + yield + " firewood and " + bonus + " hardwood"
                                                : "You found " + yield  + " firewood."
                                        : "There's no wood to be found."
                                )));

            }
        });

        TextureRegion waterTR = new TextureRegion(waterTexture);
        TextureRegionDrawable waterTRD = new TextureRegionDrawable(waterTR);
        ImageButton waterButton = new ImageButton(waterTRD);
        waterButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Index position = player.getPosition();
                Area area = map[position.y()][position.x()];
                boolean mapCondition = Arrays.asList(AreaType.VILLAGE, AreaType.FARM, AreaType.FOREST).contains(area.getType());
                boolean inventoryCondition = player.getInventory().contains("empty_bottle");
                if (!mapCondition) {
                    showDialogScreen(new DialogScreen("","There's no sources available"));
                } else if (!inventoryCondition) {
                    showDialogScreen(new DialogScreen("","You have no ways to carry water"));}
                if(mapCondition && inventoryCondition) {
                    showLoadingScreen(new LoadingScreen(self, "Collecting Water", new DialogScreen("Collection Successful","You collected some dirty water")));
                    player.decay();
                    Sound sound = Gdx.audio.newSound(Gdx.files.internal("sounds/water.mp3"));
                    sound.play();
                    player.getInventory().remove("empty_bottle");
                    player.getInventory().add("dirty_water");
                }
            }
        });



        // Finalization, arranging actors onto table
        padTable(table);
        table.align(Align.left);
        table.defaults().size(108);


        table.add(restButton).pad(12).padLeft(0);
        table.add(travelButton).pad(12);

        table.row().fillX();

        table.add(scavengeButton).pad(12).padLeft(0);
        table.add(inventoryButton).pad(12);

        table.row().fillX();

        table.add(craftingButton).pad(12).padLeft(0);
        table.add(campfireButton).pad(12);

        table.row().fillX();

        table.add(woodButton).pad(12).padLeft(0);
        table.add(waterButton).pad(12);

        return table;
    }

    private void padTable(Table table) {
        table.padBottom(Gdx.graphics.getHeight() / 32.0f);
        table.padTop(Gdx.graphics.getHeight() / 32.0f);
        table.padLeft(Gdx.graphics.getWidth() / 40.0f);
        table.padRight(Gdx.graphics.getWidth() / 40.0f);
    }

    private void updateLocationGraphics() {
        Area area = map[player.getPosition().y()][player.getPosition().x()];
        currentLocationLabel.setText((area.getName()).toUpperCase());
    }

    private void updateAttributeGraphics() {
        conditionBar.setValue(player.getCondition());
        fullnessBar.setValue(player.getFullness());
        hydrationBar.setValue(player.getHydration());
        energyBar.setValue(player.getEnergy());
    }

    private void updateDiurnalCycleGraphics() {
        int hours = player.getMinutes() % 24;
        int waitingHours;
        String dayCycle, nextCycle;
        daysPassed = player.getMinutes() / 24;
        // Following conditions check if time is 0AM - 12AM, and 1PM - 11PM
        if (0 <= hours && hours < 12) {
            if (hours < 6) {
                dayCycle = "EARLY MORNING, ";
                nextCycle = "SUNRISE";
                waitingHours = 6 - hours;
                containerTable.setBackground(midnight);
            } else {
                dayCycle = "MORNING, ";
                nextCycle = "NOON";
                waitingHours = 12 - hours;

                containerTable.setBackground(day);
            }
        } else {
            if (hours < 18) {
                dayCycle = "AFTERNOON, ";
                nextCycle = "SUNFALL";
                waitingHours = 18 - hours;

                containerTable.setBackground(dusk);
            } else {
                dayCycle = "EVENING, ";
                nextCycle = "MIDNIGHT";
                waitingHours = 24 - hours;
                containerTable.setBackground(night);
            }

        }

        daysPassedLabel.setText("DAY " + daysPassed);

        hoursBeforeNextPhaseLabel.setText(dayCycle + waitingHours + " HOURS BEFORE " + nextCycle);

//        headDirectionLabel.setText("" + player.getTimeSinceLastSecond());
    }

    private void updateNearestLocation() {
        Index[] found = null;
        ArrayList<Index[]> queue  = new ArrayList<>();
        HashSet<Index> seen = new HashSet<>();
        queue.add(new Index[] {player.getPosition()});

        outer:
        while (!queue.isEmpty()) {
            Index[] currentPath = queue.removeFirst();
            Index latest = currentPath[0];

            seen.add(latest);
            final Index[] neighbors = {
                    new Index(-1, 0),
                    new Index(0, 1),
                    new Index(1, 0),
                    new Index(0, -1),
            };

            for (Index delta : neighbors) {
                int ny = latest.y() + delta.y();
                int nx = latest.x() + delta.x();
                if (!((0 <= ny && ny < mapHeight) && (0 <= nx && nx < mapWidth))) continue;

                if (map[ny][nx].getType() == AreaType.RESCUE_AREA) {
                    found = currentPath;
                    break outer;
                }

                Index neighbor = new Index(ny, nx);
                if (seen.contains(neighbor)) {
                    continue;
                }

                Index[] neighboringPath = new Index[currentPath.length + 1];
                neighboringPath[0] = neighbor;
                for (int i = 1; i < currentPath.length + 1; ++i) {
                    neighboringPath[i] = currentPath[i - 1];
                }
                queue.addLast(neighboringPath);
            }
        }

        if (found == null) return;

        int distance = 0;
        for (Index ind : found) {
            Area area = map[ind.y()][ind.x()];
            distance += area.getDistance();
        }

        double angle = (180.0 / Math.PI) * Math.atan2(found[found.length - 1].y() - found[0].y(), found[0].x() - found[found.length - 1].x());
        angle += 360.0 * 4;
        angle %= 360.0;

        System.out.println(angle);

        String angleDirection = getDirection(angle);
        headDirectionLabel.setText("Nearest: " + distance + "km" + (angleDirection == null ? "" : ", " + angleDirection));
    }

    private static String getDirection(double angle) {
        String angleDirection = null;
        if (angle <= 22.5) angleDirection = "E";
        else if (22.5 <= angle && angle <= 67.5) angleDirection = "NE";
        else if (67.5 <= angle && angle <= 112.5) angleDirection = "N";
        else if (112.5 <= angle && angle <= 157.5) angleDirection = "NW";
        else if (157.5 <= angle && angle <= 202.5) angleDirection = "W";
        else if (202.5 <= angle && angle <= 247.5) angleDirection = "SW";
        else if (247.5 <= angle && angle <= 292.5) angleDirection = "S";
        else if (292.5 <= angle && angle <= 337.5) angleDirection = "SE";
        else angleDirection = "E";

        return angleDirection;
    }

    protected interface KeyHandler {
        void run();
    }

    private void addKeyListener(int key, KeyHandler handler) {
        listeners.put(key, handler);
    }

    private BitmapFont generateFont(String path, int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(path));
        FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        return font;
    }

//    Waits for the next minute and checks if the player met the condition for winning and losing
    private void checkForWinLoseConditions(){
        Area area = map[player.getPosition().y()][player.getPosition().x()];
        if ((0< player.getMinutes()|| minutes < player.getMinutes()) && player.getTimeSinceLastSecond() == 0) {
//            TODO: Create text screens for lose and win screens
//            Check for lost condition
            if (player.getCondition() < 5) {
//                Calls lose ending screen
                gameEndingScene = 1;
                  isGameDone = true;
            }
//            Checks win condition;
//            TODO: Check and Change Formula

//            Checks for win condition
            else if(Utils.random.nextDouble() < area.getRescueProbability()){
//                Calls win ending screen
                gameEndingScene = 2;
                isGameDone = true;
            }

            minutes = player.getMinutes();
        }
    }

    private String displayItemsForMessage(List<String> items) {
        if (items.size() <= 0) {
            return "You didn't find anything useful.";
        }

        StringBuilder builder = new StringBuilder();
        HashMap<String, Integer> counter = new HashMap<>();
        for (String id : items) {
            counter.put(id, counter.getOrDefault(id, 0) + 1);
        }

        List<Map.Entry<String, Integer>> sortedEntries = counter.entrySet()
                .stream()
                .sorted((a, b) -> a.getKey().compareTo(b.getKey()))
                .toList();

        for (Map.Entry<String, Integer> entry : sortedEntries) {
            Item item = Item.of(entry.getKey());

            builder.append(item.name())
                    .append(" (")
                    .append(entry.getValue())
                    .append(")");
            if (entry != sortedEntries.getLast()) {
                builder.append("\n");
            }
        }

        return builder.toString();
    }
    private void checkForStructureEvents(){
//        If a fish basket trap is set up, chance for player to catch fishes
        if (0.30 < Utils.random.nextDouble()) {
            if (fishBasketTrap.isBuilt() && 0 < fishBasketTrap.getBaitRemaining()) {
                fishBasketTrap.collect(player);
                showDialogScreen(new DialogScreen("Your trapped went off","You caught some fishes"));
            }
        }
//        If a campfire is set up, chance for passive regen of condition <3
        if (0.60 < Utils.random.nextDouble()){
            if (campfire.isBuilt() && 0 < campfire.getSecondsRemaining()) {
                player.setCondition(player.getCondition() + 5);
            }
        }
//       If a deadfall trap is set up, chance for player to catch small animals
        if (0.30 < Utils.random.nextDouble()) {
            if (deadfallTrap.isBuilt() && 0 < deadfallTrap.getBaitRemaining()) {
                deadfallTrap.collect(player);
                showDialogScreen(new DialogScreen("Your trapped went off","A small bird was caught in your trap"));
            }
        }
    }
    private void tryForAmbienceTrack() {
        System.out.println(player.getMinutes() % 24);
        switch (Utils.random.nextInt(1,6)){
            case 1:
                music.stop();
                music = Gdx.audio.newMusic(Gdx.files.internal("music/ambience_1.mp3"));
                music.setVolume(0.8f);
                music.play();
                break;
            case 2:
                music.stop();
                music = Gdx.audio.newMusic(Gdx.files.internal("music/ambience_2.mp3"));
                music.setVolume(0.8f);
                music.play();
                break;
            case 3:
                music.stop();
                music = Gdx.audio.newMusic(Gdx.files.internal("music/ambience_3.mp3"));
                music.setVolume(0.8f);
                music.play();
                break;
            case 4:
                music.stop();
                music = Gdx.audio.newMusic(Gdx.files.internal("music/ambience_4.mp3"));
                music.setVolume(0.8f);
                music.play();
                break;
            default:
                break;
        }
    }
    private  void tryForEndCondition(){
        if(isGameDone){
            music.stop();
            runSerializingThread.set(false);
            game.setScreen(new GameOverScreen(game, this));
        }
    }
    private void tryForSpoilage(){
        switch(Utils.random.nextInt(1,5))
        {
            case 1: {
                if (player.getInventory().contains("raw_avian")){
                    player.getInventory().remove("raw_avian");
                    player.getInventory().add("rotten_meat");
                }
                break;
            }
            case 2: {
                if (player.getInventory().contains("raw_fish")){
                    player.getInventory().remove("raw_fish");
                    player.getInventory().add("rotten_meat");
                }
                break;
            }
            case 3:
                if (player.getInventory().contains("cooked_avian")){
                    player.getInventory().remove("cooked_avian");
                    player.getInventory().add("rotten_meat");
                }
                break;
            case 4:
                if (player.getInventory().contains("cooked_fish")){
                    player.getInventory().remove("cooked_fish");
                    player.getInventory().add("rotten_meat");
                }
        }
    }

    public void move(Direction direction) {
        player.move(direction);
        campfire = new Campfire();
        fishBasketTrap = new FishBasketTrap();

        Index position = player.getPosition();
        Area area = map[position.y()][position.x()];

        loadBackgrounds(area.getType().getBackgroundFolders());
        updateNearestLocation();
    }

    public void showLoadingScreen(String title, String message) {
        showLoadingScreen(new LoadingScreen(this, message, new DialogScreen(title, message)));
    }

    public void showLoadingScreen(LoadingScreen loadingScreen) {
        this.loadingScreen = loadingScreen;
        stage.addActor(loadingScreen);
    }

    public void showDialogScreen(String title, String message){
        dialogScreen = new DialogScreen(title, message);
        stage.addActor(dialogScreen);
    }

    public void showDialogScreen(DialogScreen dialogScreen){
        this.dialogScreen = dialogScreen;
        stage.addActor(dialogScreen);
    }

    public int getItemsCrafted() {
        return itemsCrafted;
    }

    public void setItemsCrafted(int itemsCrafted){
        this.itemsCrafted = itemsCrafted;
    }


    public Stage getStage() {
        return stage;
    }

    public Campfire getCampfire() {
        return campfire;
    }

    public FishBasketTrap getFishTrap() {
        return fishBasketTrap;
    }

    public DeadfallTrap getSmallAnimalTrap() {
        return deadfallTrap;
    }

    public int getDistanceTravelled() {
        return distanceTravelled;
    }

    public void setDistanceTravelled(int distanceTravelled) {
        this.distanceTravelled = distanceTravelled;
    }

    public int getInjuriesFaced() {
        return injuriesFaced;
    }

    public void setInjuriesFaced(int injuriesFaced) {
        this.injuriesFaced = injuriesFaced;
    }

    public int getInjuriesTreated() {
        return injuriesTreated;
    }

    public void setInjuriesTreated(int injuriesTreated) {
        this.injuriesTreated = injuriesTreated;
    }

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }
}