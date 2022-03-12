package edu.hm.cs.bka.swt2.helpme.initialization;

import edu.hm.cs.bka.swt2.helpme.common.SecurityHelper;
import edu.hm.cs.bka.swt2.helpme.service.UserService;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Initialisierung von Demo-Daten. Diese Komponente erstellt beim Systemstart Anwender, Topics,
 * Abonnements usw., damit man die Anwendung mit allen Features vorführen kann.
 *
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Component
@Profile("demo")
public class DemoDataInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(DemoDataInitializer.class);

    private static final String LOGIN_FINE = "fine";
    private static final String LOGIN_ERNIE = "ernie";
    private static final String LOGIN_BERT = "bert";

    @Autowired
    UserService anwenderService;

    /**
     * Erstellt die Demo-Daten.
     */
    @PostConstruct
    @SuppressWarnings("unused")
    public void addData() {
        SecurityHelper.escalate(); // admin rights
        LOG.debug("Erzeuge Demo-Daten.");

        anwenderService.legeAn(LOGIN_FINE, "f", false);
        anwenderService.legeAn(LOGIN_ERNIE, "e", false);
        anwenderService.legeAn(LOGIN_BERT, "b", false);
    }
}
