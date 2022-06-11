package edu.hm.cs.bka.swt2.helpme.mvc;

import edu.hm.cs.bka.swt2.helpme.persistence.Reaction;
import edu.hm.cs.bka.swt2.helpme.service.AdService;
import edu.hm.cs.bka.swt2.helpme.service.BoardService;
import edu.hm.cs.bka.swt2.helpme.service.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Controller-Klasse für alle Interaktionen, die die Anzeige und Verwaltung von Gesuchen betrifft.
 * <br>
 * Controller reagieren auf Aufrufe von URLs. Sie benennen ein View-Template (Thymeleaf-Vorlage) und
 * stellen Daten zusammen, die darin dargestellt werden. Dafür verwenden Sie Methoden der
 * Service-Schicht.
 *
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Controller
@Slf4j
public class AdController extends AbstractController {

    public static final String REDIRECT_BOARDS = "redirect:/boards/";
    @Autowired
    private BoardService boardService;

    @Autowired
    private AdService adService;

    /**
     * Ertellt das Formular zur Erfassung eines neuen Tasks.
     */
    @GetMapping("/boards/{uuid}/createad")
    public String getTaskCreationView(Model model, Authentication auth,
                                      @PathVariable("uuid") String uuid) {
        BoardDto board = boardService.getBoard(uuid, auth.getName());
        model.addAttribute("board", board);
        model.addAttribute("newAd", new AdCreateDto("", ""));
        return "ad-creation";
    }

    /**
     * Verarbeitet die Erstellung eines neuen Gesuchs.
     */
    @PostMapping("/boards/{uuid}/createad")
    public String handleAdCreation(Model model, Authentication auth,
                                   @PathVariable("uuid") String uuid,
                                   @ModelAttribute("newAd") AdCreateDto newAd,
                                   RedirectAttributes redirectAttributes) {
        try {
            adService.createAd(uuid, newAd, newAd.getDescription(), auth.getName());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            log.debug("Anlegen fehlgeschlagen: {}.", newAd, e);
            return REDIRECT_BOARDS + uuid + "/createad";
        }
        redirectAttributes.addFlashAttribute("success",
            "Gesuch \"" + newAd.getTitle() + "\" erstellt.");
        return REDIRECT_BOARDS + uuid;
    }

    /**
     * Verarbeitet das Löschen eines Gesuchs.
     */
    @GetMapping("/ads/{id}/delete")
    public String handleAdDeletion(Authentication auth, @PathVariable("id") Long adId) {
        AdDto ad = adService.getAd(adId, auth.getName());
        adService.deleteAd(adId, auth.getName());
        return REDIRECT_BOARDS + ad.getBoard().getUuid();
    }

    /* Verarbeitet das Ausblenden eines Gesuchs. */
    @GetMapping("/ads/{id}/hide")
    public String handleAdHide(Authentication auth, @PathVariable("id") Long adId) {
        AdDto ad = adService.getAd(adId, auth.getName());
        adService.hideAd(adId, auth.getName());
        return REDIRECT_BOARDS + ad.getBoard().getUuid();
    }

    /* Verarbeitet das Wiedereinblenden eines Gesuchs. */
    @GetMapping("/ads/{id}/showAgain")
    public String handleAdShowAgain(Authentication auth, @PathVariable("id") Long adId) {
        AdDto ad = adService.getAd(adId, auth.getName());
        adService.showAdAgain(adId, auth.getName());
        return REDIRECT_BOARDS + ad.getBoard().getUuid();
    }

    /* Verarbeitet das Zusagen auf einem Gesuchs. */
    @GetMapping("/ads/{id}/countAccept")
    public String countAccept(Authentication auth, @PathVariable("id") Long adId) {
        AdDto ad = adService.getAd(adId, auth.getName());
        adService.countAccept(adId, auth.getName());
        return REDIRECT_BOARDS + ad.getBoard().getUuid();
    }


    /* Verarbeitet das Absagen auf einem Gesuchs. */
    @GetMapping("/ads/{id}/countReject")
    public String countReject(Authentication auth, @PathVariable("id") Long adId) {
        AdDto ad = adService.getAd(adId, auth.getName());
        adService.countReject(adId, auth.getName());
        return REDIRECT_BOARDS + ad.getBoard().getUuid();
    }





    /* Verarbeitet das Erfassen oder Ändern einer Reaktion. */
    @PostMapping("/ads/{id}/react")
    public String handleReaction(Authentication auth,
                                 @PathVariable("id") Long adId,
                                 @ModelAttribute("reaction") ReactionCreateDto dto) {
        adService.setReaction(adId, auth.getName(), dto);
        AdDto ad = adService.getAd(adId, auth.getName());
        return REDIRECT_BOARDS + ad.getBoard().getUuid();
    }

}