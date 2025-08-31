package com.bost.wedding.invite.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

public class GuestDto {
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class Request{
        @NotBlank(message = "Name is required")
        private String guestName;

        @Email(message = "Valid email is required")
        private String email;

        private String phoneNumber;
        private String dietaryRestrictions;
        private String plusOneDetails;

        @NotBlank(message = "RSVP response is required")
        private String response;
    }
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class Response{
        private String message;
        private String seatNumber;
        private String invitationCardUrl;
        private String status;
        private String guestName;
    }
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class InvitationReport{
        private Long totalInvites;
        private Long acceptedCount;
        private Long declinedCount;
        private Long pendingCount;
        private Long clickedCount;
        private Double responseRate;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime generatedAt;
    }
}
