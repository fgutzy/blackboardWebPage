package edu.hm.cs.bka.swt2.helpme.persistence;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Entitätsklasse für Reaktionen.
 */
@Entity
@NoArgsConstructor
public class Reaction {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    private Long id;

    @Getter
    @Setter
    boolean hidden;

    @Getter
    @Setter
    String comment;

    /**
     * Erstellt die Reaction Entitäten für die Frage "Wurde eine Ad bereits akzeptiert/abgelehnt?"
     */
    @Getter
    @Setter
    public boolean zugesagenMoeglich = true;

    @Getter
    @Setter
    public boolean absagenMoeglich = true;

    @Getter
    @Setter
    private boolean allowedToClick = true;

    @Getter
    @Setter
    private boolean acceptedMessage;

    @Getter
    @Setter
    private boolean rejectedMessage;

    @Getter
    @Setter
    private boolean recallAcceptanceMessage;

    @Getter
    @Setter
    private boolean recallRejectedMessage;

    @Getter
    @Setter
    private boolean warningMessage;

    public void acceptedMsg(){
        setAcceptedMessage(true);
        setRecallAcceptanceMessage(false);
        setRecallRejectedMessage(false);
    }

    public void recallAcceptedMsg(){
        setRecallAcceptanceMessage(true);
        setAcceptedMessage(false);
        setWarningMessage(false);
    }

    public void rejectedMsg(){
        setRejectedMessage(true);
        setRecallRejectedMessage(false);
        setRecallAcceptanceMessage(false);
    }

    public void recallRejectedMsg(){
        setRecallRejectedMessage(true);
        setRejectedMessage(false);
        setWarningMessage(false);
    }

    public void warningMsg(){
        setWarningMessage(true);
        setRejectedMessage(false);
        setAcceptedMessage(false);
    }


    @ManyToOne
    @Getter
    User user;

    @ManyToOne
    @Getter
    Ad ad;

    public Reaction(User user, Ad ad) {
        this.ad = ad;
        this.user = user;
    }


}
