package edu.hm.cs.bka.swt2.helpme.service.dto;

import lombok.*;

/**
 * Transferobjekt für die Erstellung von Gesuchen.
 * <p>
 * Erläuterung: Transferobjekte sind Objekte, die nicht zum Modell gehören, sondern über die mit
 * der Geschäftlogik kommuniziert wird. Sie "passen" zur Schnittstelle der Geschäftslogik und <i>können</i>
 * für die Präsentation verwendet werden. Die Vererbung zwischen den Transferobjekten ist rein pragmatisch; oft sind
 * DTOs Erweiterungen anderer DTOs, weil sie eine Obermenge der Daten enthalten.
 *
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class AdCreateDto {

    @NonNull
    private String title;

    @NonNull
    private String description;

    private String category;

}
