package com.teammerge.abandoned.actors.tables;

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

public class InventoryScreen extends Table {
    Player player;

    public InventoryScreen(Player player) {
        Skin skin = VisUI.getSkin();
        this.player = player;
        BackgroundDrawable backgroundDrawable = new BackgroundDrawable("images/plain_white_background.png");
        backgroundDrawable.setColor(0,0,0, 178);
        setSize(1280, 800);
        setBackground(backgroundDrawable);

//        Creating Actors
        Table topBarTable = new VisTable();
        Label titleLabel = new VisLabel("Inventory");
        titleLabel.setSize(128, 64);
        titleLabel.setAlignment(Align.left);
        titleLabel.setFontScale(2);

        TextButton closeButton = new VisTextButton("X");
        closeButton.setSize(64,64);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setVisible(false);
                remove();
            }
        });
        topBarTable.align(Align.left);
        topBarTable.add(closeButton).size(64).spaceRight(16);
        topBarTable.add(titleLabel).height(64);


        VisScrollPane inventoryScrollPane;
        VisTable inventoryTable = new VisTable();
        List<String> inventoryList = new List<>(skin);
        //TODO get Player Inventory, Modify List Size
        inventoryList.setItems("Bandage", "Clean Water", "Beef Jerky", "Dried Fruit", "Trash Bag");
        inventoryTable.add(inventoryList).expand().fill();
        inventoryScrollPane = new VisScrollPane(inventoryTable);

        VisTable currentItemTable = new VisTable();

        Label itemLabel = new VisLabel(inventoryList.getSelected());
        itemLabel.setAlignment(Align.center);

        Label descriptionLabel = new VisLabel("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc ex turpis, pharetra nec neque nec, ultricies maximus tortor. Fusce venenatis arcu vitae odio molestie tempus.");
        descriptionLabel.setAlignment(Align.center);
        descriptionLabel.setWrap(true);

        Table buttonGroupTable = new VisTable();
        TextButton useButton = new VisTextButton("Use", "blue");
        TextButton throwButton = new VisTextButton("Throw");
        buttonGroupTable.pad(16);
        buttonGroupTable.add(useButton).size(272,80).pad(16);
        buttonGroupTable.add(throwButton).size(272,80).pad(16).row();


        currentItemTable.pad(32);
        currentItemTable.add(itemLabel).fillX().spaceBottom(32);
        currentItemTable.row().expandX().fillX();
        currentItemTable.add(descriptionLabel).expand().fill();
        currentItemTable.row().expandX().fill();
        currentItemTable.add(buttonGroupTable).fillX();

        inventoryTable.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                itemLabel.setText(inventoryList.getSelected());
            }
        });

        // Finalization
        align(Align.topLeft);
        add(topBarTable).expandX().fillX().spaceBottom(32);
        row().fillX().expandX();
        add(inventoryScrollPane).fillX().fillY();
        add(currentItemTable).fillX();
    }
}
