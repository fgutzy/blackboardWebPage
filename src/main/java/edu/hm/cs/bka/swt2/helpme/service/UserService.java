package edu.hm.cs.bka.swt2.helpme.service;


import edu.hm.cs.bka.swt2.helpme.service.dto.UserDisplayDto;

import java.util.List;

public interface UserService {

    List<UserDisplayDto> getAllUsers();

    List<UserDisplayDto> findAdmins();

    UserDisplayDto getUserInfo(String login);

    void legeAn(String login, String password, boolean isAdmin);

}

