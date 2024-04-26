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
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.teammerge.abandoned.actors.drawables.BackgroundDrawable;
import com.teammerge.abandoned.entities.Player;
import com.teammerge.abandoned.enums.Direction;
import com.teammerge.abandoned.records.Index;
import com.teammerge.abandoned.screens.GameScreen;
import com.teammerge.abandoned.utilities.wfc.classes.Area;
import com.teammerge.abandoned.utilities.wfc.classes.Utils;
import com.teammerge.abandoned.utilities.wfc.enums.AreaType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TravelScreen extends Table {
    BackgroundDrawable backgroundDrawable;
    Player player;
    GameScreen screen;
    int distanceBetweenAreas;

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
        skin.add("city_icon", new Texture(Gdx.files.internal("images/icons/map/city.png")));
        skin.add("forest_icon", new Texture(Gdx.files.internal("images/icons/map/forest.png")));
        skin.add("village_icon", new Texture(Gdx.files.internal("images/icons/map/village.png")));
        skin.add("farm_icon", new Texture(Gdx.files.internal("images/icons/map/farm.png")));
        skin.add("hospital_icon", new Texture(Gdx.files.internal("images/icons/map/hospital.png")));
        skin.add("circle", new Texture(Gdx.files.internal("images/icons/map/circle.png")));

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


        Table atlasTable = createMapGraphics(skin,screen.getMap());
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

    private Table createMapGraphics(Skin skin, Area[][] map){

        List<List<Area>> visibleMap = new ArrayList<>();
        int up = player.getPosition().y() - 5;
        int down = player.getPosition().y() + 5;
        int left = player.getPosition().x() - 5;
        int right = player.getPosition().x() + 5;

        for (int i = up; i <= down; i++) {
            if (up < 0 || map.length < down) continue;
            List<Area> row = new ArrayList<>();
            for (int j = left; j <= right; j++) {
                if (left < 0 || map[i].length < right) continue;
                row.add(map[i][j]);
            }
            visibleMap.add(row);
        }

        Table table = new Table();
        for (List<Area> row : visibleMap) {
            for (Area area : row) {
                if (area == screen.getMap()[player.getPosition().y()][player.getPosition().x()]) {
                    table.add(new Image(skin.newDrawable("circle",Color.GOLD))).size(30);
                    continue;
                }
                switch (area.getType()) {
                    case VILLAGE -> table.add(new Image(skin.newDrawable("village_icon"))).size(40).pad(0.5f);
                    case FOREST, PARK, RESCUE_AREA -> table.add(new Image(skin.newDrawable("forest_icon"))).size(40).pad(0.5f);
                    case MALL, COMMERCIAL_BLDG -> table.add(new Image(skin.newDrawable("city_icon"))).size(40).pad(0.5f);
                    case FARM -> table.add(new Image(skin.newDrawable("farm_icon"))).size(40).pad(0.5f);
                    case HOSPITAL -> table.add(new Image(skin.newDrawable("hospital_icon"))).size(40).pad(0.5f);
                }
            }
            table.row();
        }

        return table;
    }

    private Button createMoveOption(Skin skin, Direction direction) {


        Index currentIndex = player.getPosition();
        Area currentArea = screen.getMap()[currentIndex.y()][currentIndex.x()];

        Index targetIndex = player.getPosition().add(direction.getVector());
        Area targetArea = screen.getMap()[targetIndex.y()][targetIndex.x()];


        Table card = new Table();
        Label directionLabel = new Label(direction.getCardinalName().toUpperCase(),new Label.LabelStyle(textRegularFont, Color.DARK_GRAY));
        Label areaNameLabel = new Label(targetArea.getName(), new Label.LabelStyle(textRegularFont,Color.WHITE));
        Image areaIcon = new Image(skin.newDrawable("white"));
        Label description = new Label("", new Label.LabelStyle(textLightFont,Color.WHITE));
        description.setWrap(true);
        description.setWidth(500);
        switch (targetArea.getType()) {
            case VILLAGE -> {
                description.setText("A safe subdivision with water and vegetation. Can Find leftover emergency supplies and rations.");
                areaIcon = new Image(skin.newDrawable("village_icon",Color.WHITE));
            }
            case FOREST -> {
                description.setText("A deep, lush forest. Can find scrap wood, vegetation, and wildlife");
                areaIcon = new Image(skin.newDrawable("forest_icon",Color.WHITE));
            }
            case PARK -> {
                description.setText("The middle ground between nature and the city. Can find water, wood, wildlife, and emergency supplies");
                areaIcon = new Image(skin.newDrawable("forest_icon",Color.WHITE));
            }
            case RESCUE_AREA -> {
                description.setText("Safety in-sight.");
                areaIcon = new Image(skin.newDrawable("forest_icon",Color.WHITE));
//            TODO: lmao
            }
            case MALL -> {
                description.setText("Palamig muna, init eh. Can find emergency supplies, and survival equipment ");
                areaIcon = new Image(skin.newDrawable("city_icon",Color.WHITE));
            }
            case COMMERCIAL_BLDG -> {
                description.setText("Something Shops. Can find emergency supplies, survival equipment, ");
                areaIcon = new Image(skin.newDrawable("city_icon",Color.WHITE));
            }
            case FARM -> {
                description.setText("Something Farm.");
                areaIcon = new Image(skin.newDrawable("farm_icon",Color.WHITE));
            }
            case HOSPITAL -> {
                description.setText("Something Hospital. Can find rations and medical supplies");
                areaIcon = new Image(skin.newDrawable("hospital_icon",Color.WHITE));
            }
        }

        card.pad(18.0f);
        card.add(directionLabel).left().fillX();
        card.add(areaIcon).size(45).right();
        card.row().expandX().fillX();
        card.add(areaNameLabel).colspan(2).fillX();
        card.row().expandX().fillX();
        card.add(description).width(600).colspan(2).fillX();

        Button.ButtonStyle buttonStyle = new Button.ButtonStyle(skin.newDrawable("white",new Color(0.5f,0.5f,0.5f, 0.2f)), skin.newDrawable("white",new Color(0.5f,0.5f,0.5f, 0.5f)), skin.newDrawable("white",new Color(0.0f,0.0f,0.0f, 0.0f)));

        Button button = new Button(card,buttonStyle);

        /*
        * Disables button when statistics are too low
        * Computation 2 energy per distance
        */
        distanceBetweenAreas = Math.abs(targetArea.getDistance() - currentArea.getDistance());
        if (player.getEnergy() < (distanceBetweenAreas * 2) + 10){
//            button.setText("Not Enough Energy");
            button.setDisabled(true);
        }

        /*
        * Conceal area name when dark
        * TODO: Work on UI
        * */
        if (player.getMinutes() % 24 < 6 || 18 < player.getMinutes() % 24) {
//            button.setText("??? | It's too hard to see.");
//            button.getLabel().setColor(Color.RED);
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

                screen.showLoadingScreen(new LoadingScreen(screen, "Travelling to " + targetArea.getName(), dialog));
                player.setMinutes(player.getMinutes() + (distanceBetweenAreas / 5));
                player.setEnergy(player.getEnergy() - (distanceBetweenAreas * 2));
                for (int i = player.getMinutes(); i < 3 * (player.getMinutes() + (distanceBetweenAreas / 5)); i++) {
                    player.decay();
                }
                screen.move(direction);
                closeScreen();
            }
        });

        return button;
    }

    private void closeScreen() {
        screen.getActiveScreens().remove(TravelScreen.class);
        setVisible(false);
    }
}
