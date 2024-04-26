package com.teammerge.abandoned.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.teammerge.abandoned.AbandonedGame;
import com.teammerge.abandoned.actors.drawables.BackgroundDrawable;
import com.teammerge.abandoned.actors.tables.*;
import com.teammerge.abandoned.entities.Campfire;
import com.teammerge.abandoned.entities.FishBasketTrap;
import com.teammerge.abandoned.entities.Player;
import com.teammerge.abandoned.entities.DeadfallTrap;
import com.teammerge.abandoned.enums.Direction;
import com.teammerge.abandoned.records.Index;
import com.teammerge.abandoned.utilities.InsertionSort;
import com.teammerge.abandoned.utilities.wfc.classes.Area;
import com.teammerge.abandoned.utilities.wfc.classes.MapCollapse;
import com.teammerge.abandoned.utilities.wfc.enums.AreaType;

import java.util.*;


public class GameScreen implements Screen {

    final AbandonedGame game;
    final SpriteBatch batch;
    final Stage stage;
    final GameScreen self;
    OrthographicCamera camera;
    BitmapFont mediumFont, regular, lightFont;

    public BackgroundDrawable day, dusk, night, midnight;

    private final HashSet<Class<?>> activeScreens = new HashSet<>();
    private final int mapWidth;
    private final int mapHeight;

    LoadingScreen loadingScreen;
    DialogScreen dialogScreen;
    ProgressBar conditionBar, fullnessBar, hydrationBar, energyBar;
    Label conditionLabel, fullnessLabel, hydrationLabel, energyLabel;
    Label currentLocationLabel;
    Label debugMilisecondCounterLabel, daysPassedLabel, hoursBeforeNextPhaseLabel;

    Table containerTable;
    Player player;
    Campfire campfire;
    FishBasketTrap fishBasketTrap;
    DeadfallTrap deadfallTrap;
    MapCollapse mapGenerator;
    Area[][] map;

    Random random;

    boolean isInTransition, isGameDone;
    int minutes;


