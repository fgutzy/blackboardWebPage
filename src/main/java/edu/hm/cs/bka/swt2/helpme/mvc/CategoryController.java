package edu.hm.cs.bka.swt2.helpme.mvc;

import edu.hm.cs.bka.swt2.helpme.service.CategoryService;
import edu.hm.cs.bka.swt2.helpme.service.dto.CategoryDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@Slf4j
public class CategoryController extends AbstractController{

  @Autowired
  private CategoryService categoryService;

  /**
   * Erzeugt eine Listenansicht mit allen Kategorien.
   */
  @GetMapping("/categories")
  public String getCategories(Model model, Authentication auth) {
    model.addAttribute("categories", categoryService.getAllCategories());
    return "category-listview";
  }

  /**
   * Erzeugt eine Formularansicht für das Erstellen einer Kategorie.
   */
  @GetMapping("/categories/create")
  public String getCategorieCreationView(Model model) {
    model.addAttribute("newCategory", new CategoryDto());
    return "category-creation";
  }

  /**
   * Nimmt den Formularinhalt vom Formular zum Erstellen einer Kategorie entgegen und legt eine
   * entsprechende Kategorie an. Kommt es dabei zu einer Exception, wird das Erzeugungsformular
   * wieder angezeigt und eine Fehlermeldung eingeblendet. Andernfalls wird auf die Listenansicht
   * der Kategorien weitergeleitet und das Anlegen in einer Einblendung bestätigt.
   */
  @PostMapping("/categories")
  public String handleCategoryCreation(Model model,
                                       @ModelAttribute("newCategory") CategoryDto category,
                                       RedirectAttributes redirectAttributes) {
    try {
      categoryService.createCategory(category.getName());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/categories/create";
    }
    redirectAttributes.addFlashAttribute("success",
        "Kategorie " + category.getName() + " erstellt.");
    return "redirect:/categories";
  }

  /**
   * Verarbeitet das Bearbeiten einer Kategorie.
   */
  @PostMapping("/categories/{id}")
  public String handleCategoryUpdate(Model model, Authentication auth, @PathVariable("id") Long id,
                                     @ModelAttribute("category") CategoryDto category, RedirectAttributes redirectAttributes) {

    try {
      categoryService.updateCategory(id, category);
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      log.info("Fehler beim Ändern einer Kategorie.", e);
      return "/categories/{id}/edit";
    }
    redirectAttributes.addFlashAttribute("success", "Die Kategorie mit dem Titel " + category.getName() + " wurde gespeichert.");
    return "redirect:/categories";
  }


  /**
   * Verarbeitet das Bearbeiten einer Kategorie.
   */
  @GetMapping("/categories/{id}/edit")
  public String handleCategoryEdit(Model model, Authentication auth, @PathVariable("id") Long categoryId) {
    model.addAttribute("editCategory", categoryService.getCategory(categoryId));
    return "category-edit";
  }


  /**
   * Verarbeitet das Löschen einer Kategorie.
   */
  @GetMapping("/categories/{id}/delete")
  public String handleCategoryDeletion(Authentication auth, @PathVariable("id") Long categoryId) {
    CategoryDto category = categoryService.getCategory(categoryId);
    categoryService.deleteCategory(categoryId);
    return "redirect:/categories";
  }

}
