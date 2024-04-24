package com.teammerge.abandoned.actors.tables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.teammerge.abandoned.actors.drawables.BackgroundDrawable;

public class DialogScreen extends Table {

    public DialogScreen() {
        this("Sample Text", "Sample Text, Sample Text, Sample Text");
    }

    public DialogScreen(String title, String message) {

//        Creating translucent background
        BackgroundDrawable backgroundDrawable = new BackgroundDrawable("images/plain_white_background.png");
        backgroundDrawable.setColor(0f,0f,0f, 205);

        setSize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        setBackground(backgroundDrawable);

//        Generating BitmapFont from TTF Fonts
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/RobotoCondensed-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = 36;
        BitmapFont titleFont = generator.generateFont(parameter);

        parameter.size = 27;
        BitmapFont textFont = generator.generateFont(parameter);
        generator.dispose();

//        Creating LabelStyles and Labels for screen title and text
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        Label titleLabel = new Label(title, titleStyle);
        titleLabel.setAlignment(Align.center);

        Label.LabelStyle messageStyle = new Label.LabelStyle(textFont,Color.WHITE);
        Label messageLabel = new Label(message,messageStyle);
        messageLabel.setAlignment(Align.center);

//        Creating skin for TextButton
        Skin skin = new Skin();
        Pixmap pixmap = new Pixmap(10, 10, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(skin.newDrawable("white",Color.CLEAR), skin.newDrawable("white",Color.CLEAR), skin.newDrawable("white",Color.CLEAR), titleFont);

        TextButton button = new TextButton("Ok.", buttonStyle);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                remove();
            }
        });


        add(titleLabel).center().fillX().spaceBottom(18);
        row().expandX().fillX();
        add(messageLabel).center().fillX().spaceBottom(27);
        row().expandX().fillX();
        add(button).size(135, 90).fillX();
    }
}
