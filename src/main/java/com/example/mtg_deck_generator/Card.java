package com.example.mtg_deck_generator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.net.URI;
import java.util.*;

@Document(collection = "cards")
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Card {

    @Id
    private String cardId;

    @Getter
    static class Preview {
        @JsonProperty("previewed_at")
        Date previewedAt;
        @JsonProperty("source_uri")
        URI sourceUri;
        String source;
    }

    // Core card fields
    @JsonProperty("arena_id")
    private int arenaId;
    @JsonProperty("id")
    private UUID scryfallId;
    private String lang;
    @JsonProperty("mtgo_id")
    private int mtgoId;
    @JsonProperty("mtgo_foil_id")
    private int mtgoFoilId;
    @JsonProperty("multiverse_ids")
    private List<Integer> multiverseIds;
    @JsonProperty("tcgplayer_id")
    private int tcgplayerId;
    @JsonProperty("tcgplayer_etched_id")
    private int tcgplayerEtchedId;
    @JsonProperty("cardmarket_id")
    private int cardmarketId;
    //    private String object; // always card
    private String layout;
    @JsonProperty("oracle_id")
    private UUID oracleId;
    @JsonProperty("prints_search_uri")
    private URI printsSearchUri;
    @JsonProperty("rulings_uri")
    private URI rulingsUri;
    @JsonProperty("scryfall_uri")
    private URI scryfallUri;
    private URI uri;

    // Gameplay fields
    @JsonProperty("all_parts")
    private List<Card> allParts;
    @JsonProperty("card_faces")
    private List<Card> cardFaces;
    @JsonProperty("cmc")
    private BigDecimal convertedManaCost;
    @JsonProperty("color_identity")
    private Set<Character> colorIdentity;
    @JsonProperty("color_indicator")
    private List<Character> colorIndicator;
    private List<Character> colors;
    private String defense;
    @JsonProperty("edhrec_rank")
    private int edhrecRank;
    @JsonProperty("hand_modifier")
    private String handModifier;
    private List<String> keywords;
    private Map<String, String> legalities;
    @JsonProperty("life_modifier")
    private String lifeModifier;
    private String loyalty; // some non-numeric loyalties, such as X
    @JsonProperty("mana_cost")
    private String manaCost;
    private String name;
    @JsonProperty("oracle_text")
    private String oracleText;
    @JsonProperty("penny_rank")
    private int pennyRank;
    private String power; // some non-numeric powers, such as *
    @JsonProperty("produced_mana")
    private List<Character> producedMana;
    private boolean reserved;
    private String toughness; // some non-numeric powers, such as *
    @JsonProperty("type_line")
    private String typeLine;

    // Print fields
    private String artist;
    @JsonProperty("artist_ids")
    private List<String> artistIds;
    @JsonProperty("attraction_lights")
    private List<String> attractionLights; // NOTE: I don't know what type this is, I guessed String
    private boolean booster;
    @JsonProperty("border_color")
    private String borderColor;
    @JsonProperty("card_back_id")
    private UUID cardBackId;
    @JsonProperty("collector_number")
    private String collectorNumber;
    @JsonProperty("content_warning")
    private boolean contentWarning;
    private boolean digital;
    private List<String> finishes;
    @JsonProperty("flavor_name")
    private String flavorName;
    @JsonProperty("flavor_text")
    private String flavorText;
    @JsonProperty("frame_effects")
    private List<String> frameEffects;
    private String frame;
    @JsonProperty("full_art")
    private boolean fullArt;
    private List<String> games;
    @JsonProperty("highres_image")
    private boolean highresImage;
    @JsonProperty("illustration_id")
    private UUID illustrationId;
    @JsonProperty("image_status")
    private String imageStatus;
    @JsonProperty("image_uris")
    private Map<String, URI> imageUris;
    private boolean oversized;
    private Map<String, BigDecimal> prices;
    @JsonProperty("printed_name")
    private String printedName;
    @JsonProperty("printed_text")
    private String printedText;
    @JsonProperty("printed_type_line")
    private String printedTypeLine;
    private boolean promo;
    @JsonProperty("promo_types")
    private List<String> promoTypes;
    @JsonProperty("purchase_uris")
    private Map<String, URI> purchaseUris;
    private String rarity;
    @JsonProperty("related_uris")
    private Map<String, URI> relatedUris;
    @JsonProperty("released_at")
    private Date releasedAt;
    private boolean reprint;
    @JsonProperty("scryfall_set_uri")
    private URI scryfallSetUri;
    @JsonProperty("set_name")
    private String setName;
    @JsonProperty("set_search_uri")
    private URI setSearchUri;
    @JsonProperty("set_type")
    private String setType;
    @JsonProperty("set_uri")
    private URI setUri;
    private String set;
    @JsonProperty("set_id")
    private UUID setId;
    @JsonProperty("story_spotlight")
    private boolean storySpotlight;
    private boolean textless;
    private boolean variation;
    @JsonProperty("variation_of")
    private URI variationOf;
    @JsonProperty("security_stamp")
    private String securityStamp;
    private String watermark;
    private Preview preview;

    public boolean inColorIdentity(Set<Character> targetColorIdentity) {
        return targetColorIdentity.containsAll(this.colorIdentity);
    }

    public Map<String, Integer> getManaCount() {
        if (manaCost == null) {
            return null;
        }

        Map<String, Integer> manaCount = new HashMap<>();

        int i = 0;
        while (i < manaCost.length()) {
            if (manaCost.charAt(i) == '{') {
                int closingBracket = manaCost.indexOf('}', i);
                if (closingBracket != -1) {
                    String symbol = manaCost.substring(i + 1, closingBracket);

                    if (symbol.matches("\\d+")) {
                        // If it's a numeric value, treat it as generic mana
                        int genericMana = Integer.parseInt(symbol);
                        manaCount.put("generic", manaCount.getOrDefault("generic", 0) + genericMana);
                    } else {
                        // Otherwise, treat it as a specific symbol
                        manaCount.put(symbol, manaCount.getOrDefault(symbol, 0) + 1);
                    }

                    i = closingBracket;
                }
            }
            i++;
        }

        return manaCount;
    }

    // Loading JSON into the Card class
    public static Card loadFromJson(JsonNode cardJsonNode) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.treeToValue(cardJsonNode, Card.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
