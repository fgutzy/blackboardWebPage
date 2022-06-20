package edu.hm.cs.bka.swt2.helpme.persistence;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Entitätsklasse für Reaktionen.
 */
@Entity
@NoArgsConstructor
public class Reaction {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    private Long id;

    @Getter
    @Setter
    boolean hidden;

    @Getter
    @Setter
    String comment;

    /**
     * Erstellt die Reaction Entitäten für die Frage "Wurde eine Ad akzeptiert/abgelehnt?"
     */

    @Getter
    @Setter
    public int zugesagt = 2;

    @Getter
    @Setter
    public boolean zugesagenMoeglich = true;

    @Getter
    @Setter
    public int abgesagt = 2;

    @ManyToOne
    @Getter
    User user;

    @ManyToOne
    @Getter
    Ad ad;

    public Reaction(User user, Ad ad) {
        this.ad = ad;
        this.user = user;
    }


}
