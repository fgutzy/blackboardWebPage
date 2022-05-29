package edu.hm.cs.bka.swt2.helpme.it;

import edu.hm.cs.bka.swt2.helpme.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserCreationIT {

  @Autowired
  UserService userService;

  @Test
  public void createdUserContainsAllInformation() {
      userService.createUser("tiffy", "tiffy1234", "Tiffy", false);

      var createdUser = userService.getUserInfo("tiffy");

      assertEquals("tiffy", createdUser.getLogin());
      assertEquals("Tiffy", createdUser.getDisplayName());
  }
}
