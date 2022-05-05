package edu.hm.cs.bka.swt2.helpme.service;

import edu.hm.cs.bka.swt2.helpme.persistence.*;
import edu.hm.cs.bka.swt2.helpme.service.dto.BoardCreateDto;
import edu.hm.cs.bka.swt2.helpme.service.dto.DtoFactory;
import edu.hm.cs.bka.swt2.helpme.service.dto.BoardDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

        //Methode zur Einschränkung der Titel-Länge
        if (title.length() < 10 || title.length() > 60) {
            throw new ValidationException("Der Titel muss zwischen 10 und 60 Zeichen lang sein!");
        }

        if (description.length() < 20 || description.length() > 150) {
            throw new ValidationException("Die Beschreibung muss zwischen 20 und 150 Zeichen lang sein!");
        }

        Board board = new Board(uuid, boardDto.getTitle(), description, user);
        boardRepository.save(board);
        return uuid;
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
}
