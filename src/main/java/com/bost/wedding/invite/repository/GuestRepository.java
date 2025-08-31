package com.bost.wedding.invite.repository;

import com.bost.wedding.invite.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GuestRepository extends JpaRepository<Guest, String> {
    Optional<Guest> findByInviteToken(String inviteToken);

    List<Guest> findByStatus(Guest.InviteStatus status);

    @Query("SELECT COUNT(g) FROM Guest g WHERE g.status = :status")
    Long countByStatus(Guest.InviteStatus status);

    @Query("SELECT g FROM Guest g WHERE g.seatNumber IS NOT NULL ORDER BY g.seatNumber")
    List<Guest> findAllWithSeats();

    boolean existsBySeatNumber(String seatNumber);
}
