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
import com.teammerge.abandoned.records.Item;

public class InventoryScreen extends Table {
    Player player;

    private String[] currentItems() {
        return player.getInventory()
                .stream()
                .map(Item::of)
                .map(Item::name)
                .toArray(String[]::new);
    }

    public InventoryScreen(Player player) {
        Skin skin = VisUI.getSkin();
        this.player = player;
        BackgroundDrawable backgroundDrawable = new BackgroundDrawable("images/plain_white_background.png");
        backgroundDrawable.setColor(0,0,0, 102);
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

        inventoryList.setItems(currentItems());
        inventoryTable.add(inventoryList).expand().fill();
        inventoryScrollPane = new VisScrollPane(inventoryTable);

        VisTable currentItemTable = new VisTable();

        Label itemLabel = new VisLabel();
        itemLabel.setAlignment(Align.center);

        Label descriptionLabel = new VisLabel();
        descriptionLabel.setAlignment(Align.center);
        descriptionLabel.setWrap(true);

        Item selectedItem = player.getInventory().isEmpty()
                ? null
                : Item.of(player.getInventory().getFirst());

        if (selectedItem == null) {
            itemLabel.setText("");
            descriptionLabel.setText("");
        } else {
            itemLabel.setText(selectedItem.name());
            descriptionLabel.setText(selectedItem.description());
        }

        Table buttonGroupTable = new VisTable();
        TextButton useButton = new VisTextButton("Use", "blue");
        useButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int active = inventoryList.getSelectedIndex();
                Item item = Item.of(player.getInventory().get(active));

                if (item.isUsable()) {
                    /// Update the stats.

//                    getStage().addActor(new LoadingScreen("Consuming: " + item.name()));

                    player.setEnergy(player.getEnergy() + item.effectEnergy());
                    player.setCondition(player.getCondition() + item.effectCondition());
                    player.setHydration(player.getHydration() + item.effectHydration());
                    player.setFullness(player.getFullness() + item.effectFullness());

                    /// Remove the item from the inventory.
                    player.getInventory().remove(active);

                    /// Update the UI.
                    inventoryList.setItems(currentItems());
                }
            }
        });

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
                Item item = Item.of(player.getInventory().get(inventoryList.getSelectedIndex()));

                itemLabel.setText(item.name());
                descriptionLabel.setText(item.description());
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
