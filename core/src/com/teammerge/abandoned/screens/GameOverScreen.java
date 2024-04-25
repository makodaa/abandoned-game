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
    private int currentLeftLetterIndex = 0;
    private int currentRightLetterIndex = 0;
    private boolean textComplete = false;

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

        // Set up input listener
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                // If text is fully displayed, go to the next text or game screen
                if (textComplete) {
                    nextTextOrGame();
                } else {
                    /// complete the text.
                    currentLeftLetterIndex = displayLeftTexts.get(currentTextIndex).length();
                    currentRightLetterIndex = displayRightTexts.get(currentTextIndex).length();
                }
                return true;
            }
        });
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        // Draw the current text substring up to the current letter index
        font.draw(game.batch,
                displayLeftTexts.get(currentTextIndex).substring(0, currentLeftLetterIndex),
                150,
                Gdx.graphics.getHeight() - 150,
                0,
                Align.left,
                false);
        font.draw(game.batch,
                displayRightTexts.get(currentTextIndex).substring(0, currentRightLetterIndex),
                Gdx.graphics.getWidth() - 150,
                Gdx.graphics.getHeight() - 150,
                0,
                Align.right,
                false);
        game.batch.end();
    }

    @Override
    public void resize(int i, int i1) {

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

    // TODO: Modify, make right text display first before left text
    private void displayCurrentText() {
        // Start displaying the current text with letter delay
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (currentLeftLetterIndex < displayLeftTexts.get(currentTextIndex).length()) {
                    currentLeftLetterIndex++;
                    currentRightLetterIndex++;
                } else {
                    // If all letters are displayed, mark textComplete as true
                    textComplete = true;
                    this.cancel();
                }
            }
        }, 0, 0.75f);
    }

    private void nextTextOrGame() {
        currentTextIndex++;
        if (currentTextIndex < displayLeftTexts.size()) {
            // If there are more texts to display, reset currentLetterIndex and start displaying the next text.
            currentLeftLetterIndex = 0;
            currentRightLetterIndex = 0;
            textComplete = false;
            displayCurrentText();
        } else {
            // If all texts are displayed, transition to the Main Menu Screen
            game.setScreen(new MainMenuScreen(game));
        }
    }

}
