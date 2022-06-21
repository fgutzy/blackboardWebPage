package edu.hm.cs.bka.swt2.helpme.service;

import edu.hm.cs.bka.swt2.helpme.persistence.*;
import edu.hm.cs.bka.swt2.helpme.service.dto.*;
import java.util.ArrayList;
import java.util.List;
import javax.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service-Klasse für Kategorien.
 */
@Component
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class CategoryService {

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private DtoFactory factory;

  /**
   * Service-Methode zum Erstellen einer Kategorie.
   */
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public Category createCategory(String name) {
    log.info("Kategorie {} wird erstellt.", name);

    //Prüfung zur Einschränkung der Namen-Länge
    if (name.length() < 3 || name.length() > 25) {
      throw new ValidationException("Der Name muss zwischen 3 und 25 Zeichen lang sein!");
    }

    if (categoryRepository.existsByName(name)){
      log.debug("Kategorie {} existiert bereits.", name);
      throw new ValidationException("Kategorie " + name + " existiert bereits.");
    }

    Category category = new Category(name);
    categoryRepository.save(category);
    return category;
  }


  /**
   * Service-Methode zum Abfragen aller Kategorien.
   */
  public List<CategoryDto> getAllCategories() {
    log.info("Alle Kategorien werden angezeigt.");
    List<CategoryDto> result = new ArrayList<>();
    List<Category> categories = categoryRepository.findAllByOrderByName();
    for (Category category : categories) {
      CategoryDto dto = factory.createCategoryDto(category);
      result.add(dto);
    }
    return result;
  }

  /**
   * Service-Methode zum Abfragen einer einzelnen Kategorie.
   */
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public CategoryDto getCategory(Long categoryId) {
    log.info("Kategorie {} wird angezeigt.", categoryId);
    Category category = categoryRepository.findByIdOrThrow(categoryId);
    return factory.createCategoryDto(category);
  }

  /**
   * Service-Methode zum Bearbeiten einer Kategorie.
   */
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public void updateCategory(Long id, CategoryDto categoryDto) {
    log.info("Aktualisiere Kategorie {}.", categoryDto.getName());
    Category category = categoryRepository.findByIdOrThrow(id);

    validateCategoryDto(categoryDto);
    category.setName(categoryDto.getName());
  }

  /**
   * Service-Methode zum Löschen einer Kategorie.
   */
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public void deleteCategory(Long categoryId) {
    log.info("Kategorie {} wird gelöscht.", categoryId);
    final String NEW_CATEGORY_FOR_DELETED_ADS = "Sonstige";
    Category category = categoryRepository.findByIdOrThrow(categoryId);
    List<Ad> ads = category.getAds();
    for (Ad ad : ads) {
      if(categoryRepository.existsByName(NEW_CATEGORY_FOR_DELETED_ADS)) {
        ad.setCategory(categoryRepository.findByName(NEW_CATEGORY_FOR_DELETED_ADS));
      } else {
        Category newCategory = createCategory(NEW_CATEGORY_FOR_DELETED_ADS);
        ad.setCategory(newCategory);
      }
    }
    categoryRepository.delete(category);
  }

  /**
   * Service-Methode zum Validieren des Kategorie-Namens.
   */
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  private void validateCategoryDto(CategoryDto categoryDto) {
    if (categoryDto.getName().length() < 3 || categoryDto.getName().length() > 25) {
      throw new ValidationException("Der Titel muss zwischen 3 und 25 Zeichen lang sein!");
    }
  }

}
