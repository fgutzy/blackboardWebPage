package edu.hm.cs.bka.swt2.helpme.service;

import edu.hm.cs.bka.swt2.helpme.persistence.*;
import edu.hm.cs.bka.swt2.helpme.service.dto.*;

import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;

@Component
@Transactional(rollbackFor = Exception.class)
@PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
@Slf4j
public class BoardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private DtoFactory dtoFactory;

    /**
     * Service-Methode zum Erstellen einer Pinnwand
     */
    public String createBoard(BoardCreateDto boardDto, String description, String login) {
        log.info("Erstelle Pinnwand {}.", boardDto.getTitle());
        log.debug("Login {} erstellt Pinnwand {}.", login, boardDto.getTitle());
        String uuid = UUID.randomUUID().toString();

        User user = userRepository.findByIdOrThrow(login);

        String title = boardDto.getTitle();

        validateBoardDto(boardDto);

        Board board = new Board(uuid, boardDto.getTitle(), description, user);
        boardRepository.save(board);
        return uuid;
    }

    private void validateBoardDto(BoardCreateDto boardDto) {
        //Methode zur Einschränkung der Titel-Länge
        if (boardDto.getTitle().length() < 10 || boardDto.getTitle().length() > 60) {
            throw new ValidationException("Der Titel muss zwischen 10 und 60 Zeichen lang sein!");
        }

        //Methode zur Einschränkung der Besschreibungs-Länge
        if (boardDto.getDescription().length() < 20 || boardDto.getDescription().length() > 150) {
            throw new ValidationException("Die Beschreibung muss zwischen 20 und 150 Zeichen lang sein!");
        }
    }

    /**
     * Service-Methode zur Abfrage verwalteter Pinnwände
     */
    public List<BoardDto> getManagedBoards(String login) {
        log.info("Zeig Boards von Login {} an.", login);
        User user = userRepository.findByIdOrThrow(login);
        List<Board> managedBoards = boardRepository.findByManagerOrderByTitleAsc(user);
        List<BoardDto> result = new ArrayList<>();
        for (Board board : managedBoards) {
            result.add(dtoFactory.createDto(board, user));
        }
        return result;
    }

    /**
     * Service-Methode zur Abfrage einer Pinnwand
     */
    public BoardDto getBoard(String uuid, String login) {
        log.info("Board {} wird abgefragt.", uuid);
        log.debug("Board {} von Login {} wird abgefragt.", uuid, login);
        User user = userRepository.findByIdOrThrow(login);
        Board board = boardRepository.findByUuidOrThrow(uuid);
        return dtoFactory.createDto(board, user);
    }

    /**
     * Service-Methode zur Abfrage beobachteter Pinnwände.
     */
    public List<BoardDto> getSubscriptions(String login) {
        log.info("Beobachtete Pinnwände von Login {} werden angezeigt.", login);
        User user = userRepository.findByIdOrThrow(login);
        List<Subscription> subscriptions = subscriptionRepository.findByObserverOrderByBoard_Title(user);
        List<BoardDto> result = new ArrayList<>();
        for (Subscription subscription : subscriptions) {
            BoardDto dto = dtoFactory.createDto(subscription.getBoard(), user);
            result.add(dto);
        }
        return result;
    }

    public void subscribe(String uuid, String login) {
        log.info("Pinnwand {} wird beobachtet.", uuid);
        log.debug("Pinnwand {} wird von Login {} beobachtet.", uuid, login);
        User user = userRepository.findByIdOrThrow(login);
        Board board = boardRepository.findByUuidOrThrow(uuid);
        if (subscriptionRepository.findByBoardAndObserver(board, user) != null) {
            log.debug("Login {} beobachtet Board {} bereits.", login, uuid);
            throw new ValidationException("Beobachtung existiert schon!");
        }
        Subscription subscription = new Subscription(board, user);
        subscriptionRepository.save(subscription);
    }

    public void unsubscribe(String uuid, String login) {
        log.info("Pinnwand {} wird nicht mehr beobachtet.", uuid);
        log.debug("Pinnwand {} wird von Login {} nicht mehr beobachtet.", uuid, login);
        User user = userRepository.findByIdOrThrow(login);
        Board board = boardRepository.findByUuidOrThrow(uuid);
        Subscription byBoardAndObserver = subscriptionRepository.findByBoardAndObserver(board, user);
        if (byBoardAndObserver == null) {
            log.debug("Pinnwand {} wir von Login {} nicht beobachtet.", uuid, login);
            throw new ValidationException("Pinnwand wird nicht beobachtet!");
        }
        subscriptionRepository.delete(byBoardAndObserver);
    }

    public void deleteBoard(String uuid, String login) {
        User user = userRepository.findByIdOrThrow(login);
        Board board = boardRepository.findByUuidOrThrow(uuid);
        if (board.getManager() != user) {
            throw new AccessDeniedException("Nur Verwalter dürfen Pinnwände löschen.");
        }
        boardRepository.deleteById(uuid);
    }

  public void updateBoard(String uuid, BoardCreateDto boardDto, String description, String login) {
        log.info("Aktualisiere Pinnwand {}.", uuid);
        log.debug("Aktualisiere Pinnwand {} auf {} als User {}.", uuid, boardDto, login);
      User user = userRepository.findByIdOrThrow(login);
      Board board = boardRepository.findByUuidOrThrow(uuid);
      if (board.getManager() != user) {
          log.warn("Illegaler Zugriff auf Pinnwand-Änderung!");
          throw new AccessDeniedException("Nur Verwalter dürfen Pinnwände ändern.");
      }
      validateBoardDto(boardDto);
      board.setDescription(boardDto.getDescription());
      board.setTitle(boardDto.getTitle());
  }

  public List<SubscriptionDto> getSubscriptionsForBoard(String uuid, String login) {
      log.info("Zeige Beobachtungen auf Pinnwand {} an.", uuid);
      log.debug("Zeige Beobachtungen auf Pinnwand {} von User {}. an", uuid, login);
      User user = userRepository.findByIdOrThrow(login);
      Board board = boardRepository.findByUuidOrThrow(uuid);
      if (board.getManager() != user) {
          log.warn("Illegaler Zugriff auf Pinnwand-Beobachter!");
          throw new AccessDeniedException("Nur Verwalter dürfen Beocbachter verwalten.");
      }
      List<SubscriptionDto> dtos = new ArrayList<>();
      for(Subscription s : board.getSubscriptions()){
          dtos.add(dtoFactory.createSubscriptionDto(s));
      }
      return dtos;
  }

    public void setWriteAccess(String uuid, String observerLogin, boolean writeAccess, String login) {
        User user = userRepository.findByIdOrThrow(login);
        Board board = boardRepository.findByUuidOrThrow(uuid);
        if (board.getManager() != user) {
            log.warn("Illegaler Zugriff auf Pinnwand-Beobachter!");
            throw new AccessDeniedException("Nur Verwalter dürfen Beocbachter verwalten.");
        }
        User observer = userRepository.findByIdOrThrow(observerLogin);
        Subscription s = subscriptionRepository.findByBoardAndObserver(board, observer);
        s.setWriteAccess(writeAccess);
    }

    public List<BoardDto> getSearchResults(String login, String suchbegriff) {
        log.info("Suchergebnisse für " + suchbegriff + " werden angezeigt!");
        User user = userRepository.findByIdOrThrow(login);
        List<BoardDto> result = new ArrayList<>();

        List<Board> managedAndObservedBoards = boardRepository.findDistinctByManagerOrSubscriptionsObserverOrderByTitle(user, user);

        //Wenn Suchbegriff null, leer oder nur aus Leerzeichen bestehend, dann keine Ergebnisse anzeigen
        if(suchbegriff == null || suchbegriff.trim().length() == 0) {
            return result;
        }

        //Wenn Suchbegriff "*", alle Boards als Ergebnis anzeigen
        if(suchbegriff.trim().equals("*")) {
            for (Board board : managedAndObservedBoards) {
                result.add(dtoFactory.createDto(board, user));
            }
            return result;
        }

        for (Board board : managedAndObservedBoards) {
            if(board.getTitle().toLowerCase().contains(suchbegriff.toLowerCase())) {
                result.add(dtoFactory.createDto(board, user));
            }
        }

        return result;
    }
}