package gerlach.mtg_deck_generator.card;

import gerlach.mtg_deck_generator.api.ScryfallApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class CardIterator implements Iterator<Card> {

    private final ScryfallApiClient apiClient;
    private Iterator<JsonNode> currentPageIterator;

    public CardIterator(ScryfallApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public boolean hasNext() {
        // Fetch the next page if the current iterator is empty or null
        if ((currentPageIterator == null || !currentPageIterator.hasNext()) && apiClient.hasMorePages()) {
            fetchNextPage();
        }
        return currentPageIterator != null && currentPageIterator.hasNext();
    }

    @Override
    public Card next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        JsonNode cardNode = currentPageIterator.next();
        return Card.loadFromJson(cardNode);
    }

    private void fetchNextPage() {
        JsonNode data = apiClient.fetchNextPage();
        if (data != null) {
            currentPageIterator = data.elements();
        } else {
            currentPageIterator = null;
        }
    }
}
