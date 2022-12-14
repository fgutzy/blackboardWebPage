package edu.hm.cs.bka.swt2.helpme;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Konfigurationsklasse, vor allem für Spring Security. In Konfigurationsklassen können bestimmte
 * Aspekte der Anwendung konfiguriert werden, im konkreten Fall nur die Sicherheitseinstellungen.
 * Zum Beispiel aktivieren wir hier die Nutzung von Java-Annotationen zur Autorisierungsprüfung und
 * konfigurieren, welche URLs ohne Autorisierung aufgerufen werden können, damit zum Beispiel jeder
 * die Hauptseite sehen kann.
 *
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class HelpMeConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    @SuppressWarnings("java:S4502")
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // unbeschränkter Zugriff auf Ressourcen und Login/Logout
                .antMatchers("/assets/**", "/", "/login**", "/logout**").permitAll()
                // Administratorzugriff auf Datenbank
                .antMatchers("/h2-console/**").hasRole("ADMIN")
                // Authentifizierung Voraussetzung für alles andere
                .anyRequest().fullyAuthenticated()
                // Login-Handling
                .and().formLogin().and().logout().logoutSuccessUrl("/").permitAll();

        // Deaktivierung von Sicherheitsmerkmalen nur im Praktikum sinnvoll!
        http.csrf().ignoringAntMatchers("/h2-console/**", "/login");
        http.headers().frameOptions().disable();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}