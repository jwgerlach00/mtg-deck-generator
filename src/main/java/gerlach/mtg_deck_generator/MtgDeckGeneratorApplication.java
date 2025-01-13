package gerlach.mtg_deck_generator;

import gerlach.mtg_deck_generator.card.Card;
import gerlach.mtg_deck_generator.card.CardService;
import gerlach.mtg_deck_generator.deck.Deck;
import gerlach.mtg_deck_generator.deck.DeckGenerator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class MtgDeckGeneratorApplication implements CommandLineRunner {

	private final CardService cardService;

	public MtgDeckGeneratorApplication(CardService cardService) {
		this.cardService = cardService;
	}

	public static void main(String[] args) {
		SpringApplication.run(MtgDeckGeneratorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

//		cardService.loadAllStandardLegalCards();
//		cardService.deleteAll();

		List<Card> cards = cardService.findAll();
		System.out.printf("Number of standard legal cards: %s%n", cards.size());
		DeckGenerator deckGenerator = new DeckGenerator(cardService);
		for (int i = 0; i < 10; i++) {
			Deck deck = deckGenerator.generateRandomDeck(2, 2);
			deck.setName("deck-%s".formatted(i));
			deck.writeToFile(System.getenv("WRITE_DIRECTORY"));
		}

//		Card c1 = service.findByCardId("673260913e514f54cb482be8");

//		System.out.println(c1.inColorIdentity(s1)); // colorless artifacts are in any color identity

//		System.out.println(s1.containsAll(service.findAllByName("Lilah, Undefeated Slickshot").getFirst().getColorIdentity()));
	}

}
