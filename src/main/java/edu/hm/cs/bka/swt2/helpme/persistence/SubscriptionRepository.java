package edu.hm.cs.bka.swt2.helpme.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository zum Zugriff auf gespeicherte Beobachtungen.
 *
 * Repostory-Interfaces erben eine unglaubliche Menge hilfreicher Methoden. Weitere Methoden kann man einfach durch
 * Benennung definieren. Spring Data ergänzt die Implementierungen zur Laufzeit.
 *
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Subscription findByBoardAndObserver(Board board, User observer);

    List<Subscription> findByObserver(User observer);

}
