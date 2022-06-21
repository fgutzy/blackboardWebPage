package edu.hm.cs.bka.swt2.helpme.service.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FilterDto {

  private List<String> categories = new ArrayList<>();

}
