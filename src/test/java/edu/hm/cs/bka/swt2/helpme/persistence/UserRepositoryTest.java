package edu.hm.cs.bka.swt2.helpme.persistence;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class UserRepositoryTest {
  @Autowired
  UserRepository userRepository;

  @Test
  public void liefertUserSortiertAus(){
    //Arrange
    userRepository.save(new User("dora", "{noop}12345678", "Dora", true));
    userRepository.save(new User("casemir", "{noop}12345678", "Casemir", false));
    userRepository.save(new User("frederik", "{noop}12345678", "Frederik", true));
    userRepository.save(new User("berta", "{noop}12345678", "Berta", true));
    userRepository.save(new User("emil", "{noop}12345678", "Emil", false));
    userRepository.save(new User("albert", "{noop}12345678", "Albert", false));



    //Act
    List<User> users = userRepository.findAllByOrderByAdministratorDescLoginAsc();

    //Assert
    Assertions.assertEquals(6, users.size());
    Assertions.assertEquals("berta", users.get(0).getLogin());
    Assertions.assertEquals("dora", users.get(1).getLogin());
    Assertions.assertEquals("frederik", users.get(2).getLogin());
    Assertions.assertEquals("albert", users.get(3).getLogin());
    Assertions.assertEquals("casemir", users.get(4).getLogin());
    Assertions.assertEquals("emil", users.get(5).getLogin());





  }
}
