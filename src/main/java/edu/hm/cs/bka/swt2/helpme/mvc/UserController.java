package edu.hm.cs.bka.swt2.helpme.mvc;

import edu.hm.cs.bka.swt2.helpme.service.UserService;
import edu.hm.cs.bka.swt2.helpme.service.dto.UserManagementDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller-Klasse für alle Interaktionen, die die Verwaltung von Anwendern betrifft.
 * <br>
 * Controller reagieren auf Aufrufe von URLs. Sie benennen ein View-Template (Thymeleaf-Vorlage) und
 * stellen Daten zusammen, die darin dargestellt werden. Dafür verwenden Sie Methoden der
 * Service-Schicht.
 *
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Controller
public class UserController extends AbstractController {

    @Autowired
    private UserService userService;

    /**
     * Erzeugt eine Listenansicht mit allen Anwendern.
     */
    @GetMapping("/users")
    public String getUserListView(Model model, Authentication auth) {
        model.addAttribute("users", userService.getAllUsers());
        return "user-listview";
    }

    /**
     * Erzeugt eine Formularansicht für das Erstellen eines Anwenders.
     */
    @GetMapping("/users/create")
    public String getUserCreationView(Model model) {
        model.addAttribute("newUser", new UserManagementDto());
        return "user-creation";
    }

    /**
     * Nimmt den Formularinhalt vom Formular zum Erstellen eines Anwenders entgegen und legt einen
     * entsprechenden Anwender an. Kommt es dabei zu einer Exception, wird das Erzeugungsformular
     * wieder angezeigt und eine Fehlermeldung eingeblendet. Andernfalls wird auf die Listenansicht
     * der Anwender weitergeleitet und das Anlegen in einer Einblendung bestätigt.
     */
    @PostMapping("/users")
    public String handleUserCreation(Model model,
                                     @ModelAttribute("newUser") UserManagementDto anwender,
                                     RedirectAttributes redirectAttributes) {
        try {
            userService.createUser(anwender.getLogin(), anwender.getPassword(), false);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/users/create";
        }
        redirectAttributes.addFlashAttribute("success",
                "Anwender " + anwender.getLogin() + " erstellt.");
        return "redirect:/users";
    }
}
