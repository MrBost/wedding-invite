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
import java.time.format.DateTimeFormatter;

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

            String cardUrl = "/api/v1/invite/cards/" + fileName;
            log.info("Generated invitation card for {}: {}", guest.getGuestName(), cardUrl);

            return cardUrl;

        } catch (IOException e) {
            log.error("Error generating invitation card for guest: {}", guest.getGuestName(), e);
            throw new RuntimeException("Failed to generate invitation card", e);
        }
    }
    private String generateCardHtml(Guest guest) {
        String safeGuestName = guest.getGuestName()
                .trim()
                .replaceAll("[^a-zA-Z0-9]", "_");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy hh:mm a");
        String formattedDate = LocalDateTime.now().format(formatter);
        return String.format("""
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Wedding Invitation - %s</title>
    <style>
        body {
            font-family: 'Cormorant Garamond', serif;
            background: #fdfcfb;
            margin: 0;
            padding: 20px;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
        }
        .divider {
            width: 100%%;
            height: 2px;
            background-color: #D4AF37;
            margin: 10px auto 25px;
            border-radius: 2px;
        }
        .invitation-card {
            background: #fff;
            padding: 50px 30px;
            border-radius: 20px;
            box-shadow: 0 8px 25px rgba(0,0,0,0.15);
            max-width: 650px;
            width: 100%%;
            text-align: center;
            border: 6px double #d4af37;
            position: relative;
            overflow: hidden;
        }
        .invitation-card::before,
        .invitation-card::after {
            content: "";
            position: absolute;
            width: 120px;
            height: 120px;
            background: url('/images/floral-corner.png') no-repeat center/contain;
            z-index: 0;
        }
        .invitation-card::before {
            top: -10px;
            left: -10px;
        }
        .invitation-card::after {
            bottom: -10px;
            right: -10px;
            transform: rotate(180deg);
        }
        .header {
            font-family: 'Great Vibes', cursive;
            font-size: 50px;
            color: #d4af37;
            margin-bottom: 10px;
            position: relative;
            z-index: 1;
        }
        .families {
            font-style: italic;
            font-size: 18px;
            color: #7f8c8d;
            margin-bottom: 15px;
        }
        .couple {
            font-size: 32px;
            color: #2c3e50;
            margin-bottom: 10px;
            position: relative;
            z-index: 1;
        }
        .guest-name {
            font-size: 22px;
            margin: 25px 0 15px;
            font-style: italic;
            color: #34495e;
            position: relative;
            z-index: 1;
        }
        .invitation-text {
            font-size: 18px;
            color: #2c3e50;
            line-height: 1.8;
            margin: 10px 0 25px;
            position: relative;
            z-index: 1;
        }
        .wedding-details {
            font-size: 18px;
            line-height: 1.8;
            color: #2c3e50;
            margin: 20px 0;
            z-index: 1;
            position: relative;
        }
        .time {
            font-family: 'Great Vibes', cursive;
            font-size: 26px;
            color: #b4942f;
            margin-top: -5px;
            margin-bottom: 10px;
        }
        .seat-info {
            margin: 25px 0;
            padding: 15px;
            background: #faf3e0;
            border: 1px solid #d4af37;
            border-radius: 10px;
            font-size: 20px;
            font-weight: bold;
            color: #8e6e3d;
            z-index: 1;
            position: relative;
        }
        .footer {
            font-size: 14px;
            margin-top: 30px;
            color: #7f8c8d;
            z-index: 1;
            position: relative;
        }
        .footer .invitation-note {
            font-size: 16px;
            font-weight: 600;
            color: #d4af37;
            margin-top: 20px;
        }
        .footer h2 {
            font-size: 20px;
            font-weight: bold;
            color: #b03a2e;
            margin-top: 10px;
        }
        @media (max-width: 600px) {
            .invitation-card {
                padding: 25px 15px;
                font-size: 90%%;
            }
            .header {
                font-size: 36px;
            }
            .couple {
                font-size: 24px;
            }
        }
        .download-btn {
            margin-top: 20px;
            padding: 12px 20px;
            font-size: 16px;
            font-weight: bold;
            border: none;
            border-radius: 8px;
            background: #d4af37;
            color: white;
            cursor: pointer;
            transition: background 0.3s ease;
            z-index: 1;
            position: relative;
        }
        .download-btn:hover {
            background: #b4942f;
        }
    </style>
    <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@400;600&family=Great+Vibes&display=swap" rel="stylesheet">
</head>
<body>
    <div class="invitation-card" id="invitationCard">
        <div class="header">Wedding Invitation</div>
        <div class="families"><i>Together with our families</i></div>
        <div class="couple">%s</div>

        <div class="invitation-text">
            joyfully request the honor of your presence<br>
            as we celebrate the beginning of our forever.
        </div>

        <div class="guest-name">%s</div>
        <div class="divider"></div>

        <div class="wedding-details">
            <p><strong>Saturday, December 20, 2025</strong></p>
            <p class="time">10:00 AM – 2:00 PM</p>
            <p><strong>Advans Event Special</strong></p>
            <p><strong>44 Ayodele Close, off Allen Avenue, Ikeja, Lagos</strong></p>
        </div>

        <div class="seat-info">Reserved Seat: %s</div>

        <p>We would be deeply honored by your presence<br> as we unite in love and joy.</p>

        <div class="footer">
            <p class="invitation-note">STRICTLY BY INVITATION — No plus-ones due to venue capacity</p>
            <h2><strong>NO RECEPTION</strong></h2>
            <p>Generated on: %s</p>
        </div>

        <button class="download-btn" id="downloadBtn" data-html2canvas-ignore>Download Invitation Card</button>
    </div>

    <script src="https://cdnjs.cloudflare.com/ajax/libs/html2canvas/1.4.1/html2canvas.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const downloadBtn = document.getElementById('downloadBtn');
            const invitationCard = document.getElementById('invitationCard');
            downloadBtn.addEventListener('click', async function() {
                try {
                    downloadBtn.disabled = true;
                    downloadBtn.textContent = 'Generating PDF...';
                    await document.fonts.ready;
                    const canvas = await html2canvas(invitationCard, {
                        scale: 2,
                        useCORS: true,
                        backgroundColor: '#ffffff',
                        logging: false,
                        ignoreElements: el => el.hasAttribute('data-html2canvas-ignore')
                    });
                    const { jsPDF } = window.jspdf;
                    const imgWidth = 210;
                    const imgHeight = (canvas.height * imgWidth) / canvas.width;
                    const pdf = new jsPDF('p', 'mm', 'a4');
                    const imgData = canvas.toDataURL('image/png', 1.0);
                    const pageHeight = 297;
                    const yPosition = (pageHeight - imgHeight) / 2;
                    pdf.addImage(imgData, 'PNG', 0, yPosition > 0 ? yPosition : 0, imgWidth, imgHeight);
                    const filename = `Wedding_Invitation_%s.pdf`;
                    pdf.save(filename);
                } catch (err) {
                    console.error('Error generating PDF:', err);
                    alert('Sorry, there was an error generating the PDF. Please try again.');
                } finally {
                    downloadBtn.disabled = false;
                    downloadBtn.textContent = 'Download Invitation Card';
                }
            });
        });
    </script>
</body>
</html>
""",
                "Oluwaseun & Opeyemi",
                "Oluwaseun & Opeyemi",
                guest.getGuestName(),
                guest.getSeatNumber(),
                formattedDate,
                safeGuestName
        );



//        return String.format("""
//                        <!DOCTYPE html>
//                        <html>
//                        <head>
//                            <meta charset="UTF-8">
//                            <title>Wedding Invitation - %s</title>
//                            <style>
//                                body {
//                                    font-family: 'Cormorant Garamond', serif;
//                                    background: #fdfcfb;
//                                    margin: 0;
//                                    padding: 20px;
//                                    display: flex;
//                                    justify-content: center;
//                                    align-items: center;
//                                    min-height: 100vh;
//                                }
//                                .invitation-card {
//                                    background: #fff;
//                                    padding: 50px 30px;
//                                    border-radius: 20px;
//                                    box-shadow: 0 8px 25px rgba(0,0,0,0.15);
//                                    max-width: 650px;
//                                    width: 100%%;
//                                    text-align: center;
//                                    border: 6px double #d4af37;
//                                    position: relative;
//                                    overflow: hidden;
//                                }
//                                /* Floral borders (corner decorations) */
//                                .invitation-card::before,
//                                .invitation-card::after {
//                                    content: "";
//                                    position: absolute;
//                                    width: 120px;
//                                    height: 120px;
//                                    background: url('/images/floral-corner.png') no-repeat center/contain;
//                                    z-index: 0;
//                                }
//                                .invitation-card::before {
//                                    top: -10px;
//                                    left: -10px;
//                                }
//                                .invitation-card::after {
//                                    bottom: -10px;
//                                    right: -10px;
//                                    transform: rotate(180deg);
//                                }
//                                .header {
//                                    font-family: 'Great Vibes', cursive;
//                                    font-size: 48px;
//                                    color: #d4af37;
//                                    margin-bottom: 15px;
//                                    z-index: 1;
//                                    position: relative;
//                                }
//                                .couple {
//                                    font-size: 30px;
//                                    color: #2c3e50;
//                                    margin-bottom: 20px;
//                                    position: relative;
//                                    z-index: 1;
//                                }
//                                .guest-name {
//                                    font-size: 24px;
//                                    margin: 20px 0;
//                                    font-style: italic;
//                                    color: #34495e;
//                                    z-index: 1;
//                                    position: relative;
//                                }
//                                .wedding-details {
//                                    font-size: 18px;
//                                    line-height: 1.8;
//                                    color: #2c3e50;
//                                    margin: 20px 0;
//                                    z-index: 1;
//                                    position: relative;
//                                }
//                                .seat-info {
//                                    margin: 25px 0;
//                                    padding: 15px;
//                                    background: #faf3e0;
//                                    border: 1px solid #d4af37;
//                                    border-radius: 10px;
//                                    font-size: 20px;
//                                    font-weight: bold;
//                                    color: #8e6e3d;
//                                    z-index: 1;
//                                    position: relative;
//                                }
//                                .footer {
//                                    font-size: 14px;
//                                    margin-top: 30px;
//                                    color: #7f8c8d;
//                                    z-index: 1;
//                                    position: relative;
//                                }
//                                /* Responsive tweaks */
//                                @media (max-width: 600px) {
//                                    .invitation-card {
//                                        padding: 25px 15px;
//                                        font-size: 90%%;
//                                    }
//                                    .header {
//                                        font-size: 32px;
//                                    }
//                                    .couple {
//                                        font-size: 22px;
//                                    }
//                                }
//                                .download-btn {
//                                    margin-top: 20px;
//                                    padding: 12px 20px;
//                                    font-size: 16px;
//                                    font-weight: bold;
//                                    border: none;
//                                    border-radius: 8px;
//                                    background: #d4af37;
//                                    color: white;
//                                    cursor: pointer;
//                                    transition: background 0.3s ease;
//                                    z-index: 1;
//                                    position: relative;
//                                }
//                                .download-btn:hover {
//                                    background: #b4942f;
//                                }
//                            </style>
//                            <!-- Google Fonts -->
//                            <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@400;600&family=Great+Vibes&display=swap" rel="stylesheet">
//                        </head>
//                        <body>
//                            <div class="invitation-card" id="invitationCard">
//                                <div class="header">Wedding Invitation</div>
//                                <div class="couple"><i>Together with our families<i></div>
//                                <div class="couple">%s</div>
//                                <h3>joyfully invite</h3>
//                                <div class="guest-name">%s</div>
//                                <div class="wedding-details">
//                                    <p><strong>Saturday, December 20, 2025</p>
//                                    <p><strong>10:00 AM - 2:00 PM</p>
//                                    <p><strong>Advans Event Special</p>
//                                    <p><strong>44, Ayodele close, off Allen Avenue, Ikeja, Lagos State</p>
//                                </div>
//                                <div class="seat-info">
//                                   Your Reserved Seat: %s
//                                </div>
//                                <p>We would be honored to have you join us as we exchange vows and celebrate our love.</p>
//                                <div class="footer">
//                                <p><strong>Strictly by invitation, no plus one due to hall capacity.<strong></p>
//                                <h2>NO RECEPTION</h2>
//                                    Generated on: %s
//                                </div>
//                                <button class="download-btn" id="downloadBtn" data-html2canvas-ignore>Download Invitation Card</button>
//                            </div>
//                            <script src="https://cdnjs.cloudflare.com/ajax/libs/html2canvas/1.4.1/html2canvas.min.js"></script>
//                            <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js"></script>
//                            <script>
//                                                         document.addEventListener('DOMContentLoaded', function() {
//                                                             const downloadBtn = document.getElementById('downloadBtn');
//                                                             const invitationCard = document.getElementById('invitationCard');
//                                                             downloadBtn.addEventListener('click', async function() {
//                                                                 try {
//                                                                     // Disable button and show loading state
//                                                                     downloadBtn.disabled = true;
//                                                                     downloadBtn.textContent = 'Generating PDF...';
//                                                                     downloadBtn.classList.add('loading');
//                                                                     // Wait for fonts to load
//                                                                     await document.fonts.ready;
//                                                                     // Configure html2canvas options for better quality
//                                                                     const canvas = await html2canvas(invitationCard, {
//                                                                         scale: 2, // Higher resolution
//                                                                         useCORS: true,
//                                                                         allowTaint: true,
//                                                                         backgroundColor: '#ffffff',
//                                                                         logging: false,
//                                                                         width: invitationCard.scrollWidth,
//                                                                         height: invitationCard.scrollHeight,
//                                                                         ignoreElements: function(element) {
//                                                                             return element.hasAttribute('data-html2canvas-ignore');
//                                                                         }
//                                                                     });
//                                                                     // Convert canvas to PDF
//                                                                     const { jsPDF } = window.jspdf;
//                                                                     // Calculate dimensions to fit the card properly
//                                                                     const imgWidth = 210; // A4 width in mm
//                                                                     const imgHeight = (canvas.height * imgWidth) / canvas.width;
//                                                                     const pdf = new jsPDF('p', 'mm', 'a4');
//                                                                     const imgData = canvas.toDataURL('image/png', 1.0);
//                                                                     // Center the image on the page
//                                                                     const pageHeight = 297; // A4 height in mm
//                                                                     const yPosition = (pageHeight - imgHeight) / 2;
//                                                                     pdf.addImage(imgData, 'PNG', 0, yPosition > 0 ? yPosition : 0, imgWidth, imgHeight);
//                                                                     // Generate filename with guest name and timestamp
//                                                                     const filename = `Wedding_Invitation_%s.pdf`;
//                                                                     // Download the PDF
//                                                                     pdf.save(filename);
//                                                                 } catch (error) {
//                                                                     console.error('Error generating PDF:', error);
//                                                                     alert('Sorry, there was an error generating the PDF. Please try again.');
//                                                                 } finally {
//                                                                     // Reset button state
//                                                                     downloadBtn.disabled = false;
//                                                                     downloadBtn.textContent = 'Download Invitation Card';
//                                                                     downloadBtn.classList.remove('loading');
//                                                                 }
//                                                             });
//                                                         });
//                                                     </script>
//                        </body>
//                        </html>
//                        """,
//                "Oluwaseun & Opeyemi",
//                "Oluwaseun & Opeyemi",
//                guest.getGuestName(),
//                guest.getSeatNumber(),
//                formattedDate,
//                safeGuestName
//        );
    }
    private String generateCardHtmlx(Guest guest) {
        String safeGuestName = guest.getGuestName()
                .trim()
                .replaceAll("[^a-zA-Z0-9]", "_");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy hh:mm a");
        String formattedDate = LocalDateTime.now().format(formatter);

        return String.format("""
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <title>Wedding Invitation - %s</title>
            <style>
                body {
                    font-family: 'Cormorant Garamond', serif;
                    background: #fdfcfb;
                    margin: 0;
                    padding: 20px;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    min-height: 100vh;
                }
                .invitation-card {
                    background: #fff;
                    padding: 50px 30px;
                    border-radius: 20px;
                    box-shadow: 0 8px 25px rgba(0,0,0,0.15);
                    max-width: 650px;
                    width: 100%%;
                    text-align: center;
                    border: 6px double #d4af37;
                    position: relative;
                    overflow: hidden;
                }
                /* Floral borders (corner decorations) */
                .invitation-card::before,
                .invitation-card::after {
                    content: "";
                    position: absolute;
                    width: 120px;
                    height: 120px;
                    background: url('/images/floral-corner.png') no-repeat center/contain;
                    z-index: 0;
                }
                .invitation-card::before {
                    top: -10px;
                    left: -10px;
                }
                .invitation-card::after {
                    bottom: -10px;
                    right: -10px;
                    transform: rotate(180deg);
                }
                .header {
                    font-family: 'Great Vibes', cursive;
                    font-size: 48px;
                    color: #d4af37;
                    margin-bottom: 15px;
                    z-index: 1;
                    position: relative;
                }
                .couple {
                    font-size: 30px;
                    color: #2c3e50;
                    margin-bottom: 20px;
                    position: relative;
                    z-index: 1;
                }
                .guest-name {
                    font-size: 24px;
                    margin: 20px 0;
                    font-style: italic;
                    color: #34495e;
                    z-index: 1;
                    position: relative;
                }
                .wedding-details {
                    font-size: 18px;
                    line-height: 1.8;
                    color: #2c3e50;
                    margin: 20px 0;
                    z-index: 1;
                    position: relative;
                }
                .seat-info {
                    margin: 25px 0;
                    padding: 15px;
                    background: #faf3e0;
                    border: 1px solid #d4af37;
                    border-radius: 10px;
                    font-size: 20px;
                    font-weight: bold;
                    color: #8e6e3d;
                    z-index: 1;
                    position: relative;
                }
                .footer {
                    font-size: 14px;
                    margin-top: 30px;
                    color: #7f8c8d;
                    z-index: 1;
                    position: relative;
                }
                /* Responsive tweaks */
                @media (max-width: 600px) {
                    .invitation-card {
                        padding: 25px 15px;
                        font-size: 90%%;
                    }
                    .header {
                        font-size: 32px;
                    }
                    .couple {
                        font-size: 22px;
                    }
                }
                .download-btn {
                    margin-top: 20px;
                    padding: 12px 20px;
                    font-size: 16px;
                    font-weight: bold;
                    border: none;
                    border-radius: 8px;
                    background: #d4af37;
                    color: white;
                    cursor: pointer;
                    transition: background 0.3s ease;
                    z-index: 1;
                    position: relative;
                }
                .download-btn:hover {
                    background: #b4942f;
                }
            </style>
            <!-- Google Fonts -->
            <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@400;600&family=Great+Vibes&display=swap" rel="stylesheet">
        </head>
        <body>
            <div class="invitation-card" id="invitationCard">
                <div class="header">Wedding Invitation</div>
                <div class="couple">%s</div>
                <h3>cordially invite</h3>
                <div class="guest-name">%s</div>
                <div class="wedding-details">
                    <p><strong>Date:</strong> December 20, 2025</p>
                    <p><strong>Time:</strong> 10:00 AM - 2:00 PM</p>
                    <p><strong>Venue:</strong> Advans Event Special</p>
                    <p><strong>Address:</strong> 44, Ayodele close, off Allen Avenue,Ikeja, Lagos State</p>
                </div>
                <div class="seat-info">
                   Your Reserved Seat: %s
                </div>
                <p>We would be honored to have you join us as we exchange vows and celebrate our love.</p>
                <div class="footer">
                <p><strong>Strictly by invitation, no plus one due to hall capacity.<strong></p>
                <h2>NO RECEPTION</h2>
                    Generated on: %s
                </div>
                <button class="download-btn" id="downloadBtn" data-html2canvas-ignore="true">Download Invitation Card</button>
            </div>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/html2canvas/1.4.1/html2canvas.min.js"></script>
            <script>
                document.getElementById("downloadBtn").addEventListener("click", () => {
                    const card = document.getElementById("invitationCard");
                    html2canvas(card).then(canvas => {
                        const link = document.createElement("a");
                        link.href = canvas.toDataURL("image/png");
                        link.download = "wedding_invitation_%s.png";
                        link.click();
                    });
                });
            </script>
        </body>
        </html>
        """,
                "Oluwaseun & Opeyemi",
                "Oluwaseun & Opeyemi",
                guest.getGuestName(),
                guest.getSeatNumber(),
                formattedDate,
                safeGuestName
        );
    }


}
