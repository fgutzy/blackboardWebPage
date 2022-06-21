package edu.hm.cs.bka.swt2.helpme.persistence;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import javax.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Entitätsklasse für Gesuche.
 */
@Entity
@NoArgsConstructor // erforderlich für JPA
public class Ad {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    private Long id;

    @NotNull
    @Length(min = 8, max = 50)
    @Column(length = 50)
    @Getter
    @Setter
    private String title;

    @NotNull
    @ManyToOne
    @Getter
    @Setter
    private Board board;

    @NotNull
    @Column(length = 150)
    @Length(min = 20, max = 150)
    @Getter
    @Setter
    private String description;

    /**
     * Erstellt die Ad Entitäten für die Counter der Zusagen/Absagen für eine Ad.
     */



    @PositiveOrZero
    @Getter
    @Setter
    private int acceptCounter;


    @PositiveOrZero
    @Getter
    @Setter
    private int rejectCounter;
/*


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

 */



    //Erstellt die Entität für das Datum an dem die Ad erstellt wurde
    @NotNull
    @Getter
    @Setter
    private LocalDate dateAdCreated;

    //Erstellt die Entität für das Datum bis zu dem die Ad gültig ist
    @NotNull
    @Getter
    @Setter
    private LocalDate dateToDeleteAd;


    @OneToMany(mappedBy = "ad", cascade = {CascadeType.REMOVE})
    @Getter
    @NotNull
    public List<Reaction> reactions = new ArrayList<>();

/*
    @Getter
    @Setter
    private List<String> usersThatAcceptedAd = new ArrayList<>();

    @Getter
    @Setter
    private List<String> usersThatRejetedAd = new ArrayList<>();

 */


}