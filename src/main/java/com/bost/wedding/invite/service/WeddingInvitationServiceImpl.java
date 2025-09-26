package com.bost.wedding.invite.service;

import com.bost.wedding.invite.entity.Guest;
import com.bost.wedding.invite.model.GuestDto;
import com.bost.wedding.invite.repository.GuestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeddingInvitationServiceImpl implements WeddingInvitationService{
    private final GuestRepository guestRepository;
    private final SeatAssignmentService seatAssignmentService;
    private final InvitationCardService invitationCardService;
    @Value("${wedding.invitation.base-url:http://localhost:1221}")
    private String baseUrl;
    @Override
    public Map<Integer, String> generateInvitationLinks(int numberOfGuests) {
        List<String> invitationLinks = new ArrayList<>();
        Map<Integer, String> invitationLinkMap = new HashMap<>();
        for (int i = 0; i < numberOfGuests; i++) {
            String token = generateUniqueToken();
            Guest guest = new Guest();
            guest.setInviteToken(token);
            guest.setStatus(Guest.InviteStatus.PENDING);

            guestRepository.save(guest);

            String inviteLink = baseUrl + "/api/v1/wedding/invite/" + token;
//            invitationLinks.add(inviteLink);
            invitationLinkMap.put(i, inviteLink);

            log.info("Generated invitation link: {}", inviteLink);
        }

        return invitationLinkMap;
    }

    @Override
    public Guest trackLinkClick(String inviteToken) {
        Guest guest = guestRepository.findByInviteToken(inviteToken)
                .orElseThrow(() -> new RuntimeException("Invalid invitation token"));

        guest.setLinkClickedAt(LocalDateTime.now());
        guest.setClickCount(guest.getClickCount() + 1);

        if (guest.getStatus() == Guest.InviteStatus.PENDING) {
            guest.setStatus(Guest.InviteStatus.LINK_CLICKED);
        }

        guestRepository.save(guest);
        log.info("Link clicked for token: {}, click count: {}", inviteToken, guest.getClickCount());

        return guest;
    }

    @Override
    @Transactional
    public GuestDto.Response processRSVP(String inviteToken, GuestDto.Request request) {
        Guest guest = guestRepository.findByInviteToken(inviteToken)
                .orElseThrow(() -> new RuntimeException("Invalid invitation token"));

        if(guest.getStatus().equals(Guest.InviteStatus.ACCEPTED) || guest.getStatus().equals(Guest.InviteStatus.DECLINED)){
            throw new RuntimeException("Invite link already used by YOU");
        }
        guest.setGuestName(request.getGuestName());
        guest.setEmail(request.getEmail());
        guest.setPhoneNumber(request.getPhoneNumber());
        guest.setDietaryRestrictions(request.getDietaryRestrictions());
        guest.setRespondedAt(LocalDateTime.now());
        guest.setSquad(request.getSquad());

        if ("accept".equalsIgnoreCase(request.getResponse())) {
            guest.setStatus(Guest.InviteStatus.ACCEPTED);

            // Assign seat number
            String seatNumber = seatAssignmentService.assignSeat(guest);
            guest.setSeatNumber(seatNumber);

            guestRepository.save(guest);

            // Generate invitation card
            String cardUrl = invitationCardService.generateInvitationCard(guest);

            log.info("RSVP accepted for guest: {}, seat: {}", request.getGuestName(), seatNumber);

            return GuestDto.Response.builder()
                    .message("Thank you for accepting our invitation!")
                    .seatNumber(seatNumber)
                    .invitationCardUrl(cardUrl)
                    .status("ACCEPTED")
                    .guestName(request.getGuestName())
                    .squad(guest.getSquad())
                    .build();

        } else {
            guest.setStatus(Guest.InviteStatus.DECLINED);
            guestRepository.save(guest);

            log.info("RSVP declined for guest: {}", request.getGuestName());

            return GuestDto.Response.builder()
                    .message("We're sorry you can't make it. Thank you for responding!")
                    .status("DECLINED")
                    .guestName(request.getGuestName())
                    .build();
        }
    }

    @Override
    public GuestDto.InvitationReport getInvitationReport() {
        long total = guestRepository.count();
        Long accepted = guestRepository.countByStatus(Guest.InviteStatus.ACCEPTED);
        Long declined = guestRepository.countByStatus(Guest.InviteStatus.DECLINED);
        Long pending = guestRepository.countByStatus(Guest.InviteStatus.PENDING);
        Long clicked = guestRepository.countByStatus(Guest.InviteStatus.LINK_CLICKED);

        Double responseRate = total > 0 ? ((accepted + declined) * 100.0) / total : 0.0;

        return GuestDto.InvitationReport.builder()
                .totalInvites(total)
                .acceptedCount(accepted)
                .declinedCount(declined)
                .pendingCount(pending)
                .clickedCount(clicked)
                .responseRate(responseRate)
                .generatedAt(LocalDateTime.now())
                .build();
    }

    @Override
    public List<GuestDto.Response> getAllGuests() {
        List<Guest> guests = guestRepository.findAll();
        return guests.stream().map(
                guest -> GuestDto.Response.builder()
                        .squad(guest.getSquad())
                        .guestName(guest.getGuestName())
                        .status(guest.getStatus().name())
                        .seatNumber(guest.getSeatNumber())
                        .message(guest.getDietaryRestrictions())
                        .build()
        ).toList();

    }
    private String generateUniqueToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
