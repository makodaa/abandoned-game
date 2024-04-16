package com.teammerge.abandoned.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.VisImageTextButton;
import com.teammerge.abandoned.AbandonedGame;
import com.teammerge.abandoned.actors.Background;
import com.teammerge.abandoned.actors.InventoryScreen;
import com.teammerge.abandoned.actors.RestScreen;
import com.teammerge.abandoned.entities.Player;
import com.teammerge.abandoned.enums.Direction;
import com.teammerge.abandoned.records.Index;
import com.teammerge.abandoned.utilities.wfc.classes.Area;
import com.teammerge.abandoned.utilities.wfc.classes.MapCollapse;
import com.kotcrab.vis.ui.widget.VisProgressBar;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static java.util.Map.entry;

public class GameScreen implements Screen {

    final AbandonedGame game;
    final SpriteBatch batch;
    final Stage stage;

    // TODO: Implement fonts for various labels
    BitmapFont font;

    Background colored,shadow;

    final int row_height = Gdx.graphics.getHeight() / 16;
    final int col_width = Gdx.graphics.getWidth() / 16;
    boolean isPaused = false;

    VisProgressBar conditionBar, fullnessBar, hydrationBar, energyBar;
    Label conditionLabel, fullnessLabel, hydrationLabel, energyLabel;
    Label currentLocationLabel;
    Label debugMilisecondCounterLabel, daysPassedLabel, hoursBeforeNextPhaseLabel;
    Image craftButtonSkin, scavengeButtonSkin, restButtonSkin, inventoryButtonSkin, travelButtonSkin;

    Table containerTable, shadowOverlayTable;
    Player player;

    MapCollapse mapGenerator;
    Area[][] map;


    protected interface KeyHandler {
        void run();
    }

    private final HashSet<Integer> activeKeys = new HashSet<>();

    private final HashMap<Integer, KeyHandler> listeners = new HashMap<>() {{
        put(Input.Keys.W, () -> {
            System.out.println("W");
        });
    }};

    private void addKeyListener(int key, KeyHandler handler) {
        listeners.put(key, handler);
    }

    public GameScreen(final AbandonedGame game, int mapWidth, int mapHeight) {

        // Load camera, stage, and assets
        this.game = game;
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());
        font = new BitmapFont();
        colored = new Background("images/plain_white_background.png");
        shadow = new Background("images/plain_white_background.png");

        // Create Instance of Player and Map
        player = new Player(new Index(mapWidth / 2, mapHeight / 2));
        mapGenerator = new MapCollapse();
        map = mapGenerator.generateMap(mapWidth, mapHeight);

        // Set up Main table and Actors
        containerTable = new Table();
        containerTable.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        colored.setColor(88, 163, 153, 255);
        containerTable.setBackground(colored);

        shadowOverlayTable = new Table();
        shadowOverlayTable.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shadow.setColor(0,0,0, 100);
        shadowOverlayTable.setBackground(shadow);

        Table timeAreaTable = createTimeAreaTable();
        Table attributesTable = createAttributesTable();
        Table actionButtonsTable = createActionButtonsTable();

        containerTable.add(shadowOverlayTable);
        containerTable.row().expandX().fillX();
        containerTable.align(Align.topRight);
        containerTable.add(timeAreaTable).fillX().fillY();
        containerTable.row().expandX().fillX();
        containerTable.add(actionButtonsTable);
        containerTable.row().expandX().fillX();
        containerTable.align(Align.bottomLeft);
        containerTable.add(attributesTable);

        stage.addActor(containerTable);
        Gdx.input.setInputProcessor(stage);

        addKeyListener(Input.Keys.W, () -> {
            player.move(Direction.UP);
        });
        addKeyListener(Input.Keys.A, () -> {
            player.move(Direction.DOWN);
        });
        addKeyListener(Input.Keys.S, () -> {
            player.move(Direction.LEFT);
        });
        addKeyListener(Input.Keys.D, () -> {
            player.move(Direction.RIGHT);
        });

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();

