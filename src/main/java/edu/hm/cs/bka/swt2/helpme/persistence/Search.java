package edu.hm.cs.bka.swt2.helpme.persistence;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entitätsklasse für Suchanfragen.
 */
@Entity
@NoArgsConstructor
public class Search {

  @Id
  @Getter
  @Setter
  private String text;

}
