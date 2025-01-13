package gerlach.mtg_deck_generator.deck;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum ManaCurveTemplate {
    AGGRO(new HashMap<>() {{
        put(BigDecimal.valueOf(1.0), 8);
        put(BigDecimal.valueOf(2.0), 10);
        put(BigDecimal.valueOf(3.0), 8);
        put(BigDecimal.valueOf(4.0), 4);
        put(BigDecimal.valueOf(5.0), 2);  // 5+ mana cards
    }}),
    CONTROL(new HashMap<>() {{
        put(BigDecimal.valueOf(1.0), 2);
        put(BigDecimal.valueOf(2.0), 4);
        put(BigDecimal.valueOf(3.0), 6);
        put(BigDecimal.valueOf(4.0), 10);
        put(BigDecimal.valueOf(5.0), 8);  // 5+ mana cards
    }}),
    MIDRANGE(new HashMap<>() {{
        put(BigDecimal.valueOf(1.0), 4);
        put(BigDecimal.valueOf(2.0), 6);
        put(BigDecimal.valueOf(3.0), 10);
        put(BigDecimal.valueOf(4.0), 6);
        put(BigDecimal.valueOf(5.0), 4);  // 5+ mana cards
    }});

    // Getter for the mana curve map
    private final Map<BigDecimal, Integer> manaCurve;

    // Constructor to set the mana curve map for each deck type
    ManaCurveTemplate(Map<BigDecimal, Integer> manaCurve) {
        this.manaCurve = manaCurve;
    }

}
