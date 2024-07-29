package com.gimbuddy.models;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class SkillsModel {
    @Getter
    private static final Map<String, Integer> skillIds = new HashMap<>();

    static {
        skillIds.put("Overall", 0);
        skillIds.put("Attack", 1);
        skillIds.put("Defence", 2);
        skillIds.put("Strength", 3);
        skillIds.put("Constitution", 4);
        skillIds.put("Ranged", 5);
        skillIds.put("Prayer", 6);
        skillIds.put("Magic", 7);
        skillIds.put("Cooking", 8);
        skillIds.put("Woodcutting", 9);
        skillIds.put("Fletching", 10);
        skillIds.put("Fishing", 11);
        skillIds.put("Firemaking", 12);
        skillIds.put("Crafting", 13);
        skillIds.put("Smithing", 14);
        skillIds.put("Mining", 15);
        skillIds.put("Herblore", 16);
        skillIds.put("Agility", 17);
        skillIds.put("Thieving", 18);
        skillIds.put("Slayer", 19);
        skillIds.put("Farming", 20);
        skillIds.put("Runecrafting", 21);
        skillIds.put("Hunter", 22);
        skillIds.put("Construction", 23);
        skillIds.put("Summoning", 24);
    }
}