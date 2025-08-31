package com.bost.wedding.invite.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "guests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(unique = true, nullable = false)
    private String inviteToken;

    private String guestName;
    private String email;
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private InviteStatus status = InviteStatus.PENDING;

    private String seatNumber;
    private String dietaryRestrictions;
    private String plusOneDetails;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "link_clicked_at")
    private LocalDateTime linkClickedAt;

    private Integer clickCount = 0;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    public enum InviteStatus {
        PENDING,
        ACCEPTED,
        DECLINED,
        LINK_CLICKED
    }
}
