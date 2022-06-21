package edu.hm.cs.bka.swt2.helpme.persistence;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Filter {

  @Id
  @NotNull
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Getter
  private Long id;

  @ElementCollection
  private List<String> categories = new ArrayList<>();

}
