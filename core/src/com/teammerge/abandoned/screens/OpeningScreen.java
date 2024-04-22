package com.teammerge.abandoned.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.teammerge.abandoned.AbandonedGame;

import java.util.ArrayList;
import java.util.List;

public class OpeningScreen implements Screen {

    private final AbandonedGame game;
    private final BitmapFont font;
    private final List<String> displayTexts;
    private int currentTextIndex = 0;
    private int currentLetterIndex = 0;
    private boolean textComplete = false;
    private final int mapWidth;
    private final int mapHeight;

    public OpeningScreen(AbandonedGame game, int mapWidth, int mapHeight) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/RobotoCondensed-Light.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24;
        font = generator.generateFont(parameter);
        generator.dispose();

        this.game = game;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;

        displayTexts = new ArrayList<>();
        displayTexts.add("I woke up from my slumber expecting an ordinary morning" +
                "\nWhat awaits me is something out of a movie" +
                "\nWhat should be a bustling street full of noises is now completely silent devoid of people");
        displayTexts.add("Seriously, what a troublesome situation" +
                "\nAll alone in the middle of a city somewhere without any tools at my disposal" +
                "\nNo information, No necessities, Nothing");
        displayTexts.add("But would I die?");
        displayTexts.add("Nah, I'd live");
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
                    currentLetterIndex = displayTexts.get(currentTextIndex).length();
                }
                return true;
            }
        });
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        // Draw the current text substring up to the current letter index
        font.draw(game.batch,
                displayTexts.get(currentTextIndex).substring(0, currentLetterIndex),
                Gdx.graphics.getWidth() / 2f,
                Gdx.graphics.getHeight() / 2f,
                0,
                Align.center,
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
        Timer.schedule(new Task() {
            @Override
            public void run() {
                if (currentLetterIndex < displayTexts.get(currentTextIndex).length()) {
                    currentLetterIndex++;
                } else {
                    // If all letters are displayed, mark textComplete as true
                    textComplete = true;
                    this.cancel();
                }
            }
        }, 0, 0.05f);
    }

    private void nextTextOrGame() {
        currentTextIndex++;
        if (currentTextIndex < displayTexts.size()) {
            // If there are more texts to display, reset currentLetterIndex and start displaying the next text.
            currentLetterIndex = 0;
            textComplete = false;
            displayCurrentText();
        } else {
            // If all texts are displayed, transition to the game screen
            game.setScreen(new GameScreen(game, mapWidth, mapHeight));
        }
    }
}