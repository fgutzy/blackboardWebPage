package edu.hm.cs.bka.swt2.helpme.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Transferobjekt für die Abfrage von Pinwänden.
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
public class BoardDto extends BoardCreateDto {

    private String uuid;

    /**
     * Für die Pinwand verantwortlicher Anwender
     */
    private UserDisplayDto manager;

    /**
     * True, wenn der anfragende User auch der verantwortliche Anwender ist.
     */
    private boolean managing;

    /**
     * true, wenn der anfragende User die Pinwand beobachtet
     */
    private boolean subscribed;
}
