package edu.hm.cs.bka.swt2.helpme.persistence;

import java.time.LocalDate;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Entitätsklasse für Gesuche.
 */
@Entity
@NoArgsConstructor // erforderlich für JPA
public class Ad {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    private Long id;

    @NotNull
    @Length(min = 8, max = 50)
    @Column(length = 50)
    @Getter
    @Setter
    private String title;

    @NotNull
    @ManyToOne
    @Getter
    @Setter
    private Board board;

    @NotNull
    @Column(length = 150)
    @Length(min = 20, max = 150)
    @Getter
    @Setter
    private String description;

    /**
     * Erstellt die Ad Entitäten für die Counter der Zusagen/Absagen für eine Ad.
     */

    @NotNull
    @Getter
    @Setter
    private int acceptCounter;

    @NotNull
    @Getter
    @Setter
    private int rejectCounter;

    /**
     * Erstellt die Entität für das Datum an dem die Ad erstellt wurde
     */

    @NotNull
    @Getter
    @Setter
    private String date;

    @OneToMany(mappedBy = "ad", cascade = {CascadeType.REMOVE})
    @Getter
    @NotNull
    private List<Reaction> reactions = new ArrayList<>();

}