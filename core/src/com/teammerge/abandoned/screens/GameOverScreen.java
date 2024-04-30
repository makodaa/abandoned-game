package com.teammerge.abandoned.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.teammerge.abandoned.AbandonedGame;

import java.util.ArrayList;
import java.util.List;

public class GameOverScreen implements Screen {

    private final AbandonedGame game;
    private final BitmapFont font;
    private final List<String> displayLeftTexts, displayRightTexts, displayCentralTexts;
    private int currentRightLetterIndex = 0;
    private int currentLeftLetterIndex = 0;
    private int currentCentralLetterIndex = 0;
    private boolean textComplete = false;
    private boolean displayingLeftText;
    private boolean displayingCentralText;

    GameScreen gameScreen;

    public GameOverScreen(AbandonedGame game, GameScreen gameScreen) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/RobotoCondensed-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 36;
        font = generator.generateFont(parameter);
        generator.dispose();
        int gameEndingScene = gameScreen.gameEndingScene;
        int daysPassed = gameScreen.daysPassed;
        int itemsCollected = gameScreen.itemsCollected;
        int itemsCrafted = gameScreen.itemsCrafted;
        int distanceTravelled = gameScreen.getDistanceTravelled();
        int injuriesFaced = gameScreen.getInjuriesFaced();
        int injuriesTreated = gameScreen.getInjuriesTreated();
        this.game = game;



        displayLeftTexts = new ArrayList<>();
        displayRightTexts = new ArrayList<>();
        displayCentralTexts = new ArrayList<>();

        // TODO: Modify the text

        String gameEndText;
        if(gameEndingScene == 1){
            gameEndText = "Unfortunately, you suffered due to your worsening condition and died.";
        } else {
            gameEndText = "Success! A rescue helicopter sees you and are now successfully rescued";
        }
        displayCentralTexts.add("Congratulations!\n"+ gameEndText);
        displayCentralTexts.add("RAWR");

        displayLeftTexts.add(
                gameEndingScene == 1 ? "Better Luck Next Time!" : """
                        You Survived!

                        Days Survived\s

                        Distance Travelled

                        Items Gathered

                        Items Crafted

                        Injuries Faced

                        Injuries Treated""");


        displayRightTexts.add(
                "\n\n"+ daysPassed +
                "\n\n" + distanceTravelled +
                "\n\n" + itemsCollected +
                "\n\n" + itemsCrafted +
                "\n\n" + injuriesFaced +
                "\n\n" + injuriesTreated);
        // Add more texts as needed

        // Start displaying the first text
        displayCurrentText();

        displayingCentralText = true;

        // Set up input listener
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {

                // If both left and right texts are fully displayed, go to the next text or game screen
                if (textComplete && currentRightLetterIndex == displayRightTexts.getFirst().length()) {
                    game.screen.dispose();
                    game.screen = new MainMenuScreen(game);
                    game.setScreen(game.screen);
                } else if (currentCentralLetterIndex == displayCentralTexts.getFirst().length()) {
                    nextText();
                } else if (displayingCentralText) {
                    currentCentralLetterIndex = displayCentralTexts.getFirst().length();
                } else {
                    // Complete the text
                    if (displayingLeftText) {
                        currentLeftLetterIndex = displayLeftTexts.getFirst().length();
                    } else {
                        currentRightLetterIndex = displayRightTexts.getFirst().length();
                    }
                    // Set textComplete to true to trigger the next step
                    textComplete = true;
                }
                return true;
            }
        });
    }

    @Override
    public void show() {
    }

    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        font.draw(game.batch,
                displayCentralTexts.getFirst().substring(0, currentCentralLetterIndex),
                Gdx.graphics.getWidth() / 2f,
                Gdx.graphics.getHeight() / 2f,
                0,
                Align.center,
                false);

        // Draw the left text substring up to the current letter index
        font.draw(game.batch,
                displayLeftTexts.getFirst().substring(0, currentLeftLetterIndex),
                100,
                Gdx.graphics.getHeight() - 150,
                0,
                Align.left,
                false);

        // Draw the right text substring up to the current letter index
        font.draw(game.batch,
                displayRightTexts.getFirst().substring(0, currentRightLetterIndex),
                Gdx.graphics.getWidth() - 100,
                Gdx.graphics.getHeight() - 150,
                0,
                Align.right,
                false);

        game.batch.end();
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
        font.dispose();
    }

    private void displayCurrentText() {
        // Start displaying the current text with letter delay
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if(displayingCentralText){
                    if(currentCentralLetterIndex < displayCentralTexts.getFirst().length()){
                        currentCentralLetterIndex++;
                    }
                } else if (displayingLeftText) {
                    if (currentLeftLetterIndex < displayLeftTexts.getFirst().length()) {
                        currentLeftLetterIndex++;
                    } else {
                        // If all letters are displayed, mark textComplete as true
                        displayingLeftText = false; // Switch to displaying right text
                        displayCurrentText(); // Start displaying the right text
                    }
                } else {
                    if (currentRightLetterIndex < displayRightTexts.getFirst().length()) {
                        currentRightLetterIndex++;
                    } else {
                        this.cancel();
                    }
                }
            }
        }, 0.75f, 0.05f);
    }

    private void nextText(){
        if (displayingCentralText){
            // If there's another central text to display
            if (displayCentralTexts.size() > 1) {
                // Remove the current central text
                displayCentralTexts.remove(0);
                // Reset index and start displaying the next central text
                currentCentralLetterIndex = 0;
                displayCurrentText();
            } else {
                // If there are no more central texts, switch to displaying left text
                currentCentralLetterIndex = 0;
                displayingLeftText = true;
                displayingCentralText = false;
                displayCurrentText();
            }
        }
    }

}