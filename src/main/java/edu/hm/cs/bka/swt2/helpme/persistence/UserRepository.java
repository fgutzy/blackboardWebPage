package edu.hm.cs.bka.swt2.helpme.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.ValidationException;
import java.util.List;

/**
 * Repository zum Zugriff auf gespeicherte Anwenderdaten.
 *
 * Repostory-Interfaces erben eine unglaubliche Menge hilfreicher Methoden. Weitere Methoden kann man einfach durch
 * Benennung definieren. Spring Data ergänzt die Implementierungen zur Laufzeit.
 *
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Ermittelt alle Anwender:innen mit oder ohne administrativen Rechten.
     *
     * @param isAdministrator Flag zum Filtern
     * @return Liste der Anwender:innen
     */
    List<User> findByAdministrator(boolean isAdministrator);

    default User findByIdOrThrow(String login) {
        return findById(login).orElseThrow(() ->
                new ValidationException("User mit Login " + login + " existiert nicht."));
    }


    List<User> findAllByOrderByAdministratorDescLoginAsc();
}
