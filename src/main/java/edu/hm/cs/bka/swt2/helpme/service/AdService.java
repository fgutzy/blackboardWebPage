package edu.hm.cs.bka.swt2.helpme.service;

import edu.hm.cs.bka.swt2.helpme.persistence.*;
import edu.hm.cs.bka.swt2.helpme.service.dto.*;
import java.time.Instant;
import java.time.LocalDate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;
import org.hibernate.engine.internal.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.convert.JodaTimeConverters;
import org.springframework.format.datetime.joda.JodaTimeContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
    log.info("Eine Anzeige wird auf {} erstellt.", boardUuid);
    log.debug("Eine Anzeige {} wird von {} erstellt.", boardUuid, login);
    User user = userRepository.findByIdOrThrow(login);
    Board board = boardRepository.findByUuidOrThrow(boardUuid);

    if (!board.hasWriteAccess(user)) {
      log.warn("Login {} versucht, Anzeigen auf fremder Pinnwand {} zu erstellen", login,
          boardUuid);
      throw new AccessDeniedException("");
    }

    String title = dto.getTitle();
    String description = dto.getDescription();

    //Methode zur Einschränkung der Titel-Länge
    if (title.length() < 8 || title.length() > 50) {
      throw new ValidationException("Der Titel muss zwischen 8 und 50 Zeichen lang sein!");
    }

    if (description.length() < 20 || description.length() > 150) {
      throw new ValidationException("Die Beschreibung muss zwischen 20 und 150 Zeichen lang sein.");
    }

    //Erstellt das heutige Datum
    LocalDate dateCreation = LocalDate.now();
    //Erstellt das Datum, an dem die Ad automatisch gelöscht wird (z.B. 7 Tage)
    LocalDate dateToDelete = dateCreation.plus(7, ChronoUnit.DAYS);

    Ad ad = factory.createAd(dto, board, dto.getDescription(), dateCreation, dateToDelete);
    adRepository.save(ad);
    return ad.getId();
  }


  /**
   * Service-Methode zum Ermitteln aller Gesuche zu einer Pinwand.
   */
  public List<AdDto> getAdsForBoard(String boardUuid, String login) {
    log.info("Ads für {} werden gesucht.", boardUuid);
    log.debug("Login {} hat nach Ads auf {} gesucht.", login, boardUuid);
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
   * Service-Methode zum zählen von Zusagen
   */

  public void countAccept(Long adId, String login) {
    log.info("Ad {} wurde zugesagt", adId);
    log.info("Ad {} wurde von Login {} zugesagt", adId, login);
    Ad ad = adRepository.findByIdOrThrow(adId);
    User user = userRepository.findByIdOrThrow(login);
    Reaction r = getOrCreateReaction(ad, user);
    // List<String> zwischenspeicher = new ArrayList<>();


    if (r.isZugesagenMoeglich() &&
        r.isAllowedToClick()) { // wenn Zusagen möglich ist und clicken erlaubt ist
      r.setAllowedToClick(false);               // nicht mehr erlaubt zu clicken
      r.setZugesagenMoeglich(false);             // Zusagen nicht mehr möglich
      ad.setAcceptCounter(ad.getAcceptCounter() + 1);
      r.acceptedMsg();
      // zwischenspeicher.add(user.getName());   //Zugesagten User der Liste hinzufügen
      // log.info("User {} hat Ad {} zugesagt", user.getLogin(), ad.getTitle());

    } else if (!r
        .isZugesagenMoeglich()) { // wenn Zusagen nicht möglich ist (also Zusage zurückgerufen werden soll)
      r.setAllowedToClick(true);           // clicken wieder erlaubt
      r.setZugesagenMoeglich(true);         // Zusagen wieder möglich
      ad.setAcceptCounter(ad.getAcceptCounter() - 1);
      r.recallAcceptedMsg();
      //zwischenspeicher.remove(user.getName()); //Zugesagten User entfernen
      //log.info("User {} hat Zusage auf Ad {} zurückgerufen", user.getLogin(), ad.getTitle());
    } else {
      r.warningMsg();
      log.warn("User versucht gleichzeitig Zu und Abzusagen!");
    }
    //ad.getUsersThatAcceptedAd().addAll(zwischenspeicher); //Alle Zugesagten User der Liste hinzufügen
  }


  /**
   * Service-Methode zum zählen von Zusagen
   */

  public void countReject(Long adId, String login) {
    log.info("Ad {} wurde abgesagt", adId);
    log.info("Ad {} wurde von Login {} abgesagt", adId, login);
    Ad ad = adRepository.findByIdOrThrow(adId);
    User user = userRepository.findByIdOrThrow(login);
    Reaction r = getOrCreateReaction(ad, user);
    // List<String> zwischenspeicher = new ArrayList<>();

    if (r.isAbsagenMoeglich() &&
        r.isAllowedToClick()) { /// wenn Absagen möglich ist und clicken erlaubt ist
      r.setAllowedToClick(false);    // nicht mehr erlaubt zu clicken
      r.setAbsagenMoeglich(false);    // Absagen nicht mehr möglich
      ad.setRejectCounter(ad.getRejectCounter() + 1);
      r.rejectedMsg();
      // zwischenspeicher.add(user.getName()); //abgesagten User der Liste hinzufügen
      // log.info("User {} hat Ad {} abgesagt", user.getLogin(), ad.getTitle());

    } else if (!r
        .isAbsagenMoeglich()) {    // wenn Absagen nicht möglich ist (also Absage zurückgerufen werden soll)
      r.setAllowedToClick(true);   // clicken wieder erlaubt
      r.setAbsagenMoeglich(true);   // Absagen wieder erlaubt
      ad.setRejectCounter(ad.getRejectCounter() - 1);
      r.recallRejectedMsg();
      //zwischenspeicher.remove(user.getName());    //Abgesagten User entfernen
      //log.info("User {} hat Absage auf Ad {} zurückgerufen", user.getLogin(), ad.getTitle());
    } else {
      r.warningMsg();
      log.warn("User versucht gleichzeitig Zu und Abzusagen!");
    }
    //ad.getUsersThatRejetedAd().addAll(zwischenspeicher);  //Alle Zugesagten User der Liste hinzufügen

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