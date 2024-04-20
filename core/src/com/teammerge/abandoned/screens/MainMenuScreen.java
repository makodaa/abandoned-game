package com.teammerge.abandoned.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.teammerge.abandoned.AbandonedGame;
import com.teammerge.abandoned.actors.drawables.BackgroundDrawable;

public class MainMenuScreen implements Screen {

    private final AbandonedGame game;
    private final OrthographicCamera camera;
    private final Stage stage;
    private final SpriteBatch batch;

    public static final int row_height = Gdx.graphics.getHeight() / 16;
    public static final int col_width = Gdx.graphics.getWidth() / 16;
    BitmapFont h1, h2;
    Label titleLabel;
    Table difficultyScreen;

    TextButton newGameButton, exitButton;

//  TODO: Search about viewports
    public MainMenuScreen(final AbandonedGame game) {
        this.game = game;
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 800);
        Gdx.input.setInputProcessor(stage); // Never Forget
        VisUI.load();

        difficultyScreen = createDifficultyScreen();


//      Creating H1, H2 fonts
        h1 = generateFont("fonts/TheoVanDoesburg.TTF", 60);
        h2 = generateFont("fonts/TheoVanDoesburg.TTF", 28);

        titleLabel = new Label("Abandoned", new Label.LabelStyle(h1,Color.WHITE));
        titleLabel.setPosition(col_width,Gdx.graphics.getHeight() - row_height * 3);

//        Creating New Game Button
        newGameButton = createTextButton("New Game", h2);
        newGameButton.setPosition((float)col_width, Gdx.graphics.getHeight() - row_height * 6);
        newGameButton.addListener(new InputListener() {

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                newGameButton.getLabel().setColor(Color.YELLOW);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                newGameButton.getLabel().setColor(Color.WHITE);
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                stage.addActor(createDifficultyScreen());
                return false;
            }
        });

//        Creating Exit Button
        exitButton = createTextButton("Exit", h2);
        exitButton.setPosition((float)col_width, Gdx.graphics.getHeight() - row_height * 7);
        exitButton.addListener(new InputListener() {

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                exitButton.getLabel().setColor(Color.YELLOW);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                exitButton.getLabel().setColor(Color.WHITE);
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
                return false;
            }

        });

//        Telling stage to include following actors in render
        stage.addActor(titleLabel);
        stage.addActor(newGameButton);
        stage.addActor(exitButton);

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        stage.act();
        stage.draw();
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
        h1.dispose();
        h2.dispose();
        stage.dispose();
        batch.dispose();
    }

    private TextButton createTextButton(String text, BitmapFont font) {
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;

        TextButton button = new TextButton(text, buttonStyle);
        button.getLabel().setAlignment(Align.left);

        return button;
    }
    private BitmapFont generateFont(String path, int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(path));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.spaceX = 4;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        return font;
    }
    private Table createDifficultyScreen() {
        Table table = new Table();

        BackgroundDrawable white = new BackgroundDrawable("images/plain_white_background.png");
        white.setColor(0,0,0,205);
        BackgroundDrawable grey = new BackgroundDrawable("images/plain_white_background.png");
        grey.setColor(127,127,127,255);

        table.setSize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        table.setBackground(white);

        BitmapFont robotoMedium = generateFont("fonts/RobotoCondensed-Medium.ttf", 28);
        Label.LabelStyle labelStyle = new Label.LabelStyle(robotoMedium, Color.WHITE);



        TextButton closeButton = createTextButton("X",robotoMedium);
        closeButton.getLabel().setSize(36, 36);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                table.remove();
            }
        });
        Label label = new Label("Choose Difficulty", labelStyle);

        Table topBarGroup = new Table();
        topBarGroup.add(closeButton).size(72).left().spaceRight(9);
        topBarGroup.add(label).left();

        TextButton smallMapButton = new VisTextButton("Small Map [31 x 31]");
        smallMapButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game, 31,31));
            }
        });
        TextButton mediumMapButton = new VisTextButton("Medium Map [97 x 97]");
        mediumMapButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game, 97,97));
            }
        });
        TextButton largeMapButton = new VisTextButton("Medium Map [171 x 171]");
        largeMapButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game, 171,171));
            }
        });
        table.defaults().size(235,81);
        table.align(Align.topLeft);
        table.add(topBarGroup);
        table.row();
        table.align(Align.center);
        table.add(smallMapButton).spaceBottom(9);
        table.row();
        table.add(mediumMapButton).spaceBottom(9);
        table.row();
        table.add(largeMapButton).spaceBottom(9);
        table.row().expand().fill();
        return table;
    }
}
