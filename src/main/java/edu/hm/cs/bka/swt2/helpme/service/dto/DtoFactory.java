package edu.hm.cs.bka.swt2.helpme.service.dto;

import edu.hm.cs.bka.swt2.helpme.persistence.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Hilfskomponente zum Erstellen der Transferobjekte aus den Entity-Objekten (und z.T. umgekehrt).
 * Da dieses Mapping oft an vielen Stellen benötigt wird, lohnt sich die Zusammenführung.
 *
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Component
public class DtoFactory {

    /**
     * Ein Modelmapper ist ein magisches Tool, um gleichnamige Attribute zwischen Objekten zu kopieren.!
     */
    @Autowired
    private ModelMapper mapper;

    @Autowired
    private BoardRepository boardRepository;

    /**
     * Erstellt ein Transferobjekt aus einem Anwender-Objekt.
     *
     * @param user Anwender, dessen Informationen angefragt werden.
     * @return Transferobjekt
     */
    public UserDisplayDto createDto(User user) {
        UserDisplayDto dto = mapper.map(user, UserDisplayDto.class);
        dto.setBoardCount(boardRepository.countByManager(user));
        dto.setSubscriptionCount(user.getSubscriptions().size());
        return dto;
    }

    /**
     * Erstellt ein {@link BoardDto} aus einem {@link Board}. Dabei enthält das Objekt anfragespezifische Informationen,
     * z.B. ob der/die anfragende Anwender:in die Pinnwnad beobachtet.
     *
     * @param board   Pinnwand, deren Daten angefragt werden.
     * @param forUser Anwender, für dessen Kontext die Antwort bereitgestellt wird.
     * @return Transferobjekt
     */
    public BoardDto createDto(Board board, User forUser) {
        BoardDto boardDto = mapper.map(board, BoardDto.class);
        boardDto.setManager(createDto(board.getManager()));
        boardDto.setSubscribed(board.isObservedBy(forUser));
        boardDto.setManaging(forUser == board.getManager());
        return boardDto;
    }

    /**
     * Erstellt ein Gesuch aus einem Tranferobjekt ({@link AdCreateDto} auf einer Pinnwand.
     *
     * @param adCreateDto zu erstellendes Gesuch
     * @return Entität (ungespeichert).
     */
    public Ad createAd(AdCreateDto adCreateDto, Board board) {
        Ad ad = mapper.map(adCreateDto, Ad.class);
        ad.setBoard(board);
        return ad;
    }

    /**
     * Erstellt ein Transferobjekt aus einem Gesuch. Dabei enthält das Objekt anfragespezifische Informationen,
     * z.B. ob der/die anfragende Anwender:in die Pinnwand beobachtet.
     *
     * @param ad      Gesuch, deren Daten angefragt werden.
     * @param forUser Anwender, für dessen Kontext die Antwort bereitgestellt wird.
     * @return Transferobjekt
     */
    public AdDto createAdDto(Ad ad, User forUser) {
        AdDto adDto = mapper.map(ad, AdDto.class);
        adDto.setBoard(createDto(ad.getBoard(), forUser));
        List<ReactionDto> reactionDtos = new ArrayList<>();
        for (Reaction reaction : ad.getReactions()) {
            reactionDtos.add(createReactionDto(reaction));
            if (reaction.getUser() == forUser) {
                adDto.setUserReaction(createReactionDto(reaction));
            }
        }
        adDto.setReactions(reactionDtos);
        return adDto;
    }

    /**
     * Erstellt ein Transferobjekt aus einer Reaktion.
     *
     * @param reaction Gesuch, deren Daten angefragt werden.*
     * @return Transferobjekt
     */
    public ReactionDto createReactionDto(Reaction reaction) {
        ReactionDto reactionDto = mapper.map(reaction, ReactionDto.class);
        reactionDto.setUser(createDto(reaction.getUser()));
        return reactionDto;
    }
}
