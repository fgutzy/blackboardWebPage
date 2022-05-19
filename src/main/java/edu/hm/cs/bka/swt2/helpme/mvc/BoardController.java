package edu.hm.cs.bka.swt2.helpme.mvc;

import edu.hm.cs.bka.swt2.helpme.service.AdService;
import edu.hm.cs.bka.swt2.helpme.service.BoardService;
import edu.hm.cs.bka.swt2.helpme.service.dto.BoardCreateDto;
import edu.hm.cs.bka.swt2.helpme.service.dto.BoardDto;
import lombok.extern.slf4j.Slf4j;
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
 * Controller-Klasse für alle Interaktionen, die die Anzeige und Verwaltung von Pinnwänden betrifft.
 * <br>
 * Controller reagieren auf Aufrufe von URLs. Sie benennen ein View-Template (Thymeleaf-Vorlage) und
 * stellen Daten zusammen, die darin dargestellt werden. Dafür verwenden Sie Methoden der
 * Service-Schicht.
 *
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Controller
@Slf4j
public class BoardController extends AbstractController {

    @Autowired
    private BoardService boardService;

    @Autowired
    private AdService adService;

    /**
     * Erstellt die Übersicht über alle Pinnwände des Anwenders oder der Anwenderin, d.h. verwaltete und beobachtete.
     */
    @GetMapping("/boards")
    public String getBoardListView(Model model, Authentication auth) {
        model.addAttribute("managedBoards", boardService.getManagedBoards(auth.getName()));
        model.addAttribute("boards", boardService.getSubscriptions(auth.getName()));
        return "board-listview";
    }

    /**
     * Erstellt das Formular zum Erstellen einer Pinnwand.
     */
    @GetMapping("/boards/create")
    public String getBoardCreationView(Model model, Authentication auth) {
        model.addAttribute("newBoard", new BoardCreateDto(null, ""));
        return "board-creation";
    }

    @GetMapping("/boards/{uuid}/edit")
    public String getBoardEditView(Model model, Authentication auth, @PathVariable("uuid") String uuid) {
        model.addAttribute("board", boardService.getBoard(uuid, auth.getName()));
        return "board-edit";
    }

    /**
     * Nimmt den Formularinhalt vom Formular zum Erstellen einer Pinnwand entgegen und legt eine
     * entsprechende Pinnwand an. Kommt es dabei zu einer Exception, wird das Erzeugungsformular wieder
     * angezeigt und eine Fehlermeldung eingeblendet. Andernfalls wird auf die Übersicht der Pinwände
     * weitergeleitet und das Anlegen in einer Einblendung bestätigt.
     */
    @PostMapping("/boards")
    public String handleBoardCreation(Model model, Authentication auth,
                                      @ModelAttribute("newBoard") BoardCreateDto board, RedirectAttributes redirectAttributes) {
        try {
            boardService.createBoard(board, board.getDescription(),auth.getName());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            log.info("Fehler beim Erzeugen einer Pinnwand.", e);
            return "redirect:/boards/create";
        }
        redirectAttributes.addFlashAttribute("success", "Eine Pinnwand mit dem Titel " + board.getTitle() + " wurde angelegt.");
        return "redirect:/boards";
    }

    @PostMapping("/boards/{uuid}")
    public String handleBoardUpdate(Model model, Authentication auth, @PathVariable("uuid") String uuid,
                                    @ModelAttribute("board") BoardCreateDto board, RedirectAttributes redirectAttributes) {

        try {
            boardService.updateBoard(uuid, board, board.getDescription(),auth.getName());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            log.info("Fehler beim Ändern einer Pinnwand.", e);
            return "redirect:/boards/" + uuid + "/edit";
        }
        redirectAttributes.addFlashAttribute("success", "Die Pinnwand mit dem Titel " + board.getTitle() + " wurde gespeichert.");
        return "redirect:/boards/" + uuid;
    }

    /**
     * Erstellt Übersicht eines Boards.
     */
    @GetMapping("/boards/{uuid}")
    public String createBoardView(Model model, Authentication auth,
                                  @PathVariable("uuid") String uuid) {
        BoardDto board = boardService.getBoard(uuid, auth.getName());
        model.addAttribute("board", board);
        model.addAttribute("ads", adService.getAdsForBoard(uuid, auth.getName()));
        return "board-view";
    }

    /**
     * Verarbeitung einer Beobachtung.
     */
    @GetMapping("/boards/{uuid}/subscribe")
    public String createBoardSubscription(Model model, Authentication auth,
                                          @PathVariable("uuid") String uuid) {
        boardService.subscribe(uuid, auth.getName());
        return "redirect:/boards/" + uuid;
    }

    /** Verarbeitung der Beendigung einer Beobachtung */
    @GetMapping("/boards/{uuid}/unsubscribe")
    public String unsubscribe(Model model, Authentication auth,
                              @PathVariable("uuid") String uuid) {
        boardService.unsubscribe(uuid, auth.getName());
        return "redirect:/boards/" + uuid;
    }

    /** Zeigt dem Ersteller an, wer die Boards beobachtet */
    @GetMapping("/boards/{uuid}/subscriptions")
    public String getSubsciptions(Model model, Authentication auth,
                           @PathVariable("uuid") String uuid){
        model.addAttribute("board", boardService.getBoard(uuid, auth.getName()));
        model.addAttribute("subscriptions", boardService.getSubscriptionsForBoard(uuid, auth.getName()));
        return "subscription-listview";
    }

        @GetMapping("/boards/{uuid}/revokeWriteAccess/{observer}")
    public String revokeWriteAccess(Model model, Authentication auth,
                                    @PathVariable("uuid") String uuid,
                                    @PathVariable("observer") String observer){
        boardService.setWriteAccess(uuid, observer, false, auth.getName());
        return "redirect:/boards/{uuid}/subscriptions";
    }




    @GetMapping("/boards/{uuid}/grantWriteAccess/{observer}")
    public String grantWriteAccess(Model model, Authentication auth,
                                    @PathVariable("uuid") String uuid,
                                    @PathVariable("observer") String observer){
        boardService.setWriteAccess(uuid, observer, true, auth.getName());
        return "redirect:/boards/{uuid}/subscriptions";
    }




    @GetMapping("/boards/{uuid}/delete")
    public String delete(Model model, Authentication auth,@PathVariable("uuid") String uuid) {
        boardService.deleteBoard(uuid, auth.getName());
        return "redirect:/boards";
    }

}