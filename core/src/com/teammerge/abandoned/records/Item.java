package com.teammerge.abandoned.records;

public record Item(
        String id,
        String description,
        String iconPath,
        double weight,

        int effectCondition,
        int effectFullness,
        int effectHydration,
        int effectEnergy,
        boolean isUsable,
        boolean naturallyGenerates,
        boolean canBeCrafted,
        // Recipe[] recipes /// TODO: Item Recipe Format.
        String[] locationSpawns
        // TODO: The necessary item properties, such as description? icon path? What else are needed?
) {}