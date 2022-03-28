package edu.hm.cs.bka.swt2.helpme.persistence;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Modellklasse für die Speicherung von Anwenderdaten. Enthält die Abbildung auf eine
 * Datenbanktabelle in Form von JPA-Annotation.
 *
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class User {

    @Id
    @NotNull
    @Length(min = 4, max = 32)
    @Column(length = 32)
    @Getter
    @EqualsAndHashCode.Include
    private String login;

    @NotNull
    @Length(min = 7, max = 32) // lenght includes "{noop}"
    @Column(length = 32, name = "HASH")
    @Getter
    private String passwordHash;

    @Column(name = "ADMIN")
    @Getter
    private boolean administrator;

    @OneToMany(mappedBy = "observer")
    private Collection<Subscription> subscriptions = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private Collection<Reaction> reactions = new ArrayList<>();

    /**
     * Konstruktor zum Initialisieren eines neuen Anwenders.
     *
     * @param login         login, mindestens 4 Zeichen lang
     * @param password      Passwort
     * @param administrator Flag (true für Administratorrechte)
     */
    public User(String login, String password, boolean administrator) {
        super();
        this.login = login;
        this.passwordHash = "{noop}" + password;
        this.administrator = administrator;
    }

    @Override
    public String toString() {
        return getLogin() + (isAdministrator() ? " (admin)" : "");
    }
}
