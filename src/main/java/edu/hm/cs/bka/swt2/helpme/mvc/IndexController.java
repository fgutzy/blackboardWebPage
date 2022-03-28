package edu.hm.cs.bka.swt2.helpme.mvc;

import edu.hm.cs.bka.swt2.helpme.service.AdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller-Klasse für die Startseite.
 * <br>
 * Controller reagieren auf Aufrufe von URLs. Sie benennen ein View-Template (Thymeleaf-Vorlage) und
 * stellen Daten zusammen, die darin dargestellt werden. Dafür verwenden Sie Methoden der
 * Service-Schicht.
 *
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Controller
@SuppressWarnings("unused")
public class IndexController extends AbstractController {

    @Autowired
    private AdService adService;

    /**
     * Erstellt die Landing-Page.
     */
    @GetMapping("/")
    public String getIndexView(Model model, Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            model.addAttribute("ads", adService.getAdsForUser(auth.getName()));
        }
        return "index";
    }
}


