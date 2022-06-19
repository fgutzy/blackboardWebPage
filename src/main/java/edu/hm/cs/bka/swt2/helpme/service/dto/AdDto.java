package edu.hm.cs.bka.swt2.helpme.service.dto;


import edu.hm.cs.bka.swt2.helpme.persistence.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import lombok.*;

/**
 * Transferobjekt für die Abfrage von Gesuchen.
 * <p>
 * Erläuterung: Transferobjekte sind Objekte, die nicht zum Modell gehören, sondern über die mit
 * der Geschäftlogik kommuniziert wird. Sie "passen" zur Schnittstelle der Geschäftslogik und <i>können</i>
 * für die Präsentation verwendet werden. Die Vererbung zwischen den Transferobjekten ist rein pragmatisch; oft sind
 * DTOs Erweiterungen anderer DTOs, weil sie eine Obermenge der Daten enthalten.
 *
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class AdDto extends AdCreateDto {

    @NonNull
    private Long id;

    private BoardDto board;


    /**
     * Hinterlegte Reaktion des Users
     */
    private ReactionDto userReaction = new ReactionDto();

    /**
     * Alle Reaktionen
     */
    private List<ReactionDto> reactions = new ArrayList<>();


    //Erstellt Abfrageobjekte für die Werte des Counters der Zusagen/Absagen auf Gesuche
    private int acceptCounter;

    private int rejectCounter;

    //Erstellt Abfrageobjekte für das Erstellungsdatum
    private LocalDate dateAdCreated;

    //Erstellt das Abfrageobjekt für das Löschdatum
    private LocalDate dateToDeleteAd;

    //Message bei Zusage
    private boolean acceptedMessage;

    //Message bei Absage
    private boolean rejectedMessage;

    //Message bei Zurücknahme der Zusage
    private boolean recallAcceptanceMessage;

    //Message bei Zurücknahme der Absage
    private boolean recallRejectedMessage;


    private boolean canAccept = true;





    //public List<String> getUsersThatAcceptedAd = new ArrayList<>();


}
