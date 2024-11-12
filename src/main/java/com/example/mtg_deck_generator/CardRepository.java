package com.example.mtg_deck_generator;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CardRepository extends MongoRepository<Card, String> {

    public Card findByCardId(String cardId);

    public Card findByScryfallId(UUID scryfallId);

    public List<Card> findAllByName(String name);
}
