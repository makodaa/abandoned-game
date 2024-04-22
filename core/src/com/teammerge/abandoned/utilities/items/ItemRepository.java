package com.teammerge.abandoned.utilities.items;


import com.badlogic.gdx.Gdx;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/// TODO: Change strategy. Read from the CSV and dynamically load instead.
public class ItemRepository {
    static public ArrayList<String[]> getCsvData() {
        ArrayList<String[]> list = new ArrayList<>();
        Path path = Gdx.files.internal("csv/item_info.csv").file().toPath();

        try (Reader reader = Files.newBufferedReader(path);
             CSVReader csvReader = new CSVReader(reader)) {
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                list.add(line);
            }
        } catch (IOException | CsvValidationException e) {
            return null;
        }

        return list;
    }

    static public String[] getItemIds() {
        ArrayList<String[]> items = getCsvData();

        if (items == null) {
            return new String[] {};
        }

        List<String[]> itemValues = items.subList(1, items.size());

        return itemValues.stream().map((v) -> v[0]).toList().toArray(new String[itemValues.size()]);
    }

    static public final String[] allItemIds = {
            "empty_bottle",
            "clean_water",
            "dirty_water",
            "energy_drink",
            "wild_berries",
            "edible_berries",
            "raw_fish",
            "cooked_fish",
            "raw_avian",
            "cooked_avian",
            "rotten_meat",
            "bandages",
            "medicine",
            "first_aid_kit",
            "cloth",
            "clothes",
            "rope",
            "plastic",
            "tinder",
            "stick",
            "firewood",
            "hardwood",
            "rubbish",
            "backpack",
            "matches",
            "fire_starter",
            "spear",
            "flashlight",
            "axe",
    };
}
