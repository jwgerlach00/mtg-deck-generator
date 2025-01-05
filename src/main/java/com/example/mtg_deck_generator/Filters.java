package com.example.mtg_deck_generator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Filters {

    public static List<Card> colorIdentitySubset(List<Card> cardSuperset, Set<Character> colorIdentity) {
        return cardSuperset.stream()
                .filter(card -> card.inColorIdentity(colorIdentity))
                .collect(Collectors.toList());
    }

    public static List<Card> convertedManaCost(List<Card> cardSuperset, BigDecimal convertedManaCost) {
        return cardSuperset.stream()
                .filter(card -> card.getConvertedManaCost().compareTo(convertedManaCost) == 0)
                .toList();
    }

}
