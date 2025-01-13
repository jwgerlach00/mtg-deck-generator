package gerlach.mtg_deck_generator.database;

import gerlach.mtg_deck_generator.card.CardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DbInitializerService {

    private final CardService cardService;

    public DbInitializerService(CardService cardService) {
        this.cardService = cardService;
    }

    public void initializeDb() {
        log.info("DB initialization started.");
        cardService.deleteAll();
        cardService.loadAllStandardLegalCards();
        log.info("DB initialization complete.");
    }
}
