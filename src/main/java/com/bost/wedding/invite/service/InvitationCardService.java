package com.bost.wedding.invite.service;

import com.bost.wedding.invite.entity.Guest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
@Slf4j
public class InvitationCardService {
    @Value("${wedding.invitation.card.template-path:/templates}")
    private String templatePath;

    @Value("${wedding.invitation.card.output-path:/generated-cards}")
    private String outputPath;

    public String generateInvitationCard(Guest guest) {
        try {
            Path outputDir = Paths.get(outputPath);
            Files.createDirectories(outputDir);

            String cardHtml = generateCardHtml(guest);

            // Save to file
            String fileName = "invitation_" + guest.getInviteToken() + ".html";
            Path cardPath = outputDir.resolve(fileName);
            Files.writeString(cardPath, cardHtml);

            String cardUrl = "/api/v1/wedding/cards/" + fileName;
            log.info("Generated invitation card for {}: {}", guest.getGuestName(), cardUrl);

            return cardUrl;

        } catch (IOException e) {
            log.error("Error generating invitation card for guest: {}", guest.getGuestName(), e);
            throw new RuntimeException("Failed to generate invitation card", e);
        }
    }
    private String generateCardHtml(Guest guest) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Wedding Invitation - %s</title>
                <style>
                    body { 
                        font-family: 'Georgia', serif; 
                        background: linear-gradient(135deg, #f5f7fa 0%%, #c3cfe2 100%%);
                        margin: 0; 
                        padding: 20px; 
                        display: flex; 
                        justify-content: center; 
                        align-items: center; 
                        min-height: 100vh; 
                    }
                    .invitation-card { 
                        background: white; 
                        padding: 40px; 
                        border-radius: 15px; 
                        box-shadow: 0 10px 30px rgba(0,0,0,0.2); 
                        text-align: center; 
                        max-width: 500px; 
                        border: 3px solid #d4af37; 
                    }
                    .bride-groom { 
                        font-size: 28px; 
                        color: #d4af37; 
                        margin-bottom: 20px; 
                        font-weight: bold; 
                    }
                    .guest-name { 
                        font-size: 24px; 
                        color: #2c3e50; 
                        margin: 20px 0; 
                        font-style: italic; 
                    }
                    .wedding-details { 
                        font-size: 16px; 
                        color: #34495e; 
                        margin: 20px 0; 
                        line-height: 1.6; 
                    }
                    .seat-info { 
                        background: #f8f9fa; 
                        padding: 15px; 
                        border-radius: 8px; 
                        margin: 20px 0; 
                        border-left: 4px solid #d4af37; 
                    }
                    .footer { 
                        font-size: 12px; 
                        color: #7f8c8d; 
                        margin-top: 30px; 
                    }
                </style>
            </head>
            <body>
                <div class="invitation-card">
                    <div class="bride-groom">Bost & Mo'Sexy</div>
                    <h2>cordially invite</h2>
                    <div class="guest-name">%s</div>
                    <div class="wedding-details">
                        <p><strong>Date:</strong> December 21, 2025</p>
                        <p><strong>Time:</strong> 10:00 AM</p>
                        <p><strong>Venue:</strong> Grand Ballroom, City Hotel</p>
                        <p><strong>Address:</strong> 123 Opebi Street, City, State</p>
                    </div>
                    <div class="seat-info">
                        <p><strong>Your Reserved Seat:</strong> %s</p>
                    </div>
                    <p>We can't wait to celebrate with you!</p>
                    <div class="footer">
                        <p>Generated on: %s</p>
                    </div>
                </div>
            </body>
            </html>
            """,
                guest.getGuestName(),
                guest.getGuestName(),
                guest.getSeatNumber(),
                LocalDateTime.now()
        );
    }
}
