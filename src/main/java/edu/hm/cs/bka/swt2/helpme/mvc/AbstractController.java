package edu.hm.cs.bka.swt2.helpme.mvc;

import edu.hm.cs.bka.swt2.helpme.common.SecurityHelper;
import edu.hm.cs.bka.swt2.helpme.service.UserService;
import edu.hm.cs.bka.swt2.helpme.service.dto.UserDisplayDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Abstrakte Basisklasse für alle Controller, sorgt dafür, dass einige Verwaltungsattribute immer an
 * die Views übertragen werden.
 *
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
public abstract class AbstractController {

    @Autowired
    private UserService userService;

    @ModelAttribute("administration")
    private boolean isAdministrator(Authentication auth) {
        return SecurityHelper.isAdmin(auth);
    }

    @ModelAttribute("user")
    private UserDisplayDto user(Authentication auth) {
        if (auth != null) {
            return userService.getUserInfo(auth.getName());
        }
        return null;
    }

}
