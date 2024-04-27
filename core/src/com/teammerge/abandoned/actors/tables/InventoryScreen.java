package com.teammerge.abandoned.actors.tables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.teammerge.abandoned.actors.drawables.BackgroundDrawable;
import com.teammerge.abandoned.entities.Player;
import com.teammerge.abandoned.records.Item;
import com.teammerge.abandoned.screens.GameScreen;
import com.teammerge.abandoned.utilities.wfc.enums.AreaType;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

public class InventoryScreen extends Table {
    Player player;
    BackgroundDrawable backgroundDrawable;
    GameScreen screen;


    private String[] currentItems() {
        return player.getInventory()
                .stream()
                .map(Item::of)
                .map(Item::name)
                .toArray(String[]::new);
    }

    public InventoryScreen(GameScreen screen, Player player) {
        this.player = player;
        this.screen = screen;
        backgroundDrawable = new BackgroundDrawable("images/plain_white_background.png");
        backgroundDrawable.setColor(0,0,0, 205);
        setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        setBackground(backgroundDrawable);

        FreeTypeFontGenerator mediumGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/RobotoCondensed-Medium.ttf"));
        FreeTypeFontGenerator regularGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/RobotoCondensed-Medium.ttf"));
        FreeTypeFontGenerator lightGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/RobotoCondensed-Light.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 36;
        BitmapFont topBarMediumFont = mediumGenerator.generateFont(parameter);
        parameter.size = 27;
        BitmapFont titleRegularFont = regularGenerator.generateFont(parameter);
        parameter.size = 24;
        BitmapFont buttonRegularFont = regularGenerator.generateFont(parameter);



        // Load Skin, Drawable, and Icons
        Skin skin = new Skin();
        Pixmap pixmap = new Pixmap(10, 10, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));
        skin.add("close_icon", new Texture(Gdx.files.internal("images/icons/close.png")));

//        Creating Actors
        Table topBarTable = new Table();

        Label titlelabel = new Label("INVENTORY", new Label.LabelStyle(topBarMediumFont,Color.WHITE));

