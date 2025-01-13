package gerlach.mtg_deck_generator.card;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CardRepository extends MongoRepository<Card, String> {

    Card findByCardId(String cardId);

    Card findByScryfallId(UUID scryfallId);

    List<Card> findAllByName(String name);
}
