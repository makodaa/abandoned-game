package com.teammerge.abandoned.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.teammerge.abandoned.AbandonedGame;
import com.teammerge.abandoned.entities.Player;
import com.teammerge.abandoned.actors.Background;
import com.teammerge.abandoned.actors.RestScreen;
import com.teammerge.abandoned.utilities.wfc.classes.MapCollapse;

public class GameScreen implements Screen {

    final AbandonedGame game;
    final SpriteBatch batch;
    final Stage stage;

    //TODO: Implement fonts for various labels
    BitmapFont font;

    Background background;

    final int row_height = Gdx.graphics.getHeight() / 16;
    final int col_width = Gdx.graphics.getWidth() / 16;
    Boolean isPaused = false;

    Label conditionLabel, fullnessLabel, hydrationLabel, energyLabel;
    Label debugMilisecondCounterLabel, daysPassedLabel, hoursBeforeNextPhaseLabel;

    Table containerTable;
    Player player;

    MapCollapse mapGenerator;
    int[][] map;
    int[][] rescueProbabilityMatrix;


    public GameScreen(final AbandonedGame game, int mapWidth, int mapHeight) {

        // Load camera, stage, and assets
        this.game = game;
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());
        font = new BitmapFont();
        background = new Background("images/plain_white_background.png");

        // Create Instance of Player and Map
        player = new Player();
        mapGenerator = new MapCollapse();
        map = mapGenerator.generateMap(mapWidth,mapHeight);
        rescueProbabilityMatrix = mapGenerator.generateRescueProbabilityMatrix(map);

        // Set up Main table and Actors
        containerTable = new Table();
        containerTable.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        background.setColor(88, 163, 153,255);
        containerTable.setBackground(background);
        Table timeAreaTable = createTimeAreaTable();
        Table attributesTable = createAttributesTable();
        Table actionButtonsTable = createActionButtonsTable();

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
        System.out.println(Gdx.graphics.getWidth() + ", " + Gdx.graphics.getHeight());
    }



    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();

        if(Gdx.input.isKeyPressed(Input.Keys.P)){
            isPaused = !isPaused;
        }

        /*
        * Time stuff (Day, Hours til sundown/ sunrise)
        * */

        // FIXME: Current clock speed x 15 for testing purposes, change back to 1
        if(!isPaused) player.tick(delta * 1000 * 15);
        // Checks

        updateAttributeGraphics();
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

        conditionLabel = createLabel("Condition: " + player.condition, font, Color.WHITE);
        fullnessLabel = createLabel("Fullness: " + player.fullness,font, Color.WHITE);
        hydrationLabel = createLabel("Hydration: " + player.hydration,font, Color.WHITE);
        energyLabel = createLabel("Energy: " + player.energy,font, Color.WHITE);

        padTable(table);
        table.add(conditionLabel).fillX();
        table.row().expandX().fillX();
        table.add(fullnessLabel).fillX();
        table.row().expandX().fillX();
        table.add(hydrationLabel).fillX();
        table.row().expandX().fillX();
        table.add(energyLabel).fillX();
        return table;
    }

    private Table createTimeAreaTable(){
        Table table = new Table();

        daysPassedLabel = createLabel("Day n", font, Color.WHITE);
        daysPassedLabel.setAlignment(Align.right);

        hoursBeforeNextPhaseLabel = createLabel("Lorem Ipsum", font, Color.WHITE);
        hoursBeforeNextPhaseLabel.setAlignment(Align.right);

        debugMilisecondCounterLabel = createLabel("00000",font, Color.WHITE);
        debugMilisecondCounterLabel.setAlignment(Align.right);

        padTable(table);
        table.align(Align.topRight);
        table.row().expandX().fillX();
        table.add(daysPassedLabel).fillX();
        table.row().expandX().fillX();
        table.add(hoursBeforeNextPhaseLabel).fillX();
        table.row().expandX().fillX();
        table.add(debugMilisecondCounterLabel).fillX();

        return table;
    }

    public Table createActionButtonsTable() {
        Table table = new Table();
        TextButton restButton = new VisTextButton("Rest");
        restButton.setSize(96, 96 );
        restButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RestScreen overlay = new RestScreen(player);
                stage.addActor(overlay);
                overlay.setVisible(true);
            }
        });
        padTable(table);
        table.align(Align.topLeft);
        table.add(restButton);
        return table;
    }

    private void padTable(Table table) {
        table.padBottom(Gdx.graphics.getHeight() / 16.0f);
        table.padTop(Gdx.graphics.getHeight() / 16.0f);
        table.padLeft(Gdx.graphics.getWidth() / 20.0f);
        table.padRight(Gdx.graphics.getWidth() / 20.0f);
    }

    private void updateAttributeGraphics(){
        conditionLabel.setText("Condition: " + player.condition);
        fullnessLabel.setText("Fullness: " + player.fullness);
        hydrationLabel.setText("Hydration: " + player.hydration);
        energyLabel.setText("Energy: " + player.energy);
    }

    private void updateDiurnalCycleGraphics() {
        //FIXME
        int hours = player.minutes % 24;
        int waitingHours;
        String dayCycle, nextCycle;

        // Following conditions check if time is 0AM - 12AM, and 1PM - 11PM
        if(0 <= hours && hours < 12) {
            if (hours < 6 ) {
                dayCycle = "Early Morning, ";
                nextCycle = "Sunrise";
                waitingHours = 6 - hours;
            } else {
                dayCycle = "Morning, ";
                nextCycle = "Noon";
                waitingHours = 12 - hours;
            }
        }
        else {
            if (hours < 18)
            {
                dayCycle = "Afternoon, ";
                nextCycle = "Sunset";
                waitingHours = 18 - hours;
                background.setColor(226, 244, 197,255);
                containerTable.setBackground(background);
            }
            else {
                dayCycle = "Evening, ";
                nextCycle = "Midnight";
                waitingHours = 24 - hours;
            }

        }
        //FIXME:
        daysPassedLabel.setText("Day " + (player.minutes/24));
        hoursBeforeNextPhaseLabel.setText(dayCycle + waitingHours + " hours till " + nextCycle);

        //FIXME:
        debugMilisecondCounterLabel.setText(""+ player.timeSinceLastSecond);
    }

}
