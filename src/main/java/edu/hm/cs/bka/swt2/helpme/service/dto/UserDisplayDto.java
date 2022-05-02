package edu.hm.cs.bka.swt2.helpme.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Transferobjekt für Abfragen von Anwenderdaten.
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
public class UserDisplayDto {

    private String login = "";

    private int boardCount = 5;

    private int subscriptionCount = 7;

    public String getDisplayName() {
        return login;
    }
}

