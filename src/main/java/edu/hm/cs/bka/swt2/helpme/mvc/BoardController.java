package edu.hm.cs.bka.swt2.helpme.mvc;

import edu.hm.cs.bka.swt2.helpme.service.AdService;
import edu.hm.cs.bka.swt2.helpme.service.BoardService;
import edu.hm.cs.bka.swt2.helpme.service.dto.AdDto;
import edu.hm.cs.bka.swt2.helpme.service.dto.BoardCreateDto;
import edu.hm.cs.bka.swt2.helpme.service.dto.BoardDto;
import edu.hm.cs.bka.swt2.helpme.service.dto.SearchDto;
import edu.hm.cs.bka.swt2.helpme.service.CategoryService;
import edu.hm.cs.bka.swt2.helpme.service.dto.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

  public static final String REDIRECT_BOARDS = "redirect:/boards/";

  @Autowired
  private BoardService boardService;

  @Autowired
  private AdService adService;

  @Autowired
  private CategoryService categoryService;


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
  public String getBoardEditView(Model model, Authentication auth,
                                 @PathVariable("uuid") String uuid) {
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
                                    @ModelAttribute("newBoard") BoardCreateDto board,
                                    RedirectAttributes redirectAttributes) {
    try {
      boardService.createBoard(board, auth.getName());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      log.info("Fehler beim Erzeugen einer Pinnwand.", e);
      return "redirect:/boards/create";
    }
    redirectAttributes.addFlashAttribute("success",
        "Eine Pinnwand mit dem Titel " + board.getTitle() + " wurde angelegt.");
    return "redirect:/boards";
  }

  @PostMapping("/boards/{uuid}")
  public String handleBoardUpdate(Model model, Authentication auth,
                                  @PathVariable("uuid") String uuid,
                                  @ModelAttribute("board") BoardCreateDto board,
                                  RedirectAttributes redirectAttributes) {

    try {
      boardService.updateBoard(uuid, board, board.getDescription(), auth.getName());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      log.info("Fehler beim Ändern einer Pinnwand.", e);
      return REDIRECT_BOARDS + uuid + "/edit";
    }
    redirectAttributes.addFlashAttribute("success",
        "Die Pinnwand mit dem Titel " + board.getTitle() + " wurde gespeichert.");
    return REDIRECT_BOARDS + uuid;
  }

  /**
   * Erstellt Übersicht eines Boards.
   */
  @GetMapping("/boards/{uuid}")
  public String createBoardView(Model model, Authentication auth,
                                @PathVariable("uuid") String uuid) {
    BoardDto board = boardService.getBoard(uuid, auth.getName());
    List<AdDto> result;
    List<AdDto> adsToDelete = new ArrayList<>();
    result = adService
        .getAdsForBoard(uuid, auth.getName()); //Erstellt eine Liste mit allen Ads eines Boards
    model.addAttribute("board", board);
    for (AdDto adDto : result) {        //geht durch result und frägt ab ob, die Ad 7 oder mehr tage alt ist
      LocalDateTime comparer = LocalDateTime.now();
      long differenceBetweenDates =
          ChronoUnit.DAYS.between(adDto.getDateAdCreated(), comparer);
      if (differenceBetweenDates >= 7) {
        adsToDelete.add(adDto); //wenn ja wird sie der Liste an zu löschenden Ads übergeben
        log.info("Ad {} wird der Liste adsToDelete übergeben", adDto.getTitle());
      }
    }
    result.removeAll(adsToDelete); //alle zu löschenden Ads werden aus result entfernt
    log.info("Alle Ads von der Liste adsToDelete werden vom Board entfernt");
    model.addAttribute("ads", result);
    model.addAttribute("newFilter", new FilterDto());
    model.addAttribute("categoryList", categoryService.getAllCategories());

    return "board-view";
  }


  /**
   * Verarbeitung einer Beobachtung.
   */
  @GetMapping("/boards/{uuid}/subscribe")
  public String createBoardSubscription(Model model, Authentication auth,
                                        @PathVariable("uuid") String uuid) {
    boardService.subscribe(uuid, auth.getName());
    return REDIRECT_BOARDS + uuid;
  }

  /**
   * Verarbeitung der Beendigung einer Beobachtung
   */
  @GetMapping("/boards/{uuid}/unsubscribe")
  public String unsubscribe(Model model, Authentication auth,
                            @PathVariable("uuid") String uuid) {
    boardService.unsubscribe(uuid, auth.getName());
    return REDIRECT_BOARDS + uuid;
  }

  /**
   * Zeigt dem Ersteller an, wer die Boards beobachtet
   */
  @GetMapping("/boards/{uuid}/subscriptions")
  public String getSubsciptions(Model model, Authentication auth,
                                @PathVariable("uuid") String uuid) {
    model.addAttribute("board", boardService.getBoard(uuid, auth.getName()));
    model
        .addAttribute("subscriptions", boardService.getSubscriptionsForBoard(uuid, auth.getName()));
    return "subscription-listview";
  }

  @GetMapping("/boards/{uuid}/revokeWriteAccess/{observer}")
  public String revokeWriteAccess(Model model, Authentication auth,
                                  @PathVariable("uuid") String uuid,
                                  @PathVariable("observer") String observer) {
    boardService.setWriteAccess(uuid, observer, false, auth.getName());
    return "redirect:/boards/{uuid}/subscriptions";
  }


  @GetMapping("/boards/{uuid}/grantWriteAccess/{observer}")
  public String grantWriteAccess(Model model, Authentication auth,
                                 @PathVariable("uuid") String uuid,
                                 @PathVariable("observer") String observer) {
    boardService.setWriteAccess(uuid, observer, true, auth.getName());
    return "redirect:/boards/{uuid}/subscriptions";
  }



  @GetMapping("/boards/{uuid}/delete")
  public String delete(Model model, Authentication auth, @PathVariable("uuid") String uuid) {
    boardService.deleteBoard(uuid, auth.getName());
    return "redirect:/boards";
  }


  @GetMapping("/search")
  public String showPage(Model model, Authentication auth,
                         @ModelAttribute("searchResult") SearchDto searchDto) {
    model.addAttribute("searchedBoards",
        boardService.getSearchResults(auth.getName(), searchDto.getText()));
    return "search-result";
  }

  /** Verarbeitung eines Filters */
  @GetMapping("/boards/{uuid}/filter")
  public String filter(Model model, Authentication auth, @PathVariable("uuid") String uuid, @ModelAttribute("newFilter")
      FilterDto filterDto) {

    BoardDto board = boardService.getBoard(uuid, auth.getName());
    model.addAttribute("board", board);

    List<String> selectedCategories = filterDto.getCategories();
    List<AdDto> unfilteredAds = adService.getAdsForBoard(uuid, auth.getName());
    List<AdDto> filteredAds = new ArrayList<>();

    //Ads mit Filtern abgleichen
    for (AdDto adDto : unfilteredAds) {
      for (String s : selectedCategories) {
        if (adDto.getCategory().equals(s)) {
          filteredAds.add(adDto);
          for (AdDto a : filteredAds) {
            System.out.println("Titel: " + a.getTitle() + " " + "Kategorie: " + a.getCategory());
          }
        }
      }
    }

    model.addAttribute("ads", filteredAds);

    model.addAttribute("newFilter", new FilterDto());
    model.addAttribute("categoryList", categoryService.getAllCategories());

    return "board-view";
  }

}