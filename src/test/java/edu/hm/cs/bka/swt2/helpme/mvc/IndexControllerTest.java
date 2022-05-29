package edu.hm.cs.bka.swt2.helpme.mvc;

import edu.hm.cs.bka.swt2.helpme.service.AdService;
import edu.hm.cs.bka.swt2.helpme.service.BoardService;
import edu.hm.cs.bka.swt2.helpme.service.UserService;
import edu.hm.cs.bka.swt2.helpme.service.dto.UserDisplayDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest
public class IndexControllerTest {

  @MockBean
  UserService userService;

  @MockBean
  AdService AdService;

  @MockBean
  BoardService BoardService;

  @Autowired
  MockMvc mvc;

  @Test
  public void sollteIndexAusliefernOhneBegruessung() throws Exception {
    MvcResult indexPage = mvc.perform(MockMvcRequestBuilders.get("/")).andReturn();

    Assertions.assertFalse(indexPage.getResponse().getContentAsString().contains("Hallo"));
    Assertions.assertTrue(indexPage.getResponse().getContentAsString().contains("Anmelden"));
  }

  @Test
  @WithMockUser(username = "finn", password = "lala", roles = "USER")
  public void sollteIndexAusliefernMitBegruessung() throws Exception{
    UserDisplayDto finn = new UserDisplayDto();
    finn.setLogin("xxxx");
    finn.setName("Finn");
    Mockito.when(userService.getUserInfo("finn")).thenReturn(finn);
    MvcResult indexPage = mvc.perform(MockMvcRequestBuilders.get("/")).andReturn();

    Assertions.assertTrue(indexPage.getResponse().getContentAsString().contains("Abmelden"));
    Assertions.assertTrue(indexPage.getResponse().getContentAsString().contains("Finn"));

  }
}
