package com.teammerge.abandoned.records;

import com.teammerge.abandoned.utilities.items.ItemRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Item(
        String id,
        String name,
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
        Recipe[] recipes,
        String[] locationSpawns
        // TODO: The necessary item properties, such as description? icon path? What else are needed?
) {
    static public Item of(String id) {
        return fromRow(ItemRepository.rowOf(id));
    }

    static public Item fromRow(String[] row) {
        return new Item(
                row[0],
                row[1],
                row[13],
                null,
                switch (row[2]) {
                    case "" -> 0.0;
                    case String v -> Double.parseDouble(v);
                },
                switch (row[3]) {
                    case "" -> 0;
                    case String v -> Integer.parseInt(v);
                },
                switch (row[4]) {
                    case "" -> 0;
                    case String v -> Integer.parseInt(v);
                },
                switch (row[5]) {
                    case "" -> 0;
                    case String v -> Integer.parseInt(v);
                },
                switch (row[6]) {
                    case "" -> 0;
                    case String v -> Integer.parseInt(v);
                },
                row[7].equals("yes"),
                row[8].equals("yes"),
                row[9].equals("yes"),
                parseRecipes(row[0], row[10]),
                row[11].split("\\s*;\\s*")
        );
    }

    static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    static private Recipe[] parseRecipes(String output, String rows) {
        ArrayList<Recipe> recipes = new ArrayList<>();
        String[] separateRecipes = rows.split("\\s*\\.\\s*");

        for (String recipe : separateRecipes) {
            if (recipe.isEmpty()) continue;

            int resultCount = 1;
            String[] split = recipe.split(" ");

            int limit = split.length - 1;
            if (isInteger(split[limit])) {
                resultCount = Integer.parseInt(split[limit]);
                limit -= 1;
            }

            recipes.add(new Recipe(
                    getRecipeSourceEntries(limit, split).toArray(new RecipeSourceEntry[0]),
                    resultCount,
                    output
            ));
        }

        return recipes.toArray(new Recipe[0]);
    }

    private static ArrayList<RecipeSourceEntry> getRecipeSourceEntries(int limit, String[] split) {
        ArrayList<RecipeSourceEntry> entries = new ArrayList<>();
        for (int i = 0; i <= limit; ++i) {
            boolean isPaired = isInteger(split[i]);

            int sourceCount = isPaired
                    ? Integer.parseInt(split[i])
                    : 1;

            String sourceId = isPaired
                    ? split[i + 1]
                    : split[i];

            RecipeSourceEntry entry = new RecipeSourceEntry(sourceCount, sourceId);
            entries.add(entry);
            if (isPaired) {
                i += 1;
            }
        }
        return entries;
    }
}