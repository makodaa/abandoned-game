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
import com.teammerge.abandoned.actors.drawables.BackgroundDrawable;
import com.teammerge.abandoned.entities.Player;
import com.teammerge.abandoned.enums.Direction;
import com.teammerge.abandoned.records.Index;
import com.teammerge.abandoned.records.Item;
import com.teammerge.abandoned.screens.GameScreen;
import com.teammerge.abandoned.utilities.wfc.classes.Area;
import com.teammerge.abandoned.utilities.wfc.classes.Utils;

import java.util.*;
import java.util.List;

public class TravelScreen extends Table {
    BackgroundDrawable backgroundDrawable;
    Player player;
    GameScreen screen;

    BitmapFont topBarMediumFont, titleMediumFont, textLightFont, textRegularFont;

    public TravelScreen(Player player, GameScreen screen) {
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
        topBarMediumFont = mediumGenerator.generateFont(parameter);
        parameter.size = 27;
        titleMediumFont = mediumGenerator.generateFont(parameter);
        parameter.size = 17;
        textLightFont = lightGenerator.generateFont(parameter);
        parameter.size = 18;
        textRegularFont = regularGenerator.generateFont(parameter);


        // Load Skin, Drawable, and Icons
        Skin skin = new Skin();
        Pixmap pixmap = new Pixmap(10, 10, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));
        skin.add("close_icon", new Texture(Gdx.files.internal("images/icons/close.png")));
        skin.add("circle", new Texture(Gdx.files.internal("images/icons/map/circle.png")));
        skin.add("rescue_area_icon", new Texture(Gdx.files.internal("images/icons/map/rescue_area.png")));
        skin.add("forest_icon", new Texture(Gdx.files.internal("images/icons/map/forest.png")));
        skin.add("park_icon", new Texture(Gdx.files.internal("images/icons/map/park.png")));
        skin.add("farm_icon", new Texture(Gdx.files.internal("images/icons/map/farm.png")));
        skin.add("village_icon", new Texture(Gdx.files.internal("images/icons/map/village.png")));
        skin.add("mall_icon", new Texture(Gdx.files.internal("images/icons/map/city.png")));
        skin.add("district_icon", new Texture(Gdx.files.internal("images/icons/map/district.png")));
        skin.add("hospital_icon", new Texture(Gdx.files.internal("images/icons/map/hospital.png")));
        skin.add("question_mark", new Texture(Gdx.files.internal("images/icons/map/question_mark.png")));

        Table topBarTable = new Table();

        Label titlelabel = new Label("MAP", new Label.LabelStyle(topBarMediumFont,Color.WHITE));

