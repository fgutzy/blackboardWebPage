package edu.hm.cs.bka.swt2.helpme.service.dto;

import lombok.*;

/**
 * Transferobjekt für Reaktionen auf Gesuche..
 * <p>
 * Erläuterung: Transferobjekte sind Objekte, die nicht zum Modell gehören, sondern über die mit
 * der Geschäftlogik kommuniziert wird. Sie "passen" zur Schnittstelle der Geschäftslogik und <i>können</i>
 * für die Präsentation verwendet werden. Die Vererbung zwischen den Transferobjekten ist rein pragmatisch; oft sind
 * DTOs Erweiterungen anderer DTOs, weil sie eine Obermenge der Daten enthalten.
 *
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReactionDto extends ReactionCreateDto {

    UserDisplayDto user;

    boolean hidden = false;

    // Transferobjekt für die Reaktionen des Akzeptieren und Ablehnen eines Gesuchs

    boolean adAccepted = false;

    boolean adRejected = false;
    @Getter
    @Setter
    public boolean zugesagenMoeglich = true;

    @Getter
    @Setter
    public boolean absagenMoeglich = true;

    @Getter
    @Setter
    private boolean allowedToClick = true;

    @Getter
    @Setter
    private boolean acceptedMessage;

    @Getter
    @Setter
    private boolean rejectedMessage;

    @Getter
    @Setter
    private boolean recallAcceptanceMessage;

    @Getter
    @Setter
    private boolean recallRejectedMessage;

    @Getter
    @Setter
    private boolean warningMessage;

}
