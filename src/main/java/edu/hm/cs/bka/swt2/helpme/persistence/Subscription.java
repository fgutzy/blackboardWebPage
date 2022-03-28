package edu.hm.cs.bka.swt2.helpme.persistence;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Entitätsklasse für Beobachtungen, also Verhältnisse zwischen Pinwänden und beobachtenden Anwender:innen.
 */
@Entity
@NoArgsConstructor
public class Subscription {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @Getter
    private Board board;

    @ManyToOne
    @Getter
    private User observer;

    public Subscription(Board board, User observer) {
        this.board = board;
        this.observer = observer;
    }
}
