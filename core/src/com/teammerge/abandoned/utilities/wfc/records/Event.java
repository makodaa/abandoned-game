package com.teammerge.abandoned.utilities.wfc.records;

import com.teammerge.abandoned.records.Index;

import java.util.HashMap;
import java.util.HashSet;

public record Event(Index index, HashSet<Index> removals, HashMap<Index, Integer> propagationMap, int tried) {}