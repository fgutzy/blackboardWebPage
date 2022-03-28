package edu.hm.cs.bka.swt2.helpme.persistence;

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

    @OneToMany(mappedBy = "ad")
    @Getter
    @NotNull
    private List<Reaction> reactions = new ArrayList<>();
}
