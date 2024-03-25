package com.teammerge.abandoned.WFC.records;

import java.util.HashMap;
import java.util.HashSet;

public class Event {
    private final Index _index;
    private final HashSet<Index> _removals;
    private final HashMap<Index, Integer> _propagationMap;
    private final int _tried;

    public Event(Index index, HashSet<Index> removals, HashMap<Index, Integer> propagationMap, int tried) {
        _index = index;
        _removals = removals;
        _propagationMap = propagationMap;
        _tried = tried;
    }

    public Index index() {
        return _index;
    }

    public HashSet<Index> removals() {
        return _removals;
    }

    public HashMap<Index, Integer> propagationMap() {
        return _propagationMap;
    }

    public int tried() {
        return _tried;
    }
}
