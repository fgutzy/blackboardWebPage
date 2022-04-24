package edu.hm.cs.bka.swt2.helpme.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.ValidationException;
import java.util.List;

/**
 * Repository zum Zugriff auf gespeicherte Gesuche.
 *
 * Repostory-Interfaces erben eine unglaubliche Menge hilfreicher Methoden. Weitere Methoden kann man einfach durch
 * Benennung definieren. Spring Data ergänzt die Implementierungen zur Laufzeit.
 *
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Repository
public interface AdRepository extends JpaRepository<Ad, Long> {

    List<Ad> getByBoard(Board board);

    default Ad findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() ->
                new ValidationException("Gesuch mit id " + id + " existiert nicht."));
    }

    List<Ad> getByBoardOrderByIdDesc(Board board);
}
