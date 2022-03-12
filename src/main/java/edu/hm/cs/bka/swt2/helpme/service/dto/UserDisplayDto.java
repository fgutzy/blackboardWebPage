package edu.hm.cs.bka.swt2.helpme.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Transferobjekt für einfache Anzeigeinformationen von Anwendern. Transferobjekte sind
 * Schnittstellenobjekte der Geschäftslogik; Sie sind nicht Teil des Modells, sodass Änderungen an
 * den Transferobjekten die Überprüfungen der Geschäftslogik nicht umgehen können.
 *
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@NoArgsConstructor
public class UserDisplayDto {

    @Getter
    @Setter
    private String login = "";

    public UserDisplayDto(String login) {
        this.login = login;
    }

    public String getDisplayName() {
        return login;
    }
}

