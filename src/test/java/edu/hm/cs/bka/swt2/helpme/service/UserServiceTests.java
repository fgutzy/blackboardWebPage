package edu.hm.cs.bka.swt2.helpme.service;

import edu.hm.cs.bka.swt2.helpme.persistence.User;
import edu.hm.cs.bka.swt2.helpme.persistence.UserRepository;
import javax.validation.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

  @Mock
  UserRepository userRepo;

  @InjectMocks
  UserService sut = new UserService();

  @Test
  public void sollteUserAnlegen() {
    //Arrange
    Mockito.when(userRepo.existsById("tiffy")).thenReturn(false);

    //Act
    sut.createUser("tiffy", "tiffy123", "Tiffy Dingens", false);

    //Assert
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    Mockito.verify(userRepo).save(userCaptor.capture());
    Mockito.verifyNoMoreInteractions(userRepo);
    Assertions.assertEquals("tiffy", userCaptor.getValue().getLogin());
    Assertions.assertEquals("{noop}tiffy123", userCaptor.getValue().getPasswordHash());
  }

  @Test
  public void sollteFehlschlagenBeiExistierendemUser() {
    Mockito.when(userRepo.existsById("tiffy")).thenReturn(true);

    Assertions.assertThrows(ValidationException.class, () -> {
      sut.createUser("tiffy", "tiffy123", "Tiffy Dingens", false);
    });
  }

  @Test
  public void sollteFehlschlagenWennLoginZuKurzOderZuLang() {
    Assertions.assertThrows(ValidationException.class, () -> {
      sut.createUser("tif", "tiffy123", "Tiffy Dingens", false);
    });
  }

  @Test
  public void sollteFehlschlagenWennPasswortZuKurzOderZuLang() {
    Assertions.assertThrows(ValidationException.class, () -> {
      sut.createUser("tiffy", "tif12", "Tiffy Dingens", false);
    });
  }

  @Test
  public void sollteFehlschlagenWennLoginGrossbuchstabenEnthaelt() {
    Assertions.assertThrows(ValidationException.class, () -> {
      sut.createUser("Tiffy", "tiffy123", "Tiffy Dingens", false);
    });
  }

  @Test
  public void sollteFehlschlagenWennPasswortLeerzeichenEnthaelt() {
    Assertions.assertThrows(ValidationException.class, () -> {
      sut.createUser("tiffy", "tiffy 123", "Tiffy Dingens", false);
    });
  }
}
