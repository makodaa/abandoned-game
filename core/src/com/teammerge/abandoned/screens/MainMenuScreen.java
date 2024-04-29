package com.teammerge.abandoned.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.teammerge.abandoned.AbandonedGame;

import java.io.*;

public class MainMenuScreen implements Screen {

    private final AbandonedGame game;
    private final OrthographicCamera camera;
    private final Stage stage;
    private final SpriteBatch batch;

    private final GameScreen gameScreen;

    public static final int row_height = Gdx.graphics.getHeight() / 16;
    public static final int col_width = Gdx.graphics.getWidth() / 16;
    BitmapFont h1, h2;
    Label titleLabel;
    Table difficultyScreen;

    TextButton loadGameButton, newGameButton, exitButton;

    Texture background;

    public MainMenuScreen(final AbandonedGame game) {
        this.game = game;
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());
        camera = new OrthographicCamera();
        background = new Texture(Gdx.files.internal("images/main_menu_background.png"));
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.input.setInputProcessor(stage); // Never Forget

        difficultyScreen = createDifficultyScreen();

//      Creating H1, H2 fonts
        h1 = generateFont("fonts/TheoVanDoesburg.TTF", 108);
        h2 = generateFont("fonts/TheoVanDoesburg.TTF", 36);

        titleLabel = new Label("Abandoned", new Label.LabelStyle(h1,Color.WHITE));
        titleLabel.setPosition(col_width,Gdx.graphics.getHeight() - row_height * 4f);

        gameScreen = loadGameScreen();
//        Creating New Game Button

        if (gameScreen != null) {
            loadGameButton = createTextButton("Continue Game",h2);
            loadGameButton.setPosition((float) col_width, Gdx.graphics.getHeight() - row_height * 6);
            loadGameButton.addListener(new InputListener() {

                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    loadGameButton.getLabel().setColor(Color.GOLD);
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    loadGameButton.getLabel().setColor(Color.WHITE);
                }

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    game.setScreen(GameScreen.fromSerialized(game, gameScreen));
                    return false;
                }
            });
        }

//        Creating New Game Button
        newGameButton = createTextButton(gameScreen == null ? "New Game" : "Overwrite Game", h2);
        newGameButton.setPosition((float)col_width, Gdx.graphics.getHeight() - row_height * 7.5f);
        newGameButton.addListener(new InputListener() {

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                newGameButton.getLabel().setColor(Color.GOLD);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                newGameButton.getLabel().setColor(Color.WHITE);
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                stage.addActor(createDifficultyScreen());
//                game.setScreen(gameScreen = new GameScreen(game,31,31));
                return false;
            }
        });