    public GameScreen(final AbandonedGame game, int mapWidth, int mapHeight) {
        // Load camera, stage, and assets
        self = this;
        this.game = game;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 800);
        stage = new Stage(new ScreenViewport());
        loadingScreen = new LoadingScreen();
        dialogScreen = new DialogScreen();
        isInTransition = false;
        isGameDone = false;
        random = new Random();

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
        containerTable.setSize(1280, 800);

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

    }

    private void loadBackgrounds() {
        day = new BackgroundDrawable("images/backgrounds/day.png");
        dusk = new BackgroundDrawable("images/backgrounds/day.png");
        night = new BackgroundDrawable("images/backgrounds/night.png");
        midnight = new BackgroundDrawable("images/backgrounds/midnight.png");
    }

    private void loadBackgrounds(String folder) {
        day = new BackgroundDrawable("images/backgrounds/" + folder + "/day.png");
        dusk = new BackgroundDrawable("images/backgrounds/" + folder + "/dusk.png");
        night = new BackgroundDrawable("images/backgrounds/" + folder + "/night.png");
        midnight = new BackgroundDrawable("images/backgrounds/" + folder + "/midnight.png");
    }

    public Area[][] getMap() {
        return this.map;
    }

    public HashSet<Class<?>> getActiveScreens() {
        return this.activeScreens;
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

            if (player.getMinutes() % 6 == 0 && player.getTimeSinceLastSecond() == 0) {
                checkForStructureEvents();
            }

            if(isGameDone){
                game.setScreen(new GameOverScreen(game, mapWidth, mapHeight));
            }
        };


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
        Texture white = new Texture(pixmap);
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

        // Create Table
        Table table = new Table();

        // Create Rest Button that opens up RestScreen
        VisTextButton restButton = new VisTextButton("Rest");
        restButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RestScreen overlay = new RestScreen(player,self);
                stage.addActor(overlay);
                overlay.setVisible(true);
            }
        });

        /// TRAVEL BUTTON IMPLEMENTATION.
        VisTextButton travelButton = new VisTextButton("Travel");
        travelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (activeScreens.add(TravelScreen.class)) {
                    TravelScreen overlay = new TravelScreen(player, self);
                    stage.addActor(overlay);
                    overlay.setVisible(true);
                }
            }
        });
        VisTextButton scavengeButton = new VisTextButton("Scavenge");
        scavengeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // player.scavenge();
                Index position = player.getPosition();
                Area area = map[position.y()][position.x()];
                List<String> extractedItems = area.extract();
                DialogScreen dialog = new DialogScreen("Collected", extractedItems.toString());

                switch(random.nextInt(1,4)) {
                    case 1:
//                        If too dark, player has a chance to injure themselves
                        if ((player.getMinutes()  % 24 < 6 || 18 < player.getMinutes() % 24) && !player.getInventory().contains("flashlight")) {
//                            10% Chance of Injury
                            if (random.nextDouble() > 0.85) {
                                player.setCondition(player.getCondition() - random.nextInt(5,11));
                                extractedItems.clear();
                                dialog = new DialogScreen("Scavenge Failed", "It was too dark, and you injured yourself");
                            }
                        }
                        break;
                    case 2:
//                        If in Village, Commercial Bldg, or Mall, might face against dangerous survivors
                        if (area.getType() == AreaType.VILLAGE || area.getType() == AreaType.COMMERCIAL_BLDG || area.getType() == AreaType.MALL) {
//                            10% Chance of getting into a fight
                            if (random.nextDouble() > 0.85) {
                                player.setCondition(player.getCondition() - random.nextInt(5,16));
                                extractedItems.clear();
                                dialog = new DialogScreen("Scavenge Failed", "You've encountered and fought with a violent survivor");
                            }
                        }
                        break;
                    case 3:
//                        If in Forest, Park, Village, Farm, might encounter wild animals
                        if (area.getType() == AreaType.FOREST || area.getType() == AreaType.VILLAGE || area.getType() == AreaType.PARK) {
//                            10% Chance of getting into a fight
                            if (random.nextDouble() > 0.85) {
                                player.setCondition(player.getCondition() - random.nextInt(5,16));
                                extractedItems.clear();
                                dialog = new DialogScreen("Scavenge Failed", "A snake was hiding around the area and bit you.");
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
        VisTextButton inventoryButton = new VisTextButton("Inventory");
        inventoryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                InventoryScreen overlay = new InventoryScreen(player);
                stage.addActor(overlay);
                overlay.setVisible(true);
            }
        });

        VisTextButton campfireButton = new VisTextButton("Build");
        campfireButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                BuildScreen overlay = new BuildScreen(self,player);
                stage.addActor(overlay);
                overlay.setVisible(true);
            }
        });
        VisTextButton craftingButton = new VisTextButton("Craft");
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

                if (hours > 15) {
                    containerTable.setBackground(dusk);
                }


            } else {
                dayCycle = "EVENING, ";
                nextCycle = "MIDNIGHT";
                waitingHours = 24 - hours;
                containerTable.setBackground(night);
            }

        }
        // FIXME:
        daysPassedLabel.setText("DAY " + (player.getMinutes() / 24));
        hoursBeforeNextPhaseLabel.setText(dayCycle + waitingHours + " HOURS BEFORE " + nextCycle);

        // FIXME:
        debugMilisecondCounterLabel.setText("" + player.getTimeSinceLastSecond());
    }

    protected interface KeyHandler {
        void run();
    }

    private final HashSet<Integer> activeKeys = new HashSet<>();

    private final HashMap<Integer, KeyHandler> listeners = new HashMap<>();

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
        if (minutes < player.getMinutes() && player.getTimeSinceLastSecond() == 0) {
//            TODO: Create text screens for lose and win screens
//            Check for lost condition
            if (player.getCondition() < 5) {
//                Calls lose ending screen
                showDialogScreen("You ded, ded as hell", "Skill Issue");
                isGameDone = true;
            }
//            Checks win condition;
//            TODO: Check and Change Formula

//            Checks for win condition
            else if(random.nextDouble() < area.getRescueProbability()){
//                Calls win ending screen
                showDialogScreen("You unfortunately live", "You were spotted by a rescue team");
                isGameDone = true;
            }

            minutes = player.getMinutes();
        }
    }

    public void checkForStructureEvents(){
//        If a trap is set up, chance for player to catch small animals
        if (0.40 < random.nextDouble()) {
            if (fishBasketTrap.isBuilt() && 0 < fishBasketTrap.getBaitRemaining()) {
                fishBasketTrap.collect(player);
                showDialogScreen(new DialogScreen("Your trapped went off","You caught some fishes"));
            }
        }
//        If a campfire is set up, chance for passive regen of condition <3
        if (0.60 < random.nextDouble()){
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

    public void showLoadingScreen(){
        loadingScreen = new LoadingScreen();
        stage.addActor(loadingScreen);
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
}