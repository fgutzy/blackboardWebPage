package edu.hm.cs.bka.swt2.helpme.initialization;

import edu.hm.cs.bka.swt2.helpme.common.SecurityHelper;

import javax.annotation.PostConstruct;

import edu.hm.cs.bka.swt2.helpme.service.AdService;
import edu.hm.cs.bka.swt2.helpme.service.BoardService;
import edu.hm.cs.bka.swt2.helpme.service.UserService;
import edu.hm.cs.bka.swt2.helpme.service.dto.AdCreateDto;
import edu.hm.cs.bka.swt2.helpme.service.dto.BoardCreateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Initialisierung von Demo-Daten. Diese Komponente erstellt beim Systemstart Anwender, Pinnwände,
 * Beobachtungen usw., damit man die Anwendung mit allen Features vorführen kann.
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
    UserService userService;

    @Autowired
    BoardService boardService;

    @Autowired
    AdService adService;

    /**
     * Erstellt die Demo-Daten.
     */
    @PostConstruct
    @SuppressWarnings("unused")
    public void addData() {
        SecurityHelper.escalate(); // admin rights
        LOG.debug("Erzeuge Demo-Daten.");

        userService.createUser(LOGIN_FINE, "fine1234", false);
        userService.createUser(LOGIN_ERNIE, "ernie123", false);
        userService.createUser(LOGIN_BERT, "bert1234", false);

        String fluechtlingshilfe = boardService.createBoard(new BoardCreateDto("Flüchtlingshilfe Memmingen"), LOGIN_FINE);
        adService.createAd(fluechtlingshilfe, new AdCreateDto("Kuchen für Flüchtlingscafé So 4.4."), LOGIN_FINE);
        adService.createAd(fluechtlingshilfe, new AdCreateDto("Helfer Beladung LKW 6.4., 16:00 Uhr"), LOGIN_FINE);
        adService.createAd(fluechtlingshilfe, new AdCreateDto("Übersetzer gesucht Ukr-Deu"), LOGIN_FINE);

        String tsvUtting = boardService.createBoard(new BoardCreateDto("TSV Utting Fußball"), LOGIN_BERT);
        adService.createAd(tsvUtting, new AdCreateDto("Schiri weibl. D, 5.4., 10:00 Uhr"), LOGIN_BERT);
        adService.createAd(tsvUtting, new AdCreateDto("Ausschank Vereinsheim 5.5."), LOGIN_BERT);
        boardService.subscribe(fluechtlingshilfe, LOGIN_BERT);
    }

}
