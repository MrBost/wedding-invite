package com.bost.wedding.invite.controller;

import com.bost.wedding.invite.entity.Guest;
import com.bost.wedding.invite.model.GuestDto;
import com.bost.wedding.invite.service.WeddingInvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/invite")
@RequiredArgsConstructor
@Validated
@Slf4j
@CrossOrigin(origins = "*")
public class WeddingInvitationController {
    private final WeddingInvitationService invitationService;

    @PostMapping("/generate-invites")
    public ResponseEntity<Map<Integer, String>> generateInvitations(@RequestParam(defaultValue = "5") int numberOfGuests) {

        log.info("Generating {} wedding invitation links", numberOfGuests);
        Map<Integer, String> inviteLinks = invitationService.generateInvitationLinks(numberOfGuests);

        return ResponseEntity.ok(inviteLinks);
    }
//    @GetMapping("/invite/{token}")
//    public String handleInviteClick(@PathVariable String token, Model model) {
//        try {
//            Guest guest = invitationService.trackLinkClick(token);
//
//            model.addAttribute("guest", guest);
//            model.addAttribute("token", token);
//
//            return "rsvp";
//        } catch (RuntimeException e) {
//            model.addAttribute("error", "Invalid Invitation Link");
//            return "error";
//        }
//    }
    @GetMapping("/invitex/{token}")
    public ResponseEntity<String> handleInviteClickx(@PathVariable String token) {
        try {
            Guest guest = invitationService.trackLinkClick(token);

            // Return HTML form for RSVP
            String rsvpForm = generateRSVPForm(token, guest);

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(rsvpForm);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.TEXT_HTML)
                    .body("<h1>Invalid Invitation Link</h1><p>This invitation link is not valid.</p>");
        }
    }

    @PostMapping("/rsvp/{token}")
    public ResponseEntity<GuestDto.Response> processRSVP(@PathVariable String token, @Valid @RequestBody GuestDto.Request request) {

        try {
            GuestDto.Response response = invitationService.processRSVP(token, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error processing RSVP for token: {}", token, e);
            return ResponseEntity.badRequest()
                    .body(GuestDto.Response.builder()
                            .message("Error processing RSVP: " + e.getMessage())
                            .status("ERROR")
                            .build());
        }
    }
    @GetMapping("/squads")
    public List<String> getSquads() {
        return Arrays.stream(Guest.Squad.values())
                .map(Enum::name)
                .toList();
    }
    @GetMapping("/report")
    public ResponseEntity<GuestDto.InvitationReport> getInvitationReport() {
        GuestDto.InvitationReport report = invitationService.getInvitationReport();
        return ResponseEntity.ok(report);
    }

    @GetMapping("/guests")
    public ResponseEntity<List<GuestDto.Response>> getAllGuests() {
        List<GuestDto.Response> guests = invitationService.getAllGuests();
        return ResponseEntity.ok(guests);
    }
    @GetMapping(value = "/guests/export", produces = "text/csv")
    public ResponseEntity<byte[]> exportGuestsToCsv() {
        List<GuestDto.Response> guests = invitationService.getAllGuests();

        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("Guest Name,Seat Number,Status,Squad,Dietary Restrictions\n");

        for (GuestDto.Response guest : guests) {
            csvBuilder.append(safeCsv(guest.getGuestName())).append(",");
            csvBuilder.append(safeCsv(guest.getSeatNumber())).append(",");
            csvBuilder.append(safeCsv(guest.getStatus())).append(",");
            csvBuilder.append(safeCsv(guest.getSquad() != null ? guest.getSquad().name() : "")).append(",");
            csvBuilder.append(safeCsv(guest.getMessage())).append("\n");
        }

        byte[] csvBytes = csvBuilder.toString().getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=guests.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvBytes);
    }

    private String safeCsv(String value) {
        if (value == null) return "";
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }

    @GetMapping("/cards/{fileName}")
    public ResponseEntity<Resource> downloadInvitationCard(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get("generated-cards").resolve(fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private String generateRSVPForm(String token, Guest guest) {
        return String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Wedding RSVP</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                            margin: 0;
                            padding: 20px;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            min-height: 100vh;
                        }
                        .rsvp-form {
                            background: white;
                            padding: 30px;
                            border-radius: 10px;
                            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
                            max-width: 400px;
                            width: 100%%;
                        }
                        .form-group {
                            margin-bottom: 15px;
                        }
                        label {
                            display: block;
                            margin-bottom: 5px;
                            font-weight: bold;
                            color: #333;
                        }
                        input, textarea, select {
                            width: 100%%;
                            padding: 10px;
                            border: 1px solid #ddd;
                            border-radius: 5px;
                            font-size: 14px;
                            box-sizing: border-box;
                        }
                        .response-buttons {
                            display: flex;
                            gap: 10px;
                            margin: 20px 0;
                        }
                        .btn {
                            flex: 1;
                            padding: 12px;
                            border: none;
                            border-radius: 5px;
                            cursor: pointer;
                            font-size: 16px;
                            font-weight: bold;
                        }
                        .btn-accept {
                            background: #27ae60;
                            color: white;
                        }
                        .btn-decline {
                            background: #e74c3c;
                            color: white;
                        }
                        .btn:hover {
                            opacity: 0.9;
                        }
                        h2 {
                            text-align: center;
                            color: #2c3e50;
                            margin-bottom: 20px;
                        }
                        .clicks-info {
                            background: #ecf0f1;
                            padding: 10px;
                            border-radius: 5px;
                            margin-bottom: 15px;
                            font-size: 12px;
                            color: #7f8c8d;
                        }
                    </style>
                </head>
                <body>
                    <div class="rsvp-form">
                        <h2>Wedding RSVP</h2>
                        <div class="clicks-info">
                            Link accessed %d times
                        </div>
                        <form id="rsvpForm">
                            <div class="form-group">
                                <label for="guestName">Full Name *</label>
                                <input type="text" id="guestName" name="guestName" required>
                            </div>
                            
                            <div class="form-group">
                                <label for="email">Email Address *</label>
                                <input type="email" id="email" name="email" required>
                            </div>
                            
                            <div class="form-group">
                                <label for="phoneNumber">Phone Number</label>
                                <input type="tel" id="phoneNumber" name="phoneNumber">
                            </div>
                            
                            <div class="form-group">
                                <label for="dietaryRestrictions">Dietary Restrictions</label>
                                <textarea id="dietaryRestrictions" name="dietaryRestrictions" rows="3" placeholder="Any allergies or dietary preferences..."></textarea>
                            </div>
                            
                           <div class="form-group">
                                      <label for="squad">Select Squad</label>
                                      <select id="squad" name="squad" class="form-select" required>
                                          <option value="">-- Select Squad --</option>
                                      </select>
                                  </div>
                            
                            <div class="response-buttons">
                                <button type="button" class="btn btn-accept" onclick="submitResponse('accept')">
                                    ✓ Accept Invitation
                                </button>
                                <button type="button" class="btn btn-decline" onclick="submitResponse('decline')">
                                    ✗ Decline Invitation
                                </button>
                            </div>
                        </form>
                    </div>

                    <script>
                        document.addEventListener("DOMContentLoaded", async () => {
                            try {
                                const res = await fetch("/api/v1/wedding/squads");
                                const squads = await res.json();
                    
                                const squadLabels = {
                                    "BRIDE": "Bride's Squad",
                                    "GROOM": "Groom's Squad"
                                };
                    
                                const squadSelect = document.getElementById("squad");
                                squads.forEach(s => {
                                    const option = document.createElement("option");
                                    option.value = s;
                                    option.text = squadLabels[s] || s;
                                    squadSelect.appendChild(option);
                                });
                            } catch (error) {
                                console.error("Failed to load squads:", error);
                            }
                        });
                        async function submitResponse(response) {
                            const form = document.getElementById('rsvpForm');
                            const formData = new FormData(form);
                            
                            const data = {
                                guestName: formData.get('guestName'),
                                email: formData.get('email'),
                                phoneNumber: formData.get('phoneNumber'),
                                dietaryRestrictions: formData.get('dietaryRestrictions'),
                                squad: formData.get('squad'),
                                response: response
                            };

                            try {
                                const result = await fetch('/api/v1/wedding/rsvp/%s', {
                                    method: 'POST',
                                    headers: {
                                        'Content-Type': 'application/json',
                                    },
                                    body: JSON.stringify(data)
                                });

                                const responseData = await result.json();
                                
                                if (result.ok) {
                                    if (response === 'accept') {
                                        document.body.innerHTML = `
                                            <div class="rsvp-form">
                                                <h2>Thank You!</h2>
                                                <p>${responseData.message}</p>
                                                <p><strong>Your Seat:</strong> ${responseData.seatNumber}</p>
                                                <a href="${responseData.invitationCardUrl}" target="_blank" class="btn btn-accept">
                                                    Download Your Invitation Card
                                                </a>
                                            </div>
                                        `;
                                    } else {
                                        document.body.innerHTML = `
                                            <div class="rsvp-form">
                                                <h2>Response Recorded</h2>
                                                <p>${responseData.message}</p>
                                            </div>
                                        `;
                                    }
                                } else {
                                    alert('Error: ' + responseData.message);
                                }
                            } catch (error) {
                                alert('Error submitting RSVP: ' + error.message);
                            }
                        }
                        
                    </script>
                </body>
                </html>
                """, guest.getClickCount(), token);
    }
}