        ImageButton closeButton = new ImageButton(skin.newDrawable("close_icon"));
        closeButton.pad(18);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                remove();
            }
        });
        topBarTable.add(titlelabel).expandX().fillX().right();
        topBarTable.add(closeButton).size(72).right();


        VisScrollPane inventoryScrollPane;
        Table inventoryTable = new Table();
        List<String> inventoryList = new List<>(new List.ListStyle(titleRegularFont, new Color(1f,1f,1f,1f), new Color(0.5f,0.5f,0.5f,0.5f), skin.newDrawable("white",new Color(0.5f,0.5f,0.5f,0.2f))));;

        List<Integer> quantityList = new List<>(new List.ListStyle(titleRegularFont, new Color(1f,1f,1f,1f), new Color(1f,1f,1f,1f), skin.newDrawable("white",new Color(0.5f,0.5f,0.5f,0.2f))));;

        inventoryList.setItems(Arrays.stream(currentItems()).distinct().toArray(String[]::new));
        quantityList.setItems(Arrays.stream(currentItems()).distinct().map(e -> Collections.frequency(java.util.List.of(currentItems()),e)).toArray(Integer[]::new));

        inventoryTable.add(inventoryList).expand().fill();
        inventoryTable.add(quantityList).fill();
        inventoryScrollPane = new VisScrollPane(inventoryTable);




        VisTable selectedItemTable = new VisTable();

        Label itemLabel = new Label("", new Label.LabelStyle(topBarMediumFont, Color.WHITE));
        itemLabel.setAlignment(Align.left);

        Label descriptionLabel = new Label("", new Label.LabelStyle(titleRegularFont,Color.WHITE));
        descriptionLabel.setAlignment(Align.left);
        descriptionLabel.setWrap(true);

        Item selectedItem = player.getInventory().isEmpty()
                ? null
                : Item.of(player.getInventory().getFirst());

        if (selectedItem == null) {
            itemLabel.setText("");
            descriptionLabel.setText("");
        } else {
            itemLabel.setText(selectedItem.name().toUpperCase());
            descriptionLabel.setText(selectedItem.description());
        }

        Table buttonGroupTable = new Table();
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(skin.newDrawable("white",new Color(0.5f,0.5f,0.5f, 0.2f)), skin.newDrawable("white",new Color(0.5f,0.5f,0.5f, 0.5f)), skin.newDrawable("white",new Color(0.0f,0.0f,0.0f, 0.0f)), buttonRegularFont);
        buttonStyle.disabledFontColor = Color.DARK_GRAY;
        buttonStyle.disabled = skin.newDrawable("white",new Color(0.2f,0.1f,0.1f,0.3f));

        TextButton useButton = new TextButton("USE", buttonStyle);
        useButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int active = inventoryList.getSelectedIndex();
                Item item = Item.of(player.getInventory().stream().distinct().toList().get(active));

                if (item.isUsable()) {
                    /// Update the stats.
                    player.setEnergy(player.getEnergy() + item.effectEnergy());
                    player.setCondition(player.getCondition() + item.effectCondition());
                    player.setHydration(player.getHydration() + item.effectHydration());
                    player.setFullness(player.getFullness() + item.effectFullness());

                    /// Remove the item from the inventory.
                    player.getInventory().remove(player.getInventory().stream().distinct().toList().get(active));

                    /// Update the UI.
                    inventoryList.setItems(Arrays.stream(currentItems()).distinct().toArray(String[]::new));
                    quantityList.setItems(Arrays.stream(currentItems()).distinct().map(e -> Collections.frequency(java.util.List.of(currentItems()),e)).toArray(Integer[]::new));
                }
            }
        });

        TextButton throwButton = new TextButton("THROW",buttonStyle);
        throwButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                int active = inventoryList.getSelectedIndex();
                Item item = Item.of(player.getInventory().stream().distinct().toList().get(active));
                /// Remove the item from the inventory.
                player.getInventory().remove(player.getInventory().stream().distinct().toList().get(active));
                /// Update the UI.
                inventoryList.setItems(Arrays.stream(currentItems()).distinct().toArray(String[]::new));
                quantityList.setItems(Arrays.stream(currentItems()).distinct().map(e -> Collections.frequency(java.util.List.of(currentItems()),e)).toArray(Integer[]::new));
            }
        });

        buttonGroupTable.align(Align.bottomRight);
        buttonGroupTable.pad(16);
        buttonGroupTable.add(useButton).size(135,63).pad(16);
        buttonGroupTable.add(throwButton).size(135,63).pad(16).row();


        selectedItemTable.pad(9.0f);
        selectedItemTable.add(itemLabel).fillX().spaceBottom(32);
        selectedItemTable.row().expandX().fillX();
        selectedItemTable.add(descriptionLabel);
        selectedItemTable.row().expand().fill();
        selectedItemTable.add(buttonGroupTable).expand().fill().right();


        inventoryTable.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Item item = Item.of(player.getInventory().stream().distinct().toList().get(inventoryList.getSelectedIndex()));

                itemLabel.setText(item.name().toUpperCase());
                descriptionLabel.setText(item.description());
            }
        });

        // Finalization
        pad(18.0f);
        defaults().spaceRight(9.0f).spaceLeft(9.0f);
        align(Align.topLeft);
        add(topBarTable).colspan(2).expandX().fillX();
        row().fillX().expandX();
        if (player.getInventory().isEmpty()) {
            Label emptyInventoryLabel = new Label("You have not collected any items yet.", new Label.LabelStyle(titleRegularFont,Color.WHITE));
            emptyInventoryLabel.setAlignment(Align.center);
            add(emptyInventoryLabel).colspan(2).expand().fill().center();
        } else {
            add(inventoryScrollPane).fill();
            add(selectedItemTable).fillY();
        }
    }
}
