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
        String seatNumber;
        int attempts = 0;

        do {
            int tableNumber = (attempts / 10) + 1; // 8 seats per table
            int seatAtTable = (attempts % 10) + 1;
            seatNumber = String.format("T%d-S%d", tableNumber, seatAtTable);
            attempts++;
        } while (guestRepository.existsBySeatNumber(seatNumber) && attempts < 1000);

        if (attempts >= 1000) {
            throw new RuntimeException("Unable to assign seat - venue full");
        }

        return seatNumber;
    }
}