        if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            isPaused = !isPaused;
        }

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

        // FIXME: Current clock speed x 15 for testing purposes, change back to 1
        if (!isPaused)
            player.tick(delta * 1000 * 15);
        // Checks


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
        Table subTable1 = new Table();
        Table subTable2 = new Table();
        Table subTable3 = new Table();
        Table subTable4 = new Table();

        conditionLabel = createLabel("Condition: " , font, Color.WHITE);
        fullnessLabel = createLabel("Fullness: " , font, Color.WHITE);
        hydrationLabel = createLabel("Hydration: " , font, Color.WHITE);
        energyLabel = createLabel("Energy: " , font, Color.WHITE);

        conditionBar = new VisProgressBar(0, 100, 1, false);
        conditionBar.setColor(Color.GREEN);
        fullnessBar = new VisProgressBar(0, 100, 1, false);
        fullnessBar.setColor(1, 0, 0, 1);
        hydrationBar = new VisProgressBar(0, 100, 1, false);
        hydrationBar.setColor(Color.WHITE);
        energyBar = new VisProgressBar(0, 100, 1, false);
        energyBar.setColor(Color.ORANGE);



        padTable(table);
        table.row().expandX().fillX();

        table.add(subTable1);
        table.add(subTable2);
        table.add(subTable3);
        table.add(subTable4);

        subTable1.add(conditionLabel).fillX();
        subTable1.row().fillX();
        subTable1.add(conditionBar);

        subTable2.add(fullnessLabel).fillX();
        subTable2.row().fillX();
        subTable2.add(fullnessBar);

        subTable3.add(hydrationLabel).fillX();
        subTable3.row().fillX();
        subTable3.add(hydrationBar);

        subTable4.add(energyLabel).fillX();
        subTable4.row().fillX();
        subTable4.add(energyBar);

        return table;
    }

    private Table createTimeAreaTable() {
        Table table = new Table();

        currentLocationLabel = createLabel("Location", font, Color.WHITE);
        currentLocationLabel.setAlignment(Align.right);

        daysPassedLabel = createLabel("Day n", font, Color.WHITE);
        daysPassedLabel.setAlignment(Align.right);

        hoursBeforeNextPhaseLabel = createLabel("Lorem Ipsum", font, Color.WHITE);
        hoursBeforeNextPhaseLabel.setAlignment(Align.right);

        debugMilisecondCounterLabel = createLabel("00000", font, Color.WHITE);
        debugMilisecondCounterLabel.setAlignment(Align.right);

        padTable(table);
        table.row().expandX().fillX();
        table.add(currentLocationLabel);
        table.row().expandX().fillX();
        table.add(daysPassedLabel);
        table.row().expandX().fillX();
        table.add(hoursBeforeNextPhaseLabel);
        table.row().expand().fillX();
        table.add(debugMilisecondCounterLabel);

        return table;
    }

    public Table createActionButtonsTable() {

        // ewan ko balat ng button ata to
        restButtonSkin = new Image(new Texture(Gdx.files.internal("images/sleep.png")));
        travelButtonSkin = new Image(new Texture(Gdx.files.internal("images/move.png")));
        scavengeButtonSkin = new Image(new Texture(Gdx.files.internal("images/axe.png")));
        inventoryButtonSkin = new Image(new Texture(Gdx.files.internal("images/inventory.png")));
        craftButtonSkin = new Image(new Texture(Gdx.files.internal("images/craft.png")));


        // Create Table
        Table table = new Table();

        // Create Rest Button that opens up RestScreen
        VisImageTextButton restButton = new VisImageTextButton("", restButtonSkin.getDrawable());
        restButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RestScreen overlay = new RestScreen(player);
                stage.addActor(overlay);
                overlay.setVisible(true);
            }
        });
        VisImageTextButton travelButton = new VisImageTextButton("", travelButtonSkin.getDrawable());
        travelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

            }
        });
        VisImageTextButton scavengeButton = new VisImageTextButton("", scavengeButtonSkin.getDrawable());
        VisImageTextButton inventoryButton = new VisImageTextButton("", inventoryButtonSkin.getDrawable());
        inventoryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                InventoryScreen overlay = new InventoryScreen(player);
                stage.addActor(overlay);
                overlay.setVisible(true);
            }
        });
        VisImageTextButton craftingButton = new VisImageTextButton("", craftButtonSkin.getDrawable());

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

        return table;
    }

    private void padTable(Table table) {
        table.padBottom(Gdx.graphics.getHeight() / 16.0f);
        table.padTop(Gdx.graphics.getHeight() / 16.0f);
        table.padLeft(Gdx.graphics.getWidth() / 20.0f);
        table.padRight(Gdx.graphics.getWidth() / 20.0f);
    }

    private void updateLocationGraphics() {
        Area area = map[player.position.y()][player.position.x()];
        currentLocationLabel.setText("Location: " + area.getName() + " " + player.position);
    }

    private void updateAttributeGraphics() {
        conditionBar.setValue( player.condition);
        fullnessBar.setValue( player.fullness);
        hydrationBar.setValue(player.hydration);
        energyBar.setValue( player.energy);
    }

    private void updateDiurnalCycleGraphics() {
        // FIXME
        int hours = player.minutes % 24;
        int waitingHours;
        String dayCycle, nextCycle;

        // Following conditions check if time is 0AM - 12AM, and 1PM - 11PM
        if (0 <= hours && hours < 12) {
            if (hours < 6) {
                dayCycle = "Early Morning, ";
                nextCycle = "Sunrise";
                waitingHours = 6 - hours;
                colored.setColor(64, 31, 113,((hours / 4.0f) * 255));
                containerTable.setBackground(colored);
            } else {
                dayCycle = "Morning, ";
                nextCycle = "Noon";
                waitingHours = 12 - hours;

                colored.setColor(19, 93, 102,((hours / 6.0f) * 255));
                containerTable.setBackground(colored);
            }
        } else {
            if (hours < 18) {
                dayCycle = "Afternoon, ";
                nextCycle = "Sunset";
                waitingHours = 18 - hours;

                if (hours > 15) {
                    colored.setColor(130, 77, 116, ((waitingHours / 8.0f) * 255));
                    containerTable.setBackground(colored);
                }


            } else {
                dayCycle = "Evening, ";
                nextCycle = "Midnight";
                waitingHours = 24 - hours;
                colored.setColor(64, 31, 113,((waitingHours / 10.0f) * 255));
                containerTable.setBackground(colored);
            }

        }
        // FIXME:
        daysPassedLabel.setText("Day " + (player.minutes / 24));
        hoursBeforeNextPhaseLabel.setText(dayCycle + waitingHours + " hours till " + nextCycle);

        // FIXME:
        debugMilisecondCounterLabel.setText("" + player.timeSinceLastSecond);
    }

}