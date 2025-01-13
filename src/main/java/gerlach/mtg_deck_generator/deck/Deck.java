package gerlach.mtg_deck_generator.deck;

import gerlach.mtg_deck_generator.card.Card;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

@Slf4j
public class Deck implements Iterable<Card> {

    @Getter
    @Setter
    private String name;

    private final Set<Character> colorSet;
    private final List<Card> deck;
    private final Map<Card, Integer> cardCount;

    public Deck(Set<Character> colorSet) {
        this(UUID.randomUUID().toString(), colorSet);
    }

    public Deck(String name, Set<Character> colorSet) {
        this.name = name;
        this.colorSet = colorSet;
        deck = new ArrayList<>();
        cardCount = new HashMap<>();
    }

    @Override
    @NonNull
    public Iterator<Card> iterator() {
        return deck.iterator();
    }

    @Override
    public void forEach(Consumer<? super Card> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<Card> spliterator() {
        return Iterable.super.spliterator();
    }

    public Card get(int index) {
        return deck.get(index);
    }

    public void add(Card card) {
        if (!card.inColorIdentity(colorSet)) {
            throw new RuntimeException("Cannot add card with colorSet: %s to deck with colorSet: %s"
                    .formatted(card.getColorIdentity(), colorSet));
        }
        deck.add(card);
        cardCount.put(card, cardCount.getOrDefault(card, 0) + 1);
    }

    public int size() {
        return deck.size();
    }

    public void writeToFile() {
        writeToFile(".");
    }

    public void writeToFile(String directory) {
        String[] metadataLines = getMetadataLinesToWrite();
        List<String> mainLines = getMainLinesToWrite();

        String fileName = "%s/%s.dck".formatted(directory, name);

        log.info("Writing deck: {} to file: {}", name, fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String line : metadataLines) {
                writer.write(line);
                writer.newLine();
            }

            for (String line : mainLines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing deck to file: ", e);
        }

    }

    private String[] getMetadataLinesToWrite() {
        return new String[] {
                "[metadata]",
                "Name=%s".formatted(name),
                "Deck Type=%s".formatted("constructed"),
                "Description=%s".formatted("Randomly generated deck with color identity: %s.".formatted(colorSet))
        };
    }

    private List<String> getMainLinesToWrite() {
        List<String> mainLines = new ArrayList<>();
        mainLines.add("[Main]");

        for (Map.Entry<Card, Integer> entry: cardCount.entrySet()) {
            Card card = entry.getKey();
            // If double-sided card, only want to write first side
            String name;
            if (Objects.equals(card.getLayout(), "transform")) {
                name = card.getName().split("//")[0].strip();
            } else {
                name = card.getName();
            }
            mainLines.add("%s %s".formatted(entry.getValue(), name));
        }
        return mainLines;
    }

}
