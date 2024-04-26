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
import com.teammerge.abandoned.entities.Player;
import com.teammerge.abandoned.records.Index;

import java.util.ArrayList;
import java.util.List;

public class GameOverScreen implements Screen {

    private final AbandonedGame game;
    private final BitmapFont font;
    private final List<String> displayLeftTexts, displayRightTexts;
    private int currentTextIndex = 0;
    private int currentLetterIndex = 0;
    private int currentRightLetterIndex = 0;
    private int currentLeftLetterIndex = 0;
    private boolean textComplete = false;
    private boolean displayingLeftText;

    Player player;

    public GameOverScreen(AbandonedGame game, int mapWidth, int mapHeight) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/RobotoCondensed-Light.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24;
        font = generator.generateFont(parameter);
        generator.dispose();

        this.game = game;
        player = new Player(new Index(mapWidth / 2, mapHeight / 2));

        displayLeftTexts = new ArrayList<>();
        displayRightTexts = new ArrayList<>();

        // TODO: Modify the text
        displayLeftTexts.add(
                "Days Survived " +
                        "\n\nDistance Travelled " +
                        "\n\nItems Gathered" +
                        "\n\nItems Crafted" +
                        "\n\nInjuries Faced" +
                        "\n\nInjuries Treated");

        displayRightTexts.add("0" +
                "\n\n" + "0" +
                "\n\n" + "0" +
                "\n\n" + "0" +
                "\n\n" + "0" +
                "\n\n" + "0");
        // Add more texts as needed

        // Start displaying the first text
        displayCurrentText();

        displayingLeftText = true;

        // Set up input listener
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                // If text is fully displayed, go to the next text or game screen
                if (textComplete) {
                    nextTextOrGame();
                } else {
                    /// complete the text
                    if(displayingLeftText){
                        currentLetterIndex = displayLeftTexts.get(currentTextIndex).length();
                    } else {
                        currentLetterIndex = displayRightTexts.get(currentTextIndex).length();
                    }
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
        // Draw the left text substring up to the current letter index
        font.draw(game.batch,
                displayLeftTexts.get(currentTextIndex).substring(0, currentLeftLetterIndex),
                100,
                Gdx.graphics.getHeight() - 150,
                0,
                Align.left,
                false);

        // Draw the right text substring up to the current letter index
        font.draw(game.batch,
                displayRightTexts.get(currentTextIndex).substring(0, currentRightLetterIndex),
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
                if (displayingLeftText) {
                    if (currentLeftLetterIndex < displayLeftTexts.get(currentTextIndex).length()) {
                        currentLeftLetterIndex++;
                    } else {
                        // If all letters are displayed, mark textComplete as true
                        textComplete = true;
                        displayingLeftText = false; // Switch to displaying right text
                        currentRightLetterIndex = 0; // Reset letter index for the next text
                        displayCurrentText(); // Start displaying the right text
                    }
                } else {
                    if (currentRightLetterIndex < displayRightTexts.get(currentTextIndex).length()) {
                        currentRightLetterIndex++;
                    } else {
                        // If all letters are displayed, mark textComplete as true
                        textComplete = true;
                        this.cancel();
                    }
                }
            }
        }, 0, 0.2f);
    }

    private void nextTextOrGame() {
        currentTextIndex++;
        if (currentTextIndex < displayLeftTexts.size()) {
            // If there are more texts to display, reset currentLetterIndex and start displaying the next text.
            currentLetterIndex = 0;
            textComplete = false;
            displayCurrentText();
        } else {
            // If all texts are displayed, transition to the game screen
            game.setScreen(new MainMenuScreen(game));
        }
    }

}