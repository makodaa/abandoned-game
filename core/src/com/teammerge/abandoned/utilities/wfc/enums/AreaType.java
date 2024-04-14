package com.teammerge.abandoned.utilities.wfc.enums;

import com.teammerge.abandoned.utilities.wfc.classes.Superpositions;

public enum AreaType {
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
            case COMMERCIAL_BLDG -> "CB";
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
            case FOREST -> new String[]{"Forest", "Range"};
            case VILLAGE -> new String[]{"Village"};
            case PARK -> new String[]{"Park"};
            case COMMERCIAL_BLDG -> new String[]{"Commercial Building"};
            case MALL -> new String[]{"Mall"};
            case FARM -> new String[]{"Farm"};
            case HOSPITAL -> new String[]{"Hospital"};
        };
    }

    public static final int UNIVERSAL = Superpositions.universal(values());
}