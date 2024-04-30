package com.teammerge.abandoned.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.teammerge.abandoned.AbandonedGame;
import com.teammerge.abandoned.actors.drawables.BackgroundDrawable;
import com.teammerge.abandoned.actors.tables.*;
import com.teammerge.abandoned.entities.Campfire;
import com.teammerge.abandoned.entities.DeadfallTrap;
import com.teammerge.abandoned.entities.FishBasketTrap;
import com.teammerge.abandoned.entities.Player;
import com.teammerge.abandoned.entities.DeadfallTrap;
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
import java.util.*;
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
    transient Label debugMilisecondCounterLabel, daysPassedLabel, hoursBeforeNextPhaseLabel;

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
    int minutes, gameEndingScene, daysPassed, itemsCollected, itemsCrafted, distanceTravelled, injuriesFaced, injuriesTreated;

    transient private HashSet<Integer> activeKeys = new HashSet<>();
    transient private HashMap<Integer, KeyHandler> listeners = new HashMap<>();

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



        return serialized;
    }

    public GameScreen(final AbandonedGame game, int mapWidth, int mapHeight) {
        /// Load the serializing thread
        serializingThread = createSerializingThread();

        // Load camera, stage, and assets
        this.game = game;
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

        activeKeys = new HashSet<>();
        listeners = new HashMap<>();

        /// FIXME: Remove this for release.
        addKeyListener(Input.Keys.Q, () -> {
            map = mapGenerator.generateMap(mapWidth, mapHeight);
            loadBackgrounds(map[player.getPosition().y()][player.getPosition().x()].getType().getBackgroundFolders());
        });

    }

    private Thread createSerializingThread() {
        GameScreen self = this;
        runSerializingThread = new AtomicBoolean();
        runSerializingThread.set(true);

        Thread thread = new Thread(() -> {
            while (runSerializingThread.get()) {
                String path = Gdx.files.internal("saves/save_file.txt").path();
                try (FileOutputStream fileOutputStream = new FileOutputStream(path);
                        ObjectOutputStream objectOutputStream  = new ObjectOutputStream(fileOutputStream)) {
                        objectOutputStream.writeObject(self);
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
//            TODO: uncomment
            checkForWinLoseConditions();



            if (player.getMinutes() % 6 == 0 && player.getTimeSinceLastSecond() == 0) {
                checkForStructureEvents();
            }

            if(isGameDone){
                game.setScreen(new GameOverScreen(game, this));
            }
        }


        updateAttributeGraphics();
        updateLocationGraphics();
        updateDiurnalCycleGraphics();
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

        debugMilisecondCounterLabel = createLabel(" ", mediumFont, Color.WHITE);
        debugMilisecondCounterLabel.setAlignment(Align.right);

        table.align(Align.topRight);
        table.add(locationDaysGroupTable).right();
        table.row();
        table.add(hoursBeforeNextPhaseLabel).right();
        table.row();
        table.add(debugMilisecondCounterLabel).right();

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
                DialogScreen dialog = new DialogScreen("Collected", displayItemsForMessage(extractedItems));
                switch(Utils.random.nextInt(1,4)) {
                    case 1:
//                        If too dark, player has a chance to injure themselves
                        int minuteLeftover = player.getMinutes() % 24;
                        if (!(6 <= minuteLeftover && minuteLeftover <= 18) &&
                                !player.getInventory().contains("flashlight")) {
//                            10% Chance of Injury
                            if (Utils.random.nextDouble() > 0.85) {
                                player.setCondition(player.getCondition() - Utils.random.nextInt(5,11));
                                extractedItems.clear();
                                dialog = new DialogScreen("Scavenge Failed", "It was too dark, and you injured yourself");
                                setInjuriesFaced(getInjuriesTreated() + 1);
                            }
                        }
                        break;
                    case 2:
//                        If in Village, Commercial Bldg, or Mall, might face against dangerous survivors
                        switch (area.getType()) {
                            case AreaType.VILLAGE, AreaType.COMMERCIAL_BLDG, AreaType.MALL:
//                            10% Chance of getting into a fight
                            if (Utils.random.nextDouble() > 0.85) {
                                player.setCondition(player.getCondition() - Utils.random.nextInt(5,16));
                                extractedItems.clear();
                                dialog = new DialogScreen("Scavenge Failed", "You've encountered and fought with a violent survivor");
                                setInjuriesFaced(getInjuriesFaced() + 1);
                            }
                            break;
                        }
                        break;
                    case 3:
//                        If in Forest, Park, Village, Farm, might encounter wild animals
                        switch (area.getType()) {
                            case AreaType.FOREST,  AreaType.VILLAGE,  AreaType.PARK:
//                            10% Chance of getting into a fight
                            if (Utils.random.nextDouble() > 0.85) {
                                player.setCondition(player.getCondition() - Utils.random.nextInt(5,16));
                                extractedItems.clear();
                                dialog = new DialogScreen("Scavenge Failed", "A snake was hiding around the area and bit you.");
                                setInjuriesFaced(getInjuriesFaced() + 1);
                            }
                        }
                        break;
                }

                showLoadingScreen(new LoadingScreen(self,"Looking around for materials...", dialog));

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
        // FIXME:

        daysPassedLabel.setText("DAY " + daysPassed);

        hoursBeforeNextPhaseLabel.setText(dayCycle + waitingHours + " HOURS BEFORE " + nextCycle);

        // FIXME:
        debugMilisecondCounterLabel.setText("" + player.getTimeSinceLastSecond());
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
            System.out.println(area.getRescueProbability());
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

    public void checkForStructureEvents(){
//        If a trap is set up, chance for player to catch small animals
        if (0.40 < Utils.random.nextDouble()) {
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
    }

    public void move(Direction direction) {
        player.move(direction);
        campfire = new Campfire();
        fishBasketTrap = new FishBasketTrap();

        Index position = player.getPosition();
        Area area = map[position.y()][position.x()];

        loadBackgrounds(area.getType().getBackgroundFolders());
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

    public void setItemsCrafted(){
        this.itemsCrafted++;
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
}