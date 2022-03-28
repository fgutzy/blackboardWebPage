package edu.hm.cs.bka.swt2.helpme.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository zum Zugriff auf gespeicherte Reaktionen.
 *
 * Repostory-Interfaces erben eine unglaubliche Menge hilfreicher Methoden. Weitere Methoden kann man einfach durch
 * Benennung definieren. Spring Data ergänzt die Implementierungen zur Laufzeit.
 *
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    Reaction findByAdAndUser(Ad ad, User user);

    List<Reaction> findByUser(User user);

    List<Reaction> findByAd(Ad ad);
}
