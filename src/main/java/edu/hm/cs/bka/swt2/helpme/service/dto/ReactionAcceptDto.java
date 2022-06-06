package edu.hm.cs.bka.swt2.helpme.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class ReactionAcceptDto extends ReactionCreateDto {

  private int counter = 1;

  public boolean hasCountAccepted = true;
  }



