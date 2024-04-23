package com.teammerge.abandoned.utilities.items;


import com.badlogic.gdx.Gdx;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.teammerge.abandoned.records.Item;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/// TODO: Change strategy. Read from the CSV and dynamically load instead.
public class ItemRepository {
    static private ArrayList<String[]> loadedCsvData = null;

    static public List<Item> getAllItems() {
        return getItemRows().stream().map(Item::fromRow).toList();
    }

    static public ArrayList<String[]> getCsvData() {
        if (loadedCsvData != null) {
            return loadedCsvData;
        }

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

        return loadedCsvData = list;
    }

    static public List<String[]> getItemRows() {
        ArrayList<String[]> items = getCsvData();
        if (items == null) {
            return new ArrayList<>();
        }

        return items.subList(1, items.size());
    }

    static public String[] getItemIds() {
        ArrayList<String[]> items = getCsvData();

        if (items == null) {
            return new String[] {};
        }

        List<String[]> itemRows = getItemRows();

        return itemRows
                .stream()
                .map((v) -> v[0]).toList().toArray(new String[itemRows.size()]);
    }
}