//        Creating Exit Button
        exitButton = createTextButton("Exit", h2);
        exitButton.setPosition((float)col_width, Gdx.graphics.getHeight() - row_height * 9);
        exitButton.addListener(new InputListener() {

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                exitButton.getLabel().setColor(Color.GOLD);
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
        if (gameScreen != null) {
            stage.addActor(loadGameButton);
        }
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
        batch.begin();
        batch.draw(background,0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
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

        if (gameScreen != null) {
            gameScreen.dispose();
        }
    }

    private GameScreen loadGameScreen() {
        String path = Gdx.files.internal("saves/save_file.txt").path();
        File file = new File(path);
        if (!file.isFile()) {
            var unused = file.getParentFile().mkdirs();

            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Failed to create file: " + e.getMessage());
                return null;
            }
        }


        try (FileInputStream fileInputStream = new FileInputStream(path);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            return (GameScreen)objectInputStream.readObject();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
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


        FreeTypeFontGenerator mediumGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/RobotoCondensed-Medium.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 36;
        BitmapFont topBarMediumFont = mediumGenerator.generateFont(parameter);
        parameter.size = 24;
        BitmapFont textRegularFont = mediumGenerator.generateFont(parameter);


        // Load Skin, Drawable, and Icons
        Skin skin = new Skin();
        Pixmap pixmap = new Pixmap(10, 10, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));
        skin.add("close_icon", new Texture(Gdx.files.internal("images/icons/close.png")));
        skin.add("easy_icon", new Texture(Gdx.files.internal("images/icons/difficulties/easy.png")));
        skin.add("medium_icon", new Texture(Gdx.files.internal("images/icons/difficulties/medium.png")));
        skin.add("hard_icon", new Texture(Gdx.files.internal("images/icons/difficulties/hard.png")));


        Table topBarTable = new Table();
        Label titlelabel = new Label("SELECT DIFFICULTY", new Label.LabelStyle(topBarMediumFont,Color.WHITE));
        ImageButton closeButton = new ImageButton(skin.newDrawable("close_icon"));
        closeButton.pad(18.0f);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                table.remove();
            }
        });
        topBarTable.add(titlelabel).expandX().fillX().right();
        topBarTable.add(closeButton).right();

        table.setSize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        table.setBackground(skin.newDrawable("white",new Color(0,0,0,0.8f )));

        ImageTextButton.ImageTextButtonStyle smallMapButtonStyle = new ImageTextButton.ImageTextButtonStyle();
        smallMapButtonStyle.font = textRegularFont;
        smallMapButtonStyle.fontColor = Color.WHITE;
        smallMapButtonStyle.up = skin.newDrawable("white",new Color(0.5f,0.5f,0.5f,0.2f));
        smallMapButtonStyle.down = skin.newDrawable("white",new Color(0.5f,0.5f,0.5f,0.5f));
        smallMapButtonStyle.checked = smallMapButtonStyle.down;
        smallMapButtonStyle.imageUp = skin.newDrawable("easy_icon");
        smallMapButtonStyle.imageDown = smallMapButtonStyle.imageUp;

        ImageTextButton.ImageTextButtonStyle mediumMapButtonStyle = new ImageTextButton.ImageTextButtonStyle();
        mediumMapButtonStyle.font = textRegularFont;
        mediumMapButtonStyle.fontColor = Color.WHITE;
        mediumMapButtonStyle.up = skin.newDrawable("white",new Color(0.5f,0.5f,0.5f,0.2f));
        mediumMapButtonStyle.down = skin.newDrawable("white",new Color(0.5f,0.5f,0.5f,0.5f));
        mediumMapButtonStyle.checked = mediumMapButtonStyle.down;
        mediumMapButtonStyle.imageUp = skin.newDrawable("medium_icon");
        mediumMapButtonStyle.imageDown = mediumMapButtonStyle.imageUp;

        ImageTextButton.ImageTextButtonStyle largeMapButtonStyle = new ImageTextButton.ImageTextButtonStyle();
        largeMapButtonStyle.font = textRegularFont;
        largeMapButtonStyle.fontColor = Color.WHITE;
        largeMapButtonStyle.up = skin.newDrawable("white",new Color(0.5f,0.5f,0.5f,0.2f));
        largeMapButtonStyle.down = skin.newDrawable("white",new Color(0.5f,0.5f,0.5f,0.5f));
        largeMapButtonStyle.checked = largeMapButtonStyle.down;
        largeMapButtonStyle.imageUp = skin.newDrawable("hard_icon");
        largeMapButtonStyle.imageDown = largeMapButtonStyle.imageUp;


        ImageTextButton smallMapButton = new ImageTextButton("SMALL MAP\n[31 x 31]",smallMapButtonStyle);
        smallMapButton.clearChildren();
        smallMapButton.add(smallMapButton.getImage()).spaceBottom(27);
        smallMapButton.row();
        smallMapButton.add(smallMapButton.getLabel());
        smallMapButton.getLabel().setAlignment(Align.bottom);
        smallMapButton.pad(63);
        smallMapButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                game.setScreen(new OpeningScreen(game, 31, 31));
            }
        });

        ImageTextButton mediumMapButton = new ImageTextButton("MEDIUM MAP\n[97 x 97]",mediumMapButtonStyle);
        mediumMapButton.clearChildren();
        mediumMapButton.add(mediumMapButton.getImage()).spaceBottom(27);
        mediumMapButton.row();
        mediumMapButton.add(mediumMapButton.getLabel());
        mediumMapButton.getLabel().setAlignment(Align.bottom);
        mediumMapButton.pad(63);
        mediumMapButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                game.setScreen(new OpeningScreen(game, 97,97));
            }
        });

        ImageTextButton largeMapButton = new ImageTextButton("LARGE MAP\n[171 x 171]",largeMapButtonStyle);
        largeMapButton.clearChildren();
        largeMapButton.add(largeMapButton.getImage()).spaceBottom(27);
        largeMapButton.row();
        largeMapButton.add(largeMapButton.getLabel());
        largeMapButton.getLabel().setAlignment(Align.bottom);
        largeMapButton.pad(63);
        largeMapButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                game.setScreen(new OpeningScreen(game, 171,171));
            }
        });




        table.pad(18.0f);
        table.align(Align.topLeft);
        table.add(topBarTable).colspan(3).height(72).expandX().fillX().left();
        table.row();
        table.add(smallMapButton).size(300,300).expandY().fill();
        table.add(mediumMapButton).size(300,300).expandY().fill();
        table.add(largeMapButton).size(300,300).expandY().fill();
        return table;
    }

}