        ImageButton closeButton = new ImageButton(skin.newDrawable("close_icon"));
        closeButton.pad(18);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                closeScreen();
            }
        });
        topBarTable.add(titlelabel).expandX().fillX().right();
        topBarTable.add(closeButton).size(72).right();

        pad(18.0f);
        defaults().spaceBottom(18.0f);
        align(Align.topLeft);
        add(topBarTable).colspan(2).fillX();
        row().expandX().fillX().fillY();


        Table atlasTable = createMapGraphics(skin);
        Label surroundingAreasLabel = new Label("ATLAS", new Label.LabelStyle(topBarMediumFont,Color.WHITE));
        add(surroundingAreasLabel).colspan(2).left().row();
        add(atlasTable).top();


        var mapHeight = screen.getMap().length;
        var mapWidth = screen.getMap()[0].length;

        var playerPosition = player.getPosition();
        var y = playerPosition.y();
        var x = playerPosition.x();

        Table moveButtonTable = new Table();
        moveButtonTable.defaults().spaceBottom(9.0f);

        if (y - 1 >= 0) {
            moveButtonTable.add(createMoveOption(skin, Direction.UP)).width(650);
            moveButtonTable.row().expandX().fillX();
        }

        if (x + 1 < mapWidth) {
            moveButtonTable.add(createMoveOption(skin, Direction.RIGHT)).width(650);
            moveButtonTable.row().expandX().fillX();
        }


        if (y + 1 < mapHeight) {
            moveButtonTable.add(createMoveOption(skin, Direction.DOWN)).width(650);
            moveButtonTable.row().expandX().fillX();
        }

        if (x - 1 >= 0) {
            moveButtonTable.add(createMoveOption(skin, Direction.LEFT)).width(650);
            moveButtonTable.row().expandX().fillX();
        }

        add(moveButtonTable);
    }

    private Table createMapGraphics(Skin skin){

        List<List<Map.Entry<Index,Area>>> visibleMap = new ArrayList<>();
        int up = player.getPosition().y() - 5;
        int down = player.getPosition().y() + 5;
        int left = player.getPosition().x() - 5;
        int right = player.getPosition().x() + 5;

        for (int i = up; i <= down; i++) {
            if (up < 0 || screen.getMap().length < down) continue;
            List<Map.Entry<Index,Area>> row = new ArrayList<>();
            for (int j = left; j <= right; j++) {
                if (left < 0 || screen.getMap()[i].length < right) continue;
                row.add(new AbstractMap.SimpleEntry<>(new Index(i,j),screen.getMap()[i][j]));
            }
            visibleMap.add(row);
        }

        Table table = new Table();
        for (List<Map.Entry<Index,Area>> row : visibleMap) {
            for (Map.Entry <Index,Area> entry : row) {
                var index = entry.getKey();
                var area = entry.getValue();
                if (area == screen.getMap()[player.getPosition().y()][player.getPosition().x()]) {
                    table.add(new Image(skin.newDrawable("circle",Color.GOLD))).size(30);
                    continue;
                }

                if(player.getAreasVisited().contains(index)) {
                    table.add(new Image(skin.newDrawable(area.getType().getIconKey(),Color.WHITE))).size(40);
                    continue;
                }
                if (player.getMinutes() % 24 < 6 || 18 < player.getMinutes() % 24) {
                    table.add(new Image(skin.newDrawable("question_mark",Color.GRAY))).size(40);
                    continue;
                }
                    table.add(new Image(skin.newDrawable(area.getType().getIconKey(),Color.GRAY))).size(40);
            }
            table.row();
        }

        return table;
    }

    private Button createMoveOption(Skin skin, Direction direction) {

        Index targetIndex = player.getPosition().add(direction.getVector());
        Area targetArea = screen.getMap()[targetIndex.y()][targetIndex.x()];


        Table card = new Table();
        Label directionLabel = new Label(direction.getCardinalName().toUpperCase(),new Label.LabelStyle(textRegularFont, Color.DARK_GRAY));
        Label areaNameLabel = new Label(targetArea.getName(), new Label.LabelStyle(textRegularFont,Color.WHITE));
        Label distanceLabel = new Label(targetArea.getDistance() + " km", new Label.LabelStyle(textRegularFont,Color.WHITE));
        distanceLabel.setAlignment(Align.right);
        Image areaIcon = new Image(skin.newDrawable("white"));
        Label description = new Label("", new Label.LabelStyle(textLightFont,Color.WHITE));
        description.setWrap(true);
        description.setWidth(500);
        description.setText(targetArea.getType().getDescriptions()[Utils.random.nextInt(0,targetArea.getType().getDescriptions().length)]);
        areaIcon.setDrawable(skin.newDrawable(targetArea.getType().getIconKey(),Color.WHITE));

        card.pad(18.0f);
        card.add(directionLabel).left().fillX();
        card.add(areaIcon).size(45).right();
        card.row().expandX().fillX();
        card.add(areaNameLabel).fillX();
        card.add(distanceLabel).fillX();
        card.row().expandX().fillX();
        card.add(description).width(600).colspan(2).fillX();

        Button.ButtonStyle buttonStyle = new Button.ButtonStyle(skin.newDrawable("white",new Color(0.5f,0.5f,0.5f, 0.2f)), skin.newDrawable("white",new Color(0.5f,0.5f,0.5f, 0.5f)), skin.newDrawable("white",new Color(0.0f,0.0f,0.0f, 0.0f)));
        buttonStyle.disabled = skin.newDrawable("white",new Color(0.2f,0.1f,0.1f,0.3f));

        Button button = new Button(card,buttonStyle);

        /*
        * Disable button when energy is too low
        * Computation 2 energy per distance
        */
        if (player.getEnergy() < (Math.abs(targetArea.getDistance()) * 2)){
            button.setDisabled(true);
            areaNameLabel.setColor(Color.DARK_GRAY);
            description.setColor(Color.DARK_GRAY);
            description.setText("You don't have energy to travel");
            areaIcon.setColor(Color.DARK_GRAY);
        }

        /*
        * Disable button when inventory is too heavy
        * */
        if(player.getInventory().stream().mapToDouble(item -> Item.of(item).getWeight()).sum() > player.getInventoryCapacity()) {
            button.setDisabled(true);
            areaNameLabel.setColor(Color.DARK_GRAY);
            description.setColor(Color.DARK_GRAY);
            description.setText("You are carrying too many items.");
            areaIcon.setColor(Color.DARK_GRAY);
        }
        /*
         * Conceal area name when dark
         * */
        if (player.getMinutes() % 24 < 6 || 18 < player.getMinutes() % 24) {
            areaNameLabel.setText("???");
            description.setText("You can't figure out the place");
            areaIcon.setDrawable(skin.newDrawable("question_mark"));
        }

        /// TODO: Work on the layout of the buttons.
        /// TODO: Work on the UI of this screen.
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                DialogScreen dialog = null;

                if (player.getMinutes() % 24 < 6 || 18 < player.getMinutes() % 24) {
                    if (Utils.random.nextDouble() > 0.90) {
                        dialog = new DialogScreen("Arrived at " + targetArea.getName(), "You got injured along the way because it was too dark.");
                        player.setCondition(player.getCondition() - Utils.random.nextInt(5, 11));
                    }
                }

                screen.showLoadingScreen(new LoadingScreen(screen, "Travelling to " + areaNameLabel.getText(), dialog));
                player.setMinutes(player.getMinutes() + (targetArea.getDistance() / 5));
                player.setEnergy(player.getEnergy() - (targetArea.getDistance()));
                screen.setDistanceTravelled((screen.getDistanceTravelled() + targetArea.getDistance()));
                System.out.println(screen.getDistanceTravelled());
                player.getAreasVisited().add(player.getPosition().add(direction.getVector()));
                for (int i = 0; i < targetArea.getDistance(); i++) player.decay();
                screen.move(direction);
                closeScreen();
            }
        });

        return button;
    }

    private void closeScreen() {
        setVisible(false);
    }
}
