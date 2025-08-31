package com.bost.wedding.invite.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "wedding")
@Data
public class WeddingConfig {
    private Invitation invitation = new Invitation();

    @Data
    public static class Invitation {
        private String baseUrl = "http://localhost:8080";
        private Card card = new Card();
        private Venue venue = new Venue();

        @Data
        public static class Card {
            private String templatePath = "/templates";
            private String outputPath = "/generated-cards";
        }

        @Data
        public static class Venue {
            private int maxGuests = 100;
            private int seatsPerTable = 8;
        }
    }
}
