package gerlach.mtg_deck_generator.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ScryfallApiClient {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private String nextUrl;

    public ScryfallApiClient(String initialUrl) {
        this.nextUrl = initialUrl;
    }

    /**
     * Fetches the next page of cards as a JsonNode array and updates the next page URL.
     * @return JsonNode containing the data array of cards, or null if there are no more pages.
     */
    public JsonNode fetchNextPage() {
        if (nextUrl == null) {
            return null;
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(nextUrl))
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                JsonNode data = root.path("data");

                // Update next URL for pagination
                nextUrl = root.path("next_page").asText(null);

                return data.isArray() ? data : null;
            } else {
                throw new IOException("Failed to fetch cards: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error fetching card data", e);
        }
    }

    /**
     * Returns whether there are more pages to fetch.
     */
    public boolean hasMorePages() {
        return nextUrl != null;
    }
}
