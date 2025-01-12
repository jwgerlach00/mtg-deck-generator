package com.example.mtg_deck_generator;

import com.example.mtg_deck_generator.card.Card;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Utils {

    public static Set<String> getAllManaTypes(List<Card> cards) {
        Set<String> manaTypes = new HashSet<>();
        for (Card card : cards) {
            Map<String, Integer> manaCount = card.getManaCount();

            if (manaCount != null) {
                manaTypes.addAll(manaCount.keySet());
            }
        }
        return manaTypes;
    }

}
