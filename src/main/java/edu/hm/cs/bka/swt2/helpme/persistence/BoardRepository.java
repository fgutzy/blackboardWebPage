package edu.hm.cs.bka.swt2.helpme.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.ValidationException;

/**
 * Repository zum Zugriff auf gespeicherte Pinnwände.
 *
 * Repostory-Interfaces erben eine unglaubliche Menge hilfreicher Methoden. Weitere Methoden kann man einfach durch
 * Benennung definieren. Spring Data ergänzt die Implementierungen zur Laufzeit.
 *
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Repository
public interface BoardRepository extends JpaRepository<Board, String> {

    List<Board> findByManager(User manager);

    default Board findByUuidOrThrow(String uuid) {
        return findById(uuid).orElseThrow(() ->
                new ValidationException("Pinwand mit Uuid " + uuid + " existiert nicht."));
    }
}
