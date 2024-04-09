package com.teammerge.abandoned.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.teammerge.abandoned.AbandonedGame;
import com.teammerge.abandoned.entities.Player;

public class GameScreen implements Screen {
    Boolean isPaused;
    final AbandonedGame game;
    int row_height, col_width, hours, waitingHours;
    String dayCycle, nextCycle;
    OrthographicCamera camera;
    Label conditionLabel, fullnessLabel, hydrationLabel, energyLabel, debugMilisecondCounterLabel, daysPassedLabel, hoursBeforeNextPhaseLabel;
    BitmapFont font;
    Stage stage;
    SpriteBatch batch;
    Player player;

    public GameScreen(final AbandonedGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());
        font = new BitmapFont();
        player = new Player();
        isPaused = false;
        Gdx.input.setInputProcessor(stage);

        row_height = Gdx.graphics.getHeight() / 16;
        col_width = Gdx.graphics.getWidth() / 16;

        addPlayerAttributeLabels();
        addTimeLabels();

    }

    private Label createLabel(String text, BitmapFont font, Color color) {
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        Label label = new Label(text, labelStyle);
        label.setColor(color);
        return label;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        stage.draw();

        if(Gdx.input.isKeyPressed(Input.Keys.P)){
            isPaused = !isPaused;
        }

        /*
        * Time stuff (Day, Hours til sundown/ sunrise)
        * */

//        TODO: Refactor, Clean-Up Code
        // Adds elapsed milliseconds to player
        // TODO: Current passed tick is 60s
        if(!isPaused) player.tick((long)Math.floor(delta * 1000 * 15));
        if(0 < player.time % 60000 && player.time % 60000 <= 100 ) player.decay();

        hours = (int) (player.time / 60000) % 24;

        // Following conditions check if time is 0AM - 12AM, and 1PM - 11PM

        if(0 <= hours && hours < 12) {
            if (hours < 6 ) {
                dayCycle = "Midnight, ";
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
                }
            else {
                dayCycle = "Evening, ";
                nextCycle = "Midnight";
                waitingHours = 24 - hours;
            }

        }

        conditionLabel.setText("Condition: " + player.condition);
        fullnessLabel.setText("Fullness: " + player.fullness);
        hydrationLabel.setText("Hydration: " + player.hydration);
        energyLabel.setText("Energy: " + player.energy);
        daysPassedLabel.setText((int)(player.time/(24 * 60000)));
        hoursBeforeNextPhaseLabel.setText(dayCycle + waitingHours + " hours till " + nextCycle);
        debugMilisecondCounterLabel.setText(""+ player.time);


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
    private void addPlayerAttributeLabels() {
        conditionLabel = createLabel("Condition: " + player.condition, font, Color.WHITE);
        fullnessLabel = createLabel("Fullness: " + player.fullness,font, Color.WHITE);
        hydrationLabel = createLabel("Hydration: " + player.hydration,font, Color.WHITE);
        energyLabel = createLabel("Energy: " + player.energy,font, Color.WHITE);


        conditionLabel.setPosition(col_width, row_height * 5);
        fullnessLabel.setPosition(col_width, row_height * 4);
        hydrationLabel.setPosition(col_width, row_height * 3);
        energyLabel.setPosition(col_width, row_height * 2);

        stage.addActor(conditionLabel);
        stage.addActor(fullnessLabel);
        stage.addActor(hydrationLabel);
        stage.addActor(energyLabel);
    }

    private void addTimeLabels(){
        daysPassedLabel = createLabel("", font, Color.WHITE);
        daysPassedLabel.setAlignment(Align.right);
        daysPassedLabel.setPosition(Gdx.graphics.getWidth() - col_width, Gdx.graphics.getHeight() - row_height);

        hoursBeforeNextPhaseLabel = createLabel("", font, Color.WHITE);
        hoursBeforeNextPhaseLabel.setAlignment(Align.right);
        hoursBeforeNextPhaseLabel.setPosition(Gdx.graphics.getWidth() - col_width, Gdx.graphics.getHeight() - 2 * row_height);

        debugMilisecondCounterLabel = createLabel("" + (player.time),font, Color.WHITE);
        debugMilisecondCounterLabel.setAlignment(Align.right);
        debugMilisecondCounterLabel.setPosition(Gdx.graphics.getWidth() - col_width, Gdx.graphics.getHeight() - 3 * row_height);

        stage.addActor(debugMilisecondCounterLabel);
        stage.addActor(daysPassedLabel);
        stage.addActor(hoursBeforeNextPhaseLabel);
    }
}
