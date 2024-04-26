package com.teammerge.abandoned.actors.tables;

import com.badlogic.gdx.graphics.Color;
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
import com.teammerge.abandoned.records.Recipe;
import com.teammerge.abandoned.records.RecipeSourceEntry;
import com.teammerge.abandoned.screens.GameScreen;
import com.teammerge.abandoned.utilities.InsertionSort;
import com.teammerge.abandoned.utilities.items.ItemRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class CraftingScreen extends Table {
    private final GameScreen screen;
    private final Player player;

    private record Tuple2<T1, T2>(T1 obj1, T2 obj2) {}

    private final String[] idsOfItemsThatCanBeCrafted;

    private String[] currentItems() {
        return player.getInventory()
                .stream()
                .map(Item::of)
                .map(Item::name)
                .toArray(String[]::new);
    }

    private final HashMap<Item, Boolean> _canCraftMemo = new HashMap<>();

    private boolean canCraft(Item item) {
        if (!_canCraftMemo.containsKey(item)) {
            /// For each recipe,
            _canCraftMemo.put(item, Arrays.stream(item.recipes())
                    /// as [r], for each of the sources of the recipe
                    .map(r -> Arrays.stream(r.sources())
                            /// as [e], if the count of the item needed is equal to the count needed by [e],
                            .map(e -> player.getInventory().stream().filter(id -> id.equals(e.id())).count() >= e.count())
                            /// and all of them match,
                            .reduce(true, (a, b) -> a && b))
                    /// and at least one of them match, then it's true.
                    .reduce(false, (a, b) -> a || b));
        }

        return _canCraftMemo.get(item);
    }

    private Table requirementsTable;

    public CraftingScreen(Player player, GameScreen screen) {
        /// [Property Initialization];
        this.player = player;
        this.screen = screen;

        ArrayList<Item> itemList = ItemRepository.getAllItems()
                .stream()
                .filter(Item::canBeCrafted)
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));

        /// Holy Moly!
        InsertionSort.run(itemList, (a, b) -> new HashMap<Tuple2<Boolean, Boolean>, Integer>(){{
            put(new Tuple2<>(true, true), a.id().compareTo(b.id()));
            put(new Tuple2<>(false, true), 1);
            put(new Tuple2<>(true, false), -1);
            put(new Tuple2<>(false, false), a.id().compareTo(b.id()));
        }}.get(new Tuple2<>(canCraft(a), canCraft(b))));

        this.idsOfItemsThatCanBeCrafted = itemList
            .stream()
            .map(Item::id)
            .toArray(String[]::new);

        int selectedIndex = 0;
        Item selectedItem = Item.of(idsOfItemsThatCanBeCrafted[selectedIndex]);

        /// [Layouts]
        Skin skin = VisUI.getSkin();
        this.setSize(1280, 800);
        this.align(Align.topLeft);

        Table topBarTable = new VisTable();
            topBarTable.align(Align.left);

            TextButton closeButton = new VisTextButton("X");
                closeButton.setSize(64,64);
            topBarTable.add(closeButton).size(64).align(Align.left);

            Label titleLabel = new VisLabel();
                titleLabel.setSize(128, 64);
                titleLabel.setAlignment(Align.left);
                titleLabel.setFontScale(2);
            topBarTable.add(titleLabel).height(64);
        this.add(topBarTable).expandX().fillX().spaceBottom(32);
        this.row().fillX().expandX();

        VisTable inventoryTable = new VisTable();
        VisScrollPane inventoryScrollPane = new VisScrollPane(inventoryTable);
            List<String> inventoryList = new List<>(skin);
            inventoryTable.add(inventoryList).expand().fill();
        this.add(inventoryScrollPane).fillX().fillY();

        VisTable currentItemTable = new VisTable();
            currentItemTable.pad(32);

            Label itemLabel = new VisLabel();
                itemLabel.setAlignment(Align.center);
            currentItemTable.add(itemLabel).fillX().spaceBottom(32);
            currentItemTable.row().expandX().fillX();

            Label descriptionLabel = new VisLabel();
                descriptionLabel.setAlignment(Align.center);
                descriptionLabel.setWrap(true);
            currentItemTable.add(descriptionLabel).expand().fill();
            currentItemTable.row().expandX().fill();

            currentItemTable.add(new VisTable() {{
                pad(16);
            }}).fill();
            currentItemTable.row().expandX().fill();

            this.requirementsTable = new VisTable();
                Label requirementsLabel = new VisLabel("Requirements:");
                requirementsLabel.setAlignment(Align.center);
                requirementsTable.add(requirementsLabel).fill();
                requirementsTable.row().expandX().fill();

                for (int i = 0; i < selectedItem.recipes().length; ++i) {
                    var recipe = selectedItem.recipes()[i];
                    Table requirementGroupTable = new VisTable();

                    if (i > 0) {
                        Label orLabel = new VisLabel("OR: ") {{
                            setAlignment(Align.center);
                        }};
                        requirementGroupTable.add(orLabel).fill();
                        requirementGroupTable.row().expandX().fill();
                    }

                    for (var entry : recipe.sources()) {
                        int countInInventory = (int)player.getInventory()
                            .stream()
                            .filter(id -> id.equals(entry.id()))
                            .count();

                        Item item = Item.of(entry.id());
                        Label requirementLabel = new VisLabel(item.name() + "(" + countInInventory + "/" + entry.count() + ")") {{
                            setAlignment(Align.center);
                        }};
                        requirementGroupTable.add(requirementLabel).fill();
                        requirementGroupTable.row().expandX().fill();
                    }

                    requirementsTable.add(requirementGroupTable).fill();
                    requirementsTable.row().expandX().fill();
                }
            currentItemTable.add(requirementsTable).fill();
            currentItemTable.row().expandX().fill();

            Table buttonGroupTable = new VisTable();
                buttonGroupTable.pad(16);

                TextButton craftButton = new VisTextButton("Craft", "blue");
                buttonGroupTable.add(craftButton).size(272f,80f).pad(16f);
            currentItemTable.add(buttonGroupTable).fillX();
        this.add(currentItemTable).fillX();

        /// [Content]

        titleLabel.setText("Crafting");
        inventoryList.setItems(itemList.stream().map(i -> i.name() + " (" + (canCraft(i) ? "Y" : "N") + ")").toArray(String[]::new));

        itemLabel.setText(selectedItem.name());
        descriptionLabel.setText(selectedItem.description());

        if (canCraft(selectedItem)) {
            craftButton.setText("Craft");
            craftButton.setDisabled(false);
        } else {
            craftButton.setText("Not enough items");
            craftButton.setDisabled(true);
        }


        /// [Event Listeners]
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setVisible(false);
                remove();
            }
        });
        craftButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int active = inventoryList.getSelectedIndex();
                Item item = Item.of(idsOfItemsThatCanBeCrafted[active]);

                if (!canCraft(item)) {
                    return;
                }

                for (Recipe recipe : item.recipes()) {
                    if (recipe.canBeCraftedWith(player.getInventory())) {
                        /// Remove all the items.

                        ArrayList<String> items = player.getInventory();
                        for (RecipeSourceEntry entry : recipe.sources()) {
                            for (int i = 0; i < entry.count(); ++i) {
                                items.remove(entry.id());
                            }
                        }

                        break;
                    }
                }

                screen.showLoadingScreen("Success", "Crafting: " + item.name());
                player.addItem(item.id());
            }
        });
        inventoryTable.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ArrayList<String> inventory = player.getInventory();
                int index = inventoryList.getSelectedIndex();
                if (index >= idsOfItemsThatCanBeCrafted.length) {
                    return;
                }

                Item selectedItem = Item.of(idsOfItemsThatCanBeCrafted[index]);

                /// Redraw.
                itemLabel.setText(selectedItem.name());
                descriptionLabel.setText(selectedItem.description());

                requirementsTable.clear();
                    Label requirementsLabel = new VisLabel("Requirements:");
                        requirementsLabel.setAlignment(Align.center);
                    requirementsTable.add(requirementsLabel).fill();
                    requirementsTable.row().expandX().fill();

                    for (int i = 0; i < selectedItem.recipes().length; ++i) {
                        var recipe = selectedItem.recipes()[i];
                        Table requirementGroupTable = new VisTable();

                        if (i > 0) {
                            Label orLabel = new VisLabel("OR: ") {{
                                setAlignment(Align.center);
                            }};
                            requirementGroupTable.add(orLabel).fill();
                            requirementGroupTable.row().expandX().fill();
                        }

                        for (var entry : recipe.sources()) {
                            int countInInventory = (int)player.getInventory()
                                    .stream()
                                    .filter(id -> id.equals(entry.id()))
                                    .count();

                            Item item = Item.of(entry.id());
                            Label requirementLabel = new VisLabel(item.name() + "(" + countInInventory + "/" + entry.count() + ")") {{
                                setAlignment(Align.center);
                            }};
                            requirementGroupTable.add(requirementLabel).fill();
                            requirementGroupTable.row().expandX().fill();
                        }

                        requirementsTable.add(requirementGroupTable).fill();
                        requirementsTable.row().expandX().fill();
                    }

                if (canCraft(selectedItem)) {
                    craftButton.setText("Craft");
                    craftButton.setDisabled(false);
                } else {
                    craftButton.setText("Not enough items");
                    craftButton.setDisabled(true);
                }
            }
        });

        /// [Drawing]
        BackgroundDrawable backgroundDrawable = new BackgroundDrawable("images/plain_white_background.png");
        backgroundDrawable.setColor(0,0,0, 102);
        setBackground(backgroundDrawable);
    }
}
