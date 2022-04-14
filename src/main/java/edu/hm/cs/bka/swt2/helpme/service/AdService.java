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
    public Long createAd(String boardUuid, AdCreateDto dto, String login) {
        Board board = boardRepository.findByUuidOrThrow(boardUuid);
        if (!board.getManager().getLogin().equals(login)) {
            throw new AccessDeniedException("");
        }

        String title = dto.getTitle();
        if (title.length() < 8 || title.length() > 50) {
            throw new ValidationException("Der Titel muss zwischen 8 und 50 Zeichen lang sein!");
        }

        Ad ad = factory.createAd(dto, board);
        adRepository.save(ad);
        return ad.getId();
    }

    /**
     * Service-Methode zum Ermitteln aller Gesuche zu einer Pinwand.
     */
    public List<AdDto> getAdsForBoard(String boardUuid, String login) {
        List<AdDto> result = new ArrayList<>();
        User user = userRepository.findByIdOrThrow(login);
        Board board = boardRepository.findByUuidOrThrow(boardUuid);
        List<Ad> ads = adRepository.getByBoard(board);
        for (Ad ad : ads) {
            AdDto dto = factory.createAdDto(ad, user);
            result.add(dto);
        }
        return result;
    }

    /**
     * Service-Methode zum Ermitteln aller Gesuche auf beobachteten Pinnwänden des Anwenders.
     */
    public List<AdDto> getAdsForUser(String login) {
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
        User user = userRepository.findByIdOrThrow(login);
        Ad ad = adRepository.findByIdOrThrow(adId);
        if (ad.getBoard().getManager() != user) {
            throw new AccessDeniedException("Nur Board-Verwalter dürfen Gesuche löschen!");
        }
        adRepository.delete(ad);
    }

    /**
     * Service-Methode zum Abfragen eines einzelnen Gesuchs.
     */
    public AdDto getAd(Long adId, String login) {
        User user = userRepository.findByIdOrThrow(login);
        Ad ad = adRepository.findByIdOrThrow(adId);
        return factory.createAdDto(ad, user);
    }

    /**
     * Service-Methode zum Ausblenden eines Gesuchs.
     */
    public void hideAd(Long adId, String login) {
        Ad ad = adRepository.findByIdOrThrow(adId);
        User user = userRepository.findByIdOrThrow(login);
        Reaction r = getOrCreateReaction(ad, user);
        r.setHidden(true);
    }

    /**
     * Service-Methode zum Ergänzen einer Reaktion.
     */
    public void setReaction(long adId, String login, ReactionCreateDto dto) {
        Ad ad = adRepository.findByIdOrThrow(adId);
        User user = userRepository.findByIdOrThrow(login);
        Reaction r = getOrCreateReaction(ad, user);
        r.setComment(dto.getComment());
    }

    /**
     * Hilfsmethode zum Erstellen einer Reaktion.
     */
    private Reaction getOrCreateReaction(Ad ad, User user) {
        Reaction reaction = reactionRepository.findByAdAndUser(ad, user);
        if (reaction == null) {
            reaction = new Reaction(user, ad);
            reactionRepository.save(reaction);
        }
        return reaction;
    }

}
