package com.bost.wedding.invite.service;

import com.bost.wedding.invite.entity.Guest;
import com.bost.wedding.invite.model.GuestDto;

import java.util.List;
import java.util.Map;

public interface WeddingInvitationService {
    Map<Integer, String> generateInvitationLinks(int numberOfGuests);
    Guest trackLinkClick(String inviteToken);
    GuestDto.Response processRSVP(String inviteToken, GuestDto.Request request);
    GuestDto.InvitationReport getInvitationReport();
    List<GuestDto.Response> getAllGuests();
}
