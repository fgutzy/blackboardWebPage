package edu.hm.cs.bka.swt2.helpme.service.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Erstellt Abfrageobjekte für die Werte des Counters der Zusagen/Absagen auf Gesuche
     */

    private int acceptCounter;

    private int rejectCounter;

}
