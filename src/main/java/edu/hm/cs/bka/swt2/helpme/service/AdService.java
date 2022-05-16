package edu.hm.cs.bka.swt2.helpme.service;

import edu.hm.cs.bka.swt2.helpme.persistence.*;
import edu.hm.cs.bka.swt2.helpme.service.dto.*;
import javax.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import org.yaml.snakeyaml.tokens.BlockEndToken;

/**
 * Service-Klasse für Methoden, die auf einzelne Gesuche bezogen sind.
 * <p>
 * Eine Service-Klasse ist ein Singleton-Objekt, dass den Zugriff auf das Modell und die Verarbeitung von Anfragen in
 * Transaktionen kapselt.
 */
@Component
@Transactional(rollbackFor = Exception.class)
@PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
@Slf4j
public class AdService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private AdRepository adRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReactionRepository reactionRepository;

    @Autowired
    private DtoFactory factory;

    /**
     * Service-Methode zum Erstellen eines Gesuchs.
     */
    public Long createAd(String boardUuid, AdCreateDto dto, String description, String login) {
        log.info("Eine Anzeige wird auf {} erstellt.", boardUuid);
        log.debug("Eine Anzeige {} wird von {} erstellt.", boardUuid, login);
        Board board = boardRepository.findByUuidOrThrow(boardUuid);
        if (!board.getManager().getLogin().equals(login)) {
            log.warn("Login {} versucht, Anzeigen auf fremder Pinnwand {} zu erstellen", login, boardUuid);
            throw new AccessDeniedException("");
        }

        String title = dto.getTitle();


        //Methode zur Einschränkung der Titel-Länge
        if (title.length() < 8 || title.length() > 50) {
            throw new ValidationException("Der Titel muss zwischen 8 und 50 Zeichen lang sein!");
        }

        if (description.length() < 20 || description.length() > 150) {
            throw new ValidationException("Die Beschreibung muss zwischen 20 und 150 Zeichen lang sein.");
        }

        Ad ad = factory.createAd(dto, board, dto.getDescription());
        adRepository.save(ad);
        return ad.getId();
    }

    /**
     * Service-Methode zum Ermitteln aller Gesuche zu einer Pinwand.
     */
    public List<AdDto> getAdsForBoard(String boardUuid, String login) {
        log.info("Ads für {} werden gesucht.", boardUuid);
        log.debug("Login {} hat nach Ads auf {} gesucht.",login, boardUuid);
        List<AdDto> result = new ArrayList<>();
        List<AdDto> hiddenAds = new ArrayList<>(); //Hilfs-List für ausgeblendete Ads
        User user = userRepository.findByIdOrThrow(login);
        Board board = boardRepository.findByUuidOrThrow(boardUuid);
        List<Ad> ads = adRepository.getByBoardOrderByIdDesc(board);
        for (Ad ad : ads) {
            AdDto dto = factory.createAdDto(ad, user);
            if (!getOrCreateReaction(ad, user).isHidden()) { //Wenn Ad nicht ausgeblendet
                result.add(dto); //dann zu Ergebnis-List hinzufügen
            } else { //sonst, wenn Ad ausgeblendet
                hiddenAds.add(dto); //dann zu Hilfs-List für ausgeblendete Ads hinzufügen
            }
        }
        result.addAll(hiddenAds); //Ausgeblendete Ads zu Ergebnis-List hinzufügen
        return result;
    }

    /**
     * Service-Methode zum Ermitteln aller Gesuche auf beobachteten Pinnwänden des Anwenders.
     */
    public List<AdDto> getAdsForUser(String login) {
        log.info("Ads von Login {} wird abgefragt.", login);
        User user = userRepository.findByIdOrThrow(login);
        List<AdDto> result = new ArrayList<>();
        for (Subscription s : subscriptionRepository.findByObserver(user)) {
            for (Ad ad : s.getBoard().getAds()) {
                AdDto dto = factory.createAdDto(ad, user);
                if (!dto.getUserReaction().isHidden()) {
                    result.add(dto);
                }
            }
        }
        return result;
    }

    /**
     * Service-Methode zum Löschen eines Gesuchs.
     */
    public void deleteAd(Long adId, String login) {
        log.info("Ad {} wird gelöscht.", adId);
        log.debug("Ad {} von Login {} wird gelöscht.", adId, login);
        User user = userRepository.findByIdOrThrow(login);
        Ad ad = adRepository.findByIdOrThrow(adId);
        if (ad.getBoard().getManager() != user) {
            log.debug("Ad {} darf nicht von Login {} gelsöcht werden, " +
                "da dieser nicht der Verwalter ist", adId, login);
            throw new AccessDeniedException("Nur Board-Verwalter dürfen Gesuche löschen!");
        }
        adRepository.delete(ad);
    }

    /**
     * Service-Methode zum Abfragen eines einzelnen Gesuchs.
     */
    public AdDto getAd(Long adId, String login) {
        log.info("Ad {} wird angezeigt.", adId);
        log.debug("Ad {} von Login {} wird angezeigt.", adId, login);
        User user = userRepository.findByIdOrThrow(login);
        Ad ad = adRepository.findByIdOrThrow(adId);
        return factory.createAdDto(ad, user);
    }

    /**
     * Service-Methode zum Ausblenden eines Gesuchs.
     */
    public void hideAd(Long adId, String login) {
        log.info("Ad {} wird ausgeblendet.", adId);
        log.debug("Ad {} von Login {} wird ausgeblendet.", adId, login);
        Ad ad = adRepository.findByIdOrThrow(adId);
        User user = userRepository.findByIdOrThrow(login);
        Reaction r = getOrCreateReaction(ad, user);
        r.setHidden(true);
    }

    /**
     * Service-Methode zum wieder Einblenden eines Gesuchs.
     */
    public void showAdAgain(Long adId, String login) {
        log.info("Ad {} wird wieder eingeblendet.", adId);
        log.debug("Ad {} von Login {} wird wieder eingeblendet.", adId, login);
        Ad ad = adRepository.findByIdOrThrow(adId);
        User user = userRepository.findByIdOrThrow(login);
        Reaction r = getOrCreateReaction(ad, user);
        r.setHidden(false);
    }

    /**
     * Service-Methode zum Ergänzen einer Reaktion.
     */
    public void setReaction(long adId, String login, ReactionCreateDto dto) {
        log.info("Ad {} wird um eine Rekation ergänzt.", adId);
        log.debug("Ad {} wird von Login {} um eine Reaktion {} ergänzt.", adId, login, dto);
        Ad ad = adRepository.findByIdOrThrow(adId);
        User user = userRepository.findByIdOrThrow(login);
        Reaction r = getOrCreateReaction(ad, user);
        r.setComment(dto.getComment());
    }

    /**
     * Hilfsmethode zum Erstellen einer Reaktion.
     */
    private Reaction getOrCreateReaction(Ad ad, User user) {
        log.info("Eine Reaktion wird zu Ad {} erstellt.", ad);
        log.debug("Eine Reaktion wird von User {} zu Ad {} erstellt.", ad, user);
        Reaction reaction = reactionRepository.findByAdAndUser(ad, user);
        if (reaction == null) {
            reaction = new Reaction(user, ad);
            reactionRepository.save(reaction);
        }
        return reaction;
    }
}