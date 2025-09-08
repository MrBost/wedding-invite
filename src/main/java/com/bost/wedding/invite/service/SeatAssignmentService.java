package com.bost.wedding.invite.service;

import com.bost.wedding.invite.entity.Guest;
import com.bost.wedding.invite.repository.GuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeatAssignmentService {
    private final GuestRepository guestRepository;

    public String assignSeat(Guest guest) {
        String prefix = guest.getSquad().equals(Guest.Squad.BRIDE) ? "B" : "G";
        int maxSeatsPerSquad = 50; //5 tables × 10 seats
        int seatsPerTable = 10;

        for (int attempt = 0; attempt < maxSeatsPerSquad; attempt++) {
            int tableNumber = (attempt / seatsPerTable) + 1;
            int seatAtTable = (attempt % seatsPerTable) + 1;

            String seatNumber = String.format("%s%d-S%d", prefix, tableNumber, seatAtTable);

            if (!guestRepository.existsBySeatNumber(seatNumber)) {
                return seatNumber;
            }
        }

        throw new RuntimeException("Unable to assign seat - all seats for squad " + guest.getSquad() + " are full");
    }

}
