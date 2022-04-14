package edu.hm.cs.bka.swt2.helpme.initialization;

import javax.annotation.PostConstruct;

import edu.hm.cs.bka.swt2.helpme.common.SecurityHelper;
import edu.hm.cs.bka.swt2.helpme.service.UserService;
import edu.hm.cs.bka.swt2.helpme.service.dto.UserDisplayDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Klasse zur Dateninitialisierung; erstellt einen Admin-Account, wenn keiner vorhanden ist.
 *
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Component
public class AdminInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(AdminInitializer.class);

    @Autowired
    UserService anwenderService;

    @Value("${helpme.admin.login}")
    String adminLogin;

    @Value("${helpme.admin.password}")
    String adminPassword;

    /**
     * Bootstrapping: Überprüft, dass der in der Konfiguration angegebene Administrator-Account
     * existiert und legt ihn ggf. an.
     */
    @PostConstruct
    public void checkAdminAccount() {
        SecurityHelper.escalate();
        List<UserDisplayDto> adminAccounts = anwenderService.findAdmins();
        if (adminAccounts.isEmpty()) {
            LOG.debug("Keine Admin-Accounts konfiguriert, füge Default-Admin {} hinzu.", adminLogin);
            anwenderService.createUser(adminLogin, adminPassword, true);
        }
    }

}
