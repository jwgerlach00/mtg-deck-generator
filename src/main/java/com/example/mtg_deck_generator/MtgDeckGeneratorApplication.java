package com.example.mtg_deck_generator;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Set;

@SpringBootApplication
public class MtgDeckGeneratorApplication implements CommandLineRunner {

	private CardService service;

	public MtgDeckGeneratorApplication(CardService service) {
		this.service = service;
	}

	public static void main(String[] args) {
		SpringApplication.run(MtgDeckGeneratorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

//		service.loadAllStandardLegalCards();

//		System.out.println(service.findByName("Abhorrent Oculus").getCardId());
//		service.deleteAll();
		List<Card> cards = service.findAll();

		System.out.printf("Number of standard legal cards: %s%n", cards.size());

		Set<Character> s1 = Set.of('U', 'B');

		System.out.println(service.findAllByColorIdentitySubset(s1).size());

//		Card c1 = service.findByCardId("673260913e514f54cb482be8");

//		System.out.println(c1.inColorIdentity(s1)); // colorless artifacts are in any color identity

//		System.out.println(s1.containsAll(service.findAllByName("Lilah, Undefeated Slickshot").getFirst().getColorIdentity()));
	}

}
