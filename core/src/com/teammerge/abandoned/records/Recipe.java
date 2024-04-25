package com.teammerge.abandoned.records;

import java.util.Arrays;
import java.util.List;

public record Recipe(RecipeSourceEntry[] sources, int resultCount, String resultId) {
    public boolean canBeCraftedWith(List<String> items) {
        return Arrays.stream(sources)
                .map(e -> items.stream().filter(id -> id.equals(e.id())).count() >= e.count())
                .reduce(true, (a, b) -> a && b);
    }
}
