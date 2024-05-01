package com.teammerge.abandoned.actors.tables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
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
    public int itemsCrafted;

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

        // Font initialization code
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


        BitmapFont font = lightGenerator.generateFont(parameter);

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
        setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.align(Align.topLeft);

        skin.add("close_icon", new Texture(Gdx.files.internal("images/icons/close.png")));
        List.ListStyle inventorystyle = new List.ListStyle(titleRegularFont, Color.WHITE, Color.GRAY, skin.newDrawable("white", new Color(0.5f, 0.5f, 0.5f, 0.2f)));
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(skin.newDrawable("white",new Color(0.5f,0.5f,0.5f, 0.2f)), skin.newDrawable("white",new Color(0.5f,0.5f,0.5f, 0.5f)), skin.newDrawable("white",new Color(0.0f,0.0f,0.0f, 0.0f)), buttonRegularFont);
        buttonStyle.disabledFontColor = Color.DARK_GRAY;
        buttonStyle.disabled = skin.newDrawable("white",new Color(0.2f,0.1f,0.1f,0.3f));

        Table topBarTable = new Table();

        topBarTable.align(Align.topLeft);

        Label titlelabel = new Label("CRAFTING", new Label.LabelStyle(topBarMediumFont, Color.WHITE));

        ImageButton closeButton = new ImageButton(skin.getDrawable("close_icon"));
        closeButton.pad(18);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                remove();
            }
        });

        topBarTable.add(titlelabel).expandX().fillX().right().pad(10f).padLeft(10f);
        topBarTable.add(closeButton).size(72).right() ;



        this.add(topBarTable).expandX().fillX().colspan(2);
        this.row().fillX().expandX();


        Table inventoryTable = new Table();
        VisScrollPane inventoryScrollPane = new VisScrollPane(inventoryTable);
            List<String> inventoryList = new List<>(inventorystyle);
            inventoryTable.add(inventoryList).expand().fill().pad(10f);
        this.add(inventoryScrollPane).fillX().fillY().expand();
        inventoryList.setItems(itemList.stream().map(Item::name).toArray(String[]::new));
        inventoryList.setSelectedIndex(0);


            Table currentItemTable = new Table();
            currentItemTable.align(Align.topLeft);
            currentItemTable.pad(32);

            Label.LabelStyle labelStyle = new Label.LabelStyle(regularGenerator.generateFont(parameter), Color.WHITE);

            Label itemLabel = new VisLabel("", labelStyle);
            itemLabel.setAlignment(Align.left);
            currentItemTable.add(itemLabel).fillX().spaceBottom(32);
            currentItemTable.row().expandX().fillX();

            Label descriptionLabel = new VisLabel("", labelStyle);
            descriptionLabel.setAlignment(Align.left);
            descriptionLabel.setWrap(true);
            currentItemTable.add(descriptionLabel).expand().fill();
            currentItemTable.row().expandX().fill();

            currentItemTable.add(new VisTable() {{
                pad(16);
            }}).fill();
            currentItemTable.row().expandX().fillX();
            currentItemTable.align(Align.topLeft);

            this.requirementsTable = new VisTable();
                Label requirementsLabel = new VisLabel("Requirements:", labelStyle);
                requirementsLabel.setAlignment(Align.topLeft);
                requirementsTable.add(requirementsLabel).fillX().left();
                requirementsTable.row().expandX().fill();


                for (int i = 0; i < selectedItem.recipes().length; ++i) {
                    var recipe = selectedItem.recipes()[i];
                    Table requirementGroupTable = new VisTable();

                    if (i > 0) {
                        Label orLabel = new VisLabel("Or: ", labelStyle) {{
                            setAlignment(Align.left);
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
                        Label requirementLabel = new VisLabel(item.name() + "(" + countInInventory + "/" + entry.count() + ")", labelStyle) {{
                            setAlignment(Align.left);
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

                TextButton craftButton = new TextButton("Craft", buttonStyle);
                craftButton.align(Align.right);
                buttonGroupTable.add(craftButton).size(235,63).pad(18f);
            currentItemTable.add(buttonGroupTable).fillX();
            this.add(currentItemTable).fillX();
            currentItemTable.add(craftButton).left();

        /// [Content]

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

        craftButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int active = inventoryList.getSelectedIndex();
                int resultCount = 0;
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
                        resultCount = recipe.resultCount();

                        break;
                    }
                }
                Sound sound = Gdx.audio.newSound(Gdx.files.internal("sounds/crafting.wav"));
                sound.play();
                screen.showLoadingScreen("Crafting Completed", item.name() + "(" + resultCount + ")");
                screen.setItemsCrafted(screen.getItemsCrafted() + 1);

                for (int i = 0; i < resultCount; ++i) {
                    player.addItem(item.id());
                }
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
                    Label requirementsLabel = new VisLabel("Requirements:", labelStyle);
                    requirementsLabel.setAlignment(Align.left);
                    requirementsTable.add(requirementsLabel).expandX().left();
                    requirementsTable.row().expandX().fill();


                    for (int i = 0; i < selectedItem.recipes().length; ++i) {
                        var recipe = selectedItem.recipes()[i];
                        Table requirementGroupTable = new VisTable();
                        requirementGroupTable.align(Align.topLeft);

                        if (i > 0) {
                            Label orLabel = new VisLabel("OR: ", labelStyle) {{
                                setAlignment(Align.left);
                            }};
                            requirementGroupTable.add(orLabel).fill().padLeft(50);
                            requirementGroupTable.row().expandX().fill();
                        }

                        for (var entry : recipe.sources()) {
                            int countInInventory = (int)player.getInventory()
                                    .stream()
                                    .filter(id -> id.equals(entry.id()))
                                    .count();

                            Item item = Item.of(entry.id());
                            Label requirementLabel = new VisLabel(item.name() + "(" + countInInventory + "/" + entry.count() + ")", labelStyle) {{
                                setAlignment(Align.left);
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
                    if ((Arrays.asList("cooked_avian","cooked_fish","clean_water")).contains(idsOfItemsThatCanBeCrafted[inventoryList.getSelectedIndex()]) && screen.getCampfire().getSecondsRemaining() == 0) {
                        craftButton.setText("Campfire Required");
                        craftButton.setDisabled(true);
                    }
                } else {
                    craftButton.setText("Not enough items");
                    craftButton.setDisabled(true);
                }
            }
        });

        /// [Drawing]

        BackgroundDrawable backgroundDrawable = new BackgroundDrawable("images/plain_white_background.png");
        backgroundDrawable.setColor(0,0,0, 205);
        setBackground(backgroundDrawable);
    }
}
