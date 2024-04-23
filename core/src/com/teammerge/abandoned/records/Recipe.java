package com.teammerge.abandoned.records;

public record Recipe(RecipeSourceEntry[] sources, int resultCount, String resultId) {
}
