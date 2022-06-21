package edu.hm.cs.bka.swt2.helpme.persistence;

import java.util.List;
import javax.validation.ValidationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository zum Zugriff auf gespeicherte Kategorien.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

  default Category findByIdOrThrow(Long id) {
    return findById(id).orElseThrow(() ->
        new ValidationException("Kategorie mit der id " + id + " existiert nicht."));
  }

  Category findByName(String name);

  Boolean existsByName(String name);

  List<Category> findAllByOrderByName();

}
