package edu.hm.cs.bka.swt2.helpme.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import edu.hm.cs.bka.swt2.helpme.persistence.User;
import edu.hm.cs.bka.swt2.helpme.persistence.UserRepository;
import edu.hm.cs.bka.swt2.helpme.service.dto.UserDisplayDto;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static edu.hm.cs.bka.swt2.helpme.common.SecurityHelper.ADMIN_ROLES;
import static edu.hm.cs.bka.swt2.helpme.common.SecurityHelper.STANDARD_ROLES;

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
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserDetailsService, UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper mapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userLookup = userRepository.findById(username);
        if (userLookup.isPresent()) {
            User user = userLookup.get();
            return new org.springframework.security.core.userdetails.User(user.getLogin(),
                    user.getPasswordHash(),
                    user.isAdministrator() ? ADMIN_ROLES : STANDARD_ROLES);
        } else {
            throw new UsernameNotFoundException("Anwender konnte nicht gefunden werden.");
        }
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<UserDisplayDto> getAllUsers() {
        List<UserDisplayDto> result = new ArrayList<>();
        for (User anwender : userRepository.findAll()) {
            result.add(mapper.map(anwender, UserDisplayDto.class));
        }
        return result;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<UserDisplayDto> findAdmins() {
        // Das Mapping auf DTOs geht eleganter, ist dann aber schwer verständlich.
        List<UserDisplayDto> result = new ArrayList<>();
        for (User anwender : userRepository.findByAdministrator(true)) {
            result.add(mapper.map(anwender, UserDisplayDto.class));
        }
        return result;
    }

    @Override
    @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
    public UserDisplayDto getUserInfo(String login) {
        LOG.debug("Lese Daten für Anwender {}.", login);
        User anwender = userRepository.getOne(login);
        return new UserDisplayDto(anwender.getLogin());
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void legeAn(String login, String password, boolean isAdministrator) {
        LOG.debug("Erstelle Anwender {}.", login);
        User anwender = new User(login, password, isAdministrator);
        userRepository.save(anwender);
    }
}
