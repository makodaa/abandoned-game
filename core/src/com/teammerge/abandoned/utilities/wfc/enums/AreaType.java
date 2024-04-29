package com.teammerge.abandoned.utilities.wfc.enums;

import com.teammerge.abandoned.records.Item;
import com.teammerge.abandoned.utilities.items.ItemRepository;
import com.teammerge.abandoned.utilities.wfc.classes.Superpositions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public enum AreaType implements Serializable {
    RESCUE_AREA,
    FOREST,
    VILLAGE,
    PARK,
    COMMERCIAL_BLDG,
    MALL,
    FARM,
    HOSPITAL,
    ;

    public static AreaType from(int index) {
        return AreaType.values()[index];
    }
    
    public String getAlias() {
        return switch (this) {
            case RESCUE_AREA -> "Ra";
            case FOREST -> "Fo";
            case VILLAGE -> "Vi";
            case PARK -> "Pa";
            case COMMERCIAL_BLDG -> "Cb";
            case MALL -> "Ma";
            case FARM -> "Fa";
            case HOSPITAL -> "Ho";
        };
    }

    public /* Superposition */ int getCompatibilities() {
        return switch (this) {
            case RESCUE_AREA -> Superpositions.createFrom(FOREST);
            case FOREST -> Superpositions.createFrom(FOREST, RESCUE_AREA, VILLAGE, PARK, FARM);
            case VILLAGE -> Superpositions.createFrom(VILLAGE, COMMERCIAL_BLDG, PARK, FOREST, FARM);
            case PARK -> Superpositions.createFrom(VILLAGE, FOREST, COMMERCIAL_BLDG);
            case COMMERCIAL_BLDG -> Superpositions.createFrom(VILLAGE, MALL, PARK, HOSPITAL);
            case MALL -> Superpositions.createFrom(COMMERCIAL_BLDG);
            case FARM -> Superpositions.createFrom(VILLAGE, FOREST);
            // @Ignore("Duplicate Case")
            case HOSPITAL -> Superpositions.createFrom(COMMERCIAL_BLDG);
        };
    }

    /**
     * Returns possible suffixes for area names, such as "Chevalier [School]" or "Mangrove [Farm]."
     * @return the array of suffixes
     */
    // TODO: Add more options. Possibly change this when needed.
    public String[] getSuffixes() {
        return switch (this) {
            case RESCUE_AREA -> new String[]{"Rescue Area"};
            case FOREST -> new String[]{"Forest", "Grove", "Reserve"};
            case VILLAGE -> new String[]{"Village", "Subdivision", "Homes", "Hills"};
            case PARK -> new String[]{"Park", "Reserve", "Plaza"};
            case COMMERCIAL_BLDG -> new String[]{"Complex","District", "Avenue", "Junction"};
            case MALL -> new String[]{"Malls", "Super Mall",};
            case FARM -> new String[]{"Farm", "Fields", "Acres", "Pastures"};
            case HOSPITAL -> new String[]{"Hospital", "Clinic", "Medical Centre"};
        };
    }

    /**
    * Returns possible prefixes for area names, such as "Blue rose [suffix]"
    * @return array of name combination
    */
    public String[] getPrefixes() {
        return switch (this) {
            case RESCUE_AREA -> new String[]{
                    "American Aid",
                    "American Safety",
                    "Camp Bravo",
                    "East National",
                    "Gen. Santos",
                    "Gen. Luna",
                    "Gen. Garcia",
                    "North National",
                    "Party Delta",
                    "Philippine National",
                    "Sector Alpha",
                    "Sector Charlie",
                    "Sector Foxtrot",
                    "Survivors United",
                    "South Eagle",
                    "South National",
                    "Swiss United",
                    "Southeast United",
                    "West-East United",
                    "West National"
            };

            case FOREST -> new String[]{
                    "Cerulean",
                    "Boreal",
                    "Dynasty",
                    "Ebonwood",
                    "Firefly",
                    "Hollow Purple",
                    "National Green",
                    "National Birch",
                    "National Oak",
                    "Pearlwood",
                    "Philippine Grand",
                    "Philippine Protected",
                    "Philippine Spruce",
                    "Philippine Oak",
                    "Shadewood",
                    "Sunlight",
                    "Unnamed",
                    "Unrecognizable",
                    "Viridian",
                    "Vermont",

            };
            case VILLAGE -> new String[]{
                    "Brightwood",
                    "Cedar",
                    "Countryside",
                    "Diggers",
                    "Evergreen",
                    "Everest",
                    "Forestside",
                    "Green Meadow",
                    "Maplewood",
                    "Mountainview",
                    "Pinesville",
                    "Provident",
                    "Queensville",
                    "Springtime",
                    "Summertime",
                    "Sunflower",
                    "Unrecognizable",
                    "Unnamed",
                    "White Plains",
                    "Willow",

            };
            case PARK -> new String[]{
                    "Bonifacio",
                    "Central",
                    "Cityscape",
                    "Downtown",
                    "Evergreen",
                    "Freedom",
                    "Heroes Memorial",
                    "Lily",
                    "National",
                    "Mabini",
                    "Mayfair",
                    "Peoples",
                    "Seo June",
                    "Serenity",
                    "Starlight",
                    "Starling",
                    "Townscape",
                    "Ventnor",
                    "Unnamed",
                    "Unrecognizable",
                    "White Dove"
            };
            case COMMERCIAL_BLDG -> new String[]{
                    "Unnamed",
                    "Araneta",
                    "Boston",
                    "Business",
                    "Ciel",
                    "Cityscape",
                    "Commerce",
                    "Country Trade",
                    "Diggers",
                    "Downtown",
                    "Easton",
                    "Fleeting",
                    "Leicester",
                    "Marcos",
                    "Marlborough",
                    "Mayfair",
                    "Oxford",
                    "Rizal",
                    "Ventnor",
                    "Wall Street",

            };
            case MALL -> new String[]{
                    "Broadwalk",
                    "Central",
                    "Diggers",
                    "Fresh Goods",
                    "General Goods",
                    "Jazz",
                    "Kostko",
                    "Pacific",
                    "Pall",
                    "Palm",
                    "States",
                    "Shopper's",
                    "Solid Goods",
                    "Saver's",
                    "Star",
                    "Trafalgar",
                    "Magnolia",
                    "Unrecognizable",
                    "Ventnor",
                    "Whitehall"
            };
            case FARM -> new String[]{
                    "Big",
                    "Bumble",
                    "Bountiful",
                    "Cotton Pick's",
                    "Diggers",
                    "Farmer Joseph",
                    "Fresh Picks",
                    "Large Harvest",
                    "Homestead",
                    "Gepard's",
                    "Old MacDonald",
                    "Peaceful",
                    "Plentiful",
                    "Rustic",
                    "Serenity",
                    "Sycamore",
                    "Tranquil",
                    "Unnamed",
                    "Verdant",
                    "Wildwood",
            };
            case HOSPITAL -> new String[]{
                    "Ace",
                    "Better Care",
                    "City National",
                    "Country Health",
                    "East Regional",
                    "Evercare",
                    "Far East",
                    "General Health",
                    "Golden Crest",
                    "Honored One",
                    "Horizon",
                    "Lazarus",
                    "McKinely",
                    "Metro City",
                    "National Children's",
                    "Peoples",
                    "Providence",
                    "Stand Proud",
                    "St. Bonaventure's",
                    "Veterans",
            };
        };
    }

    private HashMap<String, ArrayList<String>> loadedLootTables;

    private void loadLootTables() {
        if (loadedLootTables != null) return;

        loadedLootTables = new HashMap<>();

        for (AreaType type : AreaType.values()) {
            loadedLootTables.put(type.getAlias().toUpperCase(), new ArrayList<>());
        }

        for (Item item : ItemRepository.getAllItems()) {
            for (String location : item.locationSpawns()) {
                if (location.trim().isEmpty()) continue;

                if (location.equals("ALL")) {
                    for (AreaType type: AreaType.values()) {
                        loadedLootTables.get(type.getAlias().toUpperCase()).add(item.id());
                    }
                    continue;
                }

                ArrayList<String> list = loadedLootTables.get(location.toUpperCase());
                if (list == null) {
                    System.out.println("Detected an unknown id: '" + location + "'");
                    continue;
                }

                list.add(item.id());
            }
        }
    }

    public String[] getLootTable() {
        loadLootTables();

        return loadedLootTables.get(this.getAlias().toUpperCase()).toArray(new String[0]);
    }
    
    public String getBackgroundFolders() {
        return switch (this) {
            case RESCUE_AREA -> "rescue_camp";
            case FOREST, PARK -> "forest";
            case VILLAGE, FARM -> "village";
            case COMMERCIAL_BLDG, MALL, HOSPITAL -> "city";
        };
    }

    public static final int UNIVERSAL = Superpositions.universal(values());
}