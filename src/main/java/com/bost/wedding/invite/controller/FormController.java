package com.bost.wedding.invite.controller;

import com.bost.wedding.invite.entity.Guest;
import com.bost.wedding.invite.service.InvitationCardService;
import com.bost.wedding.invite.service.WeddingInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/wedding")
@RequiredArgsConstructor
public class FormController {
    private final WeddingInvitationService invitationService;
    @GetMapping("/invite/{token}")
    public String handleInviteClick(@PathVariable String token, Model model) {
        try {
            Guest guest = invitationService.trackLinkClick(token);

            model.addAttribute("guest", guest);
            model.addAttribute("token", token);

            return "rsvp";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Invalid Invitation Link");
            return "error";
        }
    }

}
