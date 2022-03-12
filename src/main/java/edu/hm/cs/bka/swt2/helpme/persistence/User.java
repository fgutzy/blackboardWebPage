package edu.hm.cs.bka.swt2.helpme.persistence;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * Modellklasse für die Speicherung von Anwenderdaten. Enthält die Abbildung auf eine
 * Datenbanktabelle in Form von JPA-Annotation.
 *
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
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
    @Column(length = 32)
    @Getter
    private String passwordHash;

    @Column(name = "ADMIN")
    @Getter
    private boolean administrator;

    /**
     * JPA-kompatibler Kostruktor. Wird nur von JPA verwendet und darf private sein.
     */
    public User() {
        // JPA benötigt einen Default-Konstruktor!
    }

    /**
     * Konstruktor zum Initialisieren eines neuen Anwenders.
     *
     * @param login login, mindestens 4 Zeichen lang
     * @param password Passwort
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
        return getLogin() + ( isAdministrator() ? " (admin)" : "");
    }
}
