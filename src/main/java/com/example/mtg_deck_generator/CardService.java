package com.example.mtg_deck_generator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.logging.Filter;
import java.util.stream.Collectors;

@Service
public class CardService {

    private final CardRepository repository;

    @Value("${scryfall.api.stem}")
    private String apiStem;

    public CardService(CardRepository repository) {
        this.repository = repository;
    }

    public void loadAllStandardLegalCards() {
        ScryfallApiClient apiClient = new ScryfallApiClient((apiStem + "cards/search?q=legal:standard"));
        CardIterator cardIterator = new CardIterator(apiClient);

        while (cardIterator.hasNext()) {
            Card card = cardIterator.next();
            repository.save(card);
        }
    }

    public List<Card> findAll() {
        return repository.findAll();
    }

    public Card findByCardId(String cardId) {
        return repository.findByCardId(cardId);
    }

    public Card findByScryfallId(String scryfallId) {
        return repository.findByCardId(scryfallId);
    }

    /**
     * Returns multiple because there can be different printings of the same card. Or the same card can appear in
     * multiple sets.
     * @param name Name of card.
     * @return List of all Cards with name.
     */
    public List<Card> findAllByName(String name) {
        return repository.findAllByName(name);
    }

    public List<Card> findAllByColorIdentitySubset(Set<Character> colorIdentitySuperset) {
        List<Card> allCards = repository.findAll();
        return Filters.colorIdentitySubset(allCards, colorIdentitySuperset);
    }
}

