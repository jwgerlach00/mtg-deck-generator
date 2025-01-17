package gerlach.mtg_deck_generator.card;

import gerlach.mtg_deck_generator.Filters;
import gerlach.mtg_deck_generator.api.ScryfallApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class CardService {

    private final CardRepository repository;

    @Value("${scryfall.api.stem}")
    private String apiStem;

    public CardService(CardRepository repository) {
        this.repository = repository;
    }

    public void loadAllStandardLegalCards() {
        log.info("Loading all standard legal cards...");
        ScryfallApiClient apiClient = new ScryfallApiClient((apiStem + "cards/search?q=legal:standard"));
        CardIterator cardIterator = new CardIterator(apiClient);

        while (cardIterator.hasNext()) {
            Card card = cardIterator.next();
            repository.save(card);
        }
    }

    public void deleteAll() {
        log.info("Deleting all DB content...");
        repository.deleteAll();
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

