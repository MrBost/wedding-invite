package com.bost.wedding.invite.service;

import com.bost.wedding.invite.entity.Guest;
import com.bost.wedding.invite.model.GuestDto;

import java.util.List;

public interface WeddingInvitationService {
    List<String> generateInvitationLinks(int numberOfGuests);
    Guest trackLinkClick(String inviteToken);
    GuestDto.Response processRSVP(String inviteToken, GuestDto.Request request);
    GuestDto.InvitationReport getInvitationReport();
    List<Guest> getAllGuests();
}
