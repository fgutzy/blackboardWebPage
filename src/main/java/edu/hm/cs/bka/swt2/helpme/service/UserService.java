package edu.hm.cs.bka.swt2.helpme.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.hm.cs.bka.swt2.helpme.persistence.User;
import edu.hm.cs.bka.swt2.helpme.persistence.UserRepository;
import edu.hm.cs.bka.swt2.helpme.service.dto.DtoFactory;
import edu.hm.cs.bka.swt2.helpme.service.dto.UserDisplayDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;

import static edu.hm.cs.bka.swt2.helpme.common.SecurityHelper.*;

/**
 * Service-Klasse zur Verwaltung von Anwendern. Wird auch genutzt, um Logins zu validieren.
 * Servicemethoden sind transaktional und rollen alle Änderungen zurück, wenn eine Exception
 * auftritt. Service-Methoden sollten
 * <ul>
 * <li>keine Modell-Objekte herausreichen, um Veränderungen des Modells außerhalb des
 * transaktionalen Kontextes zu verhindern - Schnittstellenobjekte sind die DTOs (Data Transfer
 * Objects).
 * <li>die Berechtigungen überprüfen, d.h. sich nicht darauf verlassen, dass die Zugriffen über die
 * Webcontroller zulässig sind.</li>
 * </ul>
 *
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Component
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DtoFactory dtoFactory;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByIdOrThrow(username);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        if (user.isAdministrator()) {
            authorities.add(ADMIN_AUTHORITY);
        }
        return new org.springframework.security.core.userdetails.User(user.getLogin(),
                user.getPasswordHash(), authorities);
    }

    /**
     * Service-Methode Abfragen aller Anwender.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<UserDisplayDto> getAllUsers() {
        List<UserDisplayDto> result = new ArrayList<>();
        for (User anwender : userRepository.findAll()) {
            result.add(dtoFactory.createDto(anwender));
        }
        return result;
    }

    /**
     * Service-Methode zum Abfragen aller Administrator:innen.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<UserDisplayDto> findAdmins() {
        List<UserDisplayDto> result = new ArrayList<>();
        for (User anwender : userRepository.findByAdministrator(true)) {
            result.add(dtoFactory.createDto(anwender));
        }
        return result;
    }

    /**
     * Service-Methode zum Abfragen eigener Daten.
     */
    @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
    public UserDisplayDto getUserInfo(String login) {
        log.debug("Lese Daten für Anwender {}.", login);
        User user = userRepository.findByIdOrThrow(login);
        return dtoFactory.createDto(user);
    }

    /**
     * Service-Methode zum Erstellen eines Anwenders/einer Anwenderin.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void createUser(String login, String password, boolean isAdministrator) {
        log.debug("Erstelle Anwender:in {}.", login);
        User anwender = new User(login, password, isAdministrator);
        userRepository.save(anwender);
    }
}
