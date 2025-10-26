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
        String prefix = guest.getSquad() == Guest.Squad.BRIDE ? "B" : "G";

        int seatsPerTable = 10;
        int maxSeats = prefix.equals("B") ? 70 : 50; // Bride: 70 seats, Groom: 50 seats

        for (int attempt = 0; attempt < maxSeats; attempt++) {
            int tableNumber = (attempt / seatsPerTable) + 1;
            int seatAtTable = (attempt % seatsPerTable) + 1;

            String seatNumber = String.format("%s%d-S%d", prefix, tableNumber, seatAtTable);

            if (!guestRepository.existsBySeatNumber(seatNumber)) {
                return seatNumber;
            }
        }

        throw new RuntimeException("All seats for the " + guest.getSquad() + " squad are full.");
    }


}
