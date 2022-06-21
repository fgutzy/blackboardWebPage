package edu.hm.cs.bka.swt2.helpme.persistence;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Entity
@NoArgsConstructor
public class Category {

  @Id
  @NotNull
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Getter
  private Long id;

  @NotNull
  @Column(length = 25)
  @Length(min = 3, max = 25)
  @Getter
  @Setter
  private String name;

  @OneToMany(mappedBy = "category", cascade = {CascadeType.DETACH})
  @Getter
  private List<Ad> ads = new ArrayList<>();

  @ManyToMany
  @Getter
  @Setter
  private List<Filter> filters = new ArrayList<>();

  public Category(String name) {
    super();
    this.name = name;
  }

}
