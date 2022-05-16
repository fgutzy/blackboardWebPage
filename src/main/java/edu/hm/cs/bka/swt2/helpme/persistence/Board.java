package edu.hm.cs.bka.swt2.helpme.persistence;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Entitätsklasse für Pinnwände.
 */
@Entity
@NoArgsConstructor
public class Board {

    @Id
    @Column(length = 36)
    @NotNull
    @Length(min = 36, max = 36)
    @Getter
    private String uuid;

    @NotNull
    @Column(length = 60)
    @Length(min = 10, max = 60)
    @Getter
    private String title;

    @NotNull
    @Column(length = 150)
    @Length(min = 20, max = 150)
    @Getter
    private String description;

    @ManyToOne
    @NotNull
    @Getter
    private User manager;

    @OneToMany(mappedBy = "board", cascade = {CascadeType.REMOVE})
    @Getter // missing in Lombok: result is mutable!
    private Collection<Subscription> subscriptions = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = {CascadeType.REMOVE})
    @Getter // missing in Lombok: result is mutable!
    private Collection<Ad> ads = new ArrayList<>();

    public Board(String uuid, String title, String description, User manager) {
        this.uuid = uuid;
        this.title = title;
        this.manager = manager;
        this.description = description;
    }

    public boolean isObservedBy(User user) {
        for (Subscription subscription : subscriptions) {
            if (user == subscription.getObserver()) {
                return true;
            }
        }
        return false;
    }
}