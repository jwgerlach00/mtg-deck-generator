package com.example.mtg_deck_generator.deck;

import com.example.mtg_deck_generator.Filters;
import com.example.mtg_deck_generator.card.Card;
import com.example.mtg_deck_generator.card.CardService;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Slf4j
public class DeckGenerator {

    private static final int DECK_SIZE = 60;
    private static final char[] BASIC_MANA_SYMBOLS = {'W', 'U', 'B', 'R', 'G'};
    private static final Map<Character, String> LAND_NAMES = Map.of(
            'W', "Plains",
            'U', "Island",
            'B', "Swamp",
            'R', "Mountain",
            'G', "Forest"
    );

    private final CardService cardService;
    private final Random random;

    public DeckGenerator(CardService cardService) {
        this.cardService = cardService;
        random = new Random(1);
    }

    public Deck generateRandomDeck() {
        return generateRandomDeck(1, BASIC_MANA_SYMBOLS.length);
    }

    public Deck generateRandomDeck(int minColors, int maxColors) {
        if (minColors < 0 || maxColors < minColors || maxColors > BASIC_MANA_SYMBOLS.length) {
            throw new IllegalArgumentException("Invalid bounds for colors");
        }

        Set<Character> colorSet = new HashSet<>();

        // Determine number of colors to select based on the bounds
        int numColorsToSelect = random.nextInt(maxColors - minColors + 1) + minColors;

        // Choose the colors randomly within the bounds
        List<Character> availableColors = new ArrayList<>();
        for (char basicManaSymbol : BASIC_MANA_SYMBOLS) {
            availableColors.add(basicManaSymbol);
        }

        Collections.shuffle(availableColors, random); // Shuffle colors to randomize selection
        for (int i = 0; i < numColorsToSelect; i++) {
            colorSet.add(availableColors.get(i)); // Add a random color from the shuffled list
        }

        List<Card> cardsInColors = cardService.findAllByColorIdentitySubset(colorSet);

        // Choose mana curve template randomly
        ManaCurveTemplate manaCurveTemplate = switch (random.nextInt(3)) {
            case 0 -> ManaCurveTemplate.AGGRO;
            case 1 -> ManaCurveTemplate.CONTROL;
            case 2 -> ManaCurveTemplate.MIDRANGE;
            default -> throw new RuntimeException("No mana curve template");  // this should not happen
        };

        log.info("Generating deck with color identity: {} and mana curve: {}", colorSet, manaCurveTemplate);

        return generateSampledDeck(cardsInColors, colorSet, manaCurveTemplate);
    }


    private Deck generateSampledDeck(List<Card> cardPool, Set<Character> colorSet, ManaCurveTemplate manaCurveTemplate) {
        Map<BigDecimal, Integer> manaCurve = manaCurveTemplate.getManaCurve();

        Deck deck = new Deck(colorSet);

        sampleAndAddCardsToDeck(cardPool, deck, manaCurve);

        int numLands = DECK_SIZE - deck.size();
        addLandsToDeck(deck, numLands);

        if (deck.size() != DECK_SIZE) {
            throw new RuntimeException("Deck with size: %s generated instead of size: %s"
                    .formatted(deck.size(), DECK_SIZE));
        }

        return deck;
    }

    private void sampleAndAddCardsToDeck(List<Card> cardPool, Deck deck, Map<BigDecimal, Integer> manaCurve) {
        for (BigDecimal cmc : manaCurve.keySet()) {
            // Get all cards that align with the cmc
            int num = manaCurve.get(cmc);
            List<Card> cmcCards = Filters.convertedManaCost(cardPool, cmc);

            // Add random card that aligns with the cmc
            while (num > 0) {
                int randIndex = random.nextInt(cmcCards.size());
                deck.add(cmcCards.get(randIndex));
                num -= 1;
            }
        }
    }

    private void addLandsToDeck(Deck deck, int numLands) {
        Map<Character, Integer> manaDist = getManaColorDist(deck);
        // Color identity: [R, B, U, G, W]; Mana distribution: {B=10, R=8, U=6, G=9, W=9}  // this one failed due to Unexpected number of lands
//        manaDist = Map.of('B', 5, 'R', 3, 'W', 1);  // for this example use 5 for numLands
        log.info("Mana distribution: {}", manaDist);
        Map<Character, Integer> basicLandDist = getBasicLandDist(manaDist, numLands);
        log.info("Basic land distribution: {}", basicLandDist);

        for (Map.Entry<Character, Integer> land : basicLandDist.entrySet()) {
            int n = land.getValue();
            String landName = LAND_NAMES.get(land.getKey());
            log.info("Adding {} {}", n, landName);

            for (int i = 0; i < n; i++) {
                deck.add(cardService.findAllByName(landName).getFirst());
            }
        }
    }

    private static Map<Character, Integer> getBasicLandDist(Map<Character, Integer> manaColorDist, int numLands) {
        BigDecimal totalManaNum = manaColorDist.values().stream()
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add);  //sum

        // Convert to map and copy for modification
        Map<Character, BigDecimal> bdManaDist = new HashMap<>();
        for (Map.Entry<Character, Integer> entry : manaColorDist.entrySet()) {
            bdManaDist.put(entry.getKey(), new BigDecimal(entry.getValue()));
        }

        Map<Character, Integer> landDist = new HashMap<>();

        // Start with at least 1 land per color
        for (char symbol : manaColorDist.keySet()) {
            landDist.put(symbol, 1);
        }

        // Iteratively add a land for the mana color with the highest count
        // Each time, alter the mana color dist by subtracting factor from mana color count
        BigDecimal landFactor = totalManaNum.divide(BigDecimal.valueOf(numLands), 3, RoundingMode.HALF_UP);
        while (landDist.values().stream().mapToInt(Integer::intValue).sum() < numLands) {
            Optional<Map.Entry<Character, BigDecimal>> maxEntry = bdManaDist.entrySet().stream()
                    .max(Map.Entry.comparingByValue());

            if (maxEntry.isEmpty()) {
                throw new RuntimeException("Unexpected empty lands per mana.");
            }

            char basicManaSymbol = maxEntry.get().getKey();
            landDist.put(basicManaSymbol, landDist.get(basicManaSymbol) + 1);
            bdManaDist.put(basicManaSymbol, maxEntry.get().getValue().subtract(landFactor));
        }

        return landDist;
    }

    private static Map<Character, Integer> getManaColorDist(Deck deck) {
        Map<Character, Integer> manaDist = new HashMap<>();

        for (Card card : deck) {
            Map<String, Integer> manaCount = card.getManaCount();
            if (manaCount == null) continue;
            for (Map.Entry<String, Integer> entry : manaCount.entrySet()) {
                String mana = entry.getKey();
                Integer count = entry.getValue();
                for (char symbol : BASIC_MANA_SYMBOLS) {
                    // If basic mana is in symbol, add count
                    if (mana.indexOf(symbol) != -1) {
                        manaDist.put(symbol, manaDist.getOrDefault(symbol, 0) + count);
                    }
                }
            }
        }
        return manaDist;
    }

}
