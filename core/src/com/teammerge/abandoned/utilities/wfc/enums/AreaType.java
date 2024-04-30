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
     *
     * @return the array of suffixes
     */
    // TODO: Add more options. Possibly change this when needed.
    public String[] getSuffixes() {
        return switch (this) {
            case RESCUE_AREA -> new String[]{"Rescue Area"};
            case FOREST -> new String[]{"Forest", "Grove", "Reserve"};
            case VILLAGE -> new String[]{"Village", "Subdivision", "Homes", "Hills"};
            case PARK -> new String[]{"Park", "Reserve", "Plaza"};
            case COMMERCIAL_BLDG -> new String[]{"Complex", "District", "Avenue", "Junction"};
            case MALL -> new String[]{"Malls", "Super Mall",};
            case FARM -> new String[]{"Farm", "Fields", "Acres", "Pastures"};
            case HOSPITAL -> new String[]{"Hospital", "Clinic", "Medical Centre"};
        };
    }

    /**
     * Returns possible prefixes for area names, such as "Blue rose [suffix]"
     *
     * @return array of prefixes
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

    /**
     * Returns possible descriptions for areas
     *
     * @return array of descriptions
     * */
    public String[] getDescriptions(){
        return switch (this){
            case RESCUE_AREA -> new String[]{
                    "Safety in-sight.",
                    "A makeshift haven amid the chaos, where hope flickers in the eyes of survivors awaiting salvation.",

            };
            case FOREST -> new String[]{
                    "A deep, lush forest. Can find scrap wood, vegetation, and wildlife",
                    "Nature's fortress, where ancient trees whisper secrets and danger lurks beneath every shadow.",
                    "Nature reclaims its dominion, where eerie whispers and tangled undergrowth cloak the remnants of civilization's retreat.",
                    "Nature's cathedral, where towering trees stand sentinel over forgotten trails and hidden secrets buried beneath a carpet of fallen leaves."
            };
            case VILLAGE -> new String[]{
                    "A safe subdivision with water and vegetation. Can Find leftover emergency supplies and rations.",
                    "Deserted streets bear witness to the absence of life, with decaying homes and shattered memories strewn among forgotten dreams.",
                    "Silent streets wind through deserted homes, where curtains flutter in the wind as if mourning the absence of life that once filled these quiet corners."
            };
            case PARK -> new String[]{
                    "The middle ground between nature and the city. Can find water, wood, wildlife, and emergency supplies",
                    "Once vibrant with laughter and joy, now a haunting tableau of overgrown pathways and rusting play structures, frozen in time.",
                    "A sanctuary reclaimed by nature's embrace, where tangled vines ensnare forgotten benches and crumbling statues stand as silent sentinels in a sea of greenery."
            };
            case COMMERCIAL_BLDG -> new String[]{
                    "Shops, Shops, and Shops. Can find emergency supplies and survival equipment",
                    "Empty storefronts and shattered glass reflect the hollow echoes of consumerism, where commerce once thrived but now lies dormant.",
                    "Empty shelves and shattered displays bear witness to the frenzy of looting that preceded the collapse, leaving behind a ghostly reminder of bustling commerce now reduced to silence.",
                    ""
            };
            case MALL -> new String[]{
                    "Palamig muna, init e",
                    "Can find emergency supplies, and survival equipment ",
                    "A labyrinth of desolation, where empty corridors and shattered skylights cast shadows upon the relics of consumer culture, now reclaimed by decay.",
                    "A sprawling monument to excess and abandonment, where escalators stand motionless and echoes of past footfalls fade into the eerie stillness of empty storefronts."
            };
            case FARM -> new String[]{
                    "A Farm. Can find scraps, rations, and equipment.",
                    "Fields lie fallow, machinery rusts beneath open skies, and abandoned homesteads stand as silent monuments to humanity's struggle for survival.",
                    "Fields lie fallow beneath a vast sky, where the remnants of crops sway in the breeze like ghostly echoes of a once-thriving agricultural community."
            };
            case HOSPITAL -> new String[]{
                    "A Hospital. Can find rations and medical supplies",
                    "A haunting sanctuary of broken promises and lost battles, where the stale scent of antiseptic mingles with the specter of despair, echoing through empty corridors.",
                    "A grim mausoleum of shattered hopes and fading memories, where the scent of decay mingles with the sterile chill of abandoned operating rooms, now haunted by the specter of lives lost."
            };
        };
    }

    public String getIconKey() {
        return switch(this){
            case RESCUE_AREA -> "rescue_area_icon";
            case FOREST -> "forest_icon";
            case VILLAGE -> "village_icon";
            case PARK -> "park_icon";
            case COMMERCIAL_BLDG -> "district_icon";
            case MALL -> "mall_icon";
            case FARM -> "farm_icon";
            case HOSPITAL -> "hospital_icon";
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
                    for (AreaType type : AreaType.values()) {
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
    public String getBackgroundMusic() {
        return switch (this) {
            case FOREST, VILLAGE, FARM, PARK, RESCUE_AREA -> "forest.wav";
            case MALL, COMMERCIAL_BLDG, HOSPITAL -> "ruined_city.mp3";
        };
    }

    public static final int UNIVERSAL = Superpositions.universal(values());
}