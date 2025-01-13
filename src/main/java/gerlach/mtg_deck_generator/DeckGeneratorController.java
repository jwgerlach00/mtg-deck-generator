package gerlach.mtg_deck_generator;

import gerlach.mtg_deck_generator.card.CardService;
import gerlach.mtg_deck_generator.deck.Deck;
import gerlach.mtg_deck_generator.deck.DeckGenerator;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class DeckGeneratorController {

    private final CardService cardService;

    public DeckGeneratorController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/generateDecks")
    public ResponseEntity<String> generateDecks(
            @RequestParam("numDecks") @Min(1) @Max(500) int numDecks,
            @RequestParam("minNumColors") @Min(1) @Max(5) int minNumColors,
            @RequestParam("maxNumColors") @Min(1) @Max(5) int maxNumColors
    ) {
        DeckGenerator deckGenerator = new DeckGenerator(cardService);

        for (int i = 0; i < numDecks; i++) {
            Deck deck = deckGenerator.generateRandomDeck(minNumColors, maxNumColors);
            deck.setName("deck-%s".formatted(i));
			deck.writeToFile(System.getenv("WRITE_DIRECTORY"));
        }

        return ResponseEntity.ok("%d decks generated successfully.".formatted(numDecks));
    }

}
