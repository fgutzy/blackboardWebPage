package edu.hm.cs.bka.swt2.helpme.common;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Hilfsklasse für Security-Aspekte.
 *
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
public final class SecurityHelper {

    private SecurityHelper() {}

    /** Administrator-Rolle und -authority*/
    public static final String ADMIN_ROLE = "ROLE_ADMIN";
    public static final GrantedAuthority ADMIN_AUTHORITY = new SimpleGrantedAuthority(ADMIN_ROLE);

    /**
     * Ändert die aktive Authentifizierung auf einen anonymen Administrator, um z.B. die
     * Initialisierung des Datenmodells zuzulassen. Nicht möglich, wenn es eine aktive
     * Authentifizierung gibt.
     */
    public static void escalate() {
        SecurityContext sc = SecurityContextHolder.getContext();
        if (sc.getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authReq =
                    new UsernamePasswordAuthenticationToken("", "", AuthorityUtils.createAuthorityList(ADMIN_ROLE));
            sc.setAuthentication(authReq);
        }
    }

    /**
     * Ermittelt, ob die aktive Authentifizierung Administrator-Rechte hat.
     */
    public static boolean isAdmin(Authentication auth) {
        if (auth == null) {
            return false;
        }
        return auth.getAuthorities().contains(ADMIN_AUTHORITY);
    }


}
