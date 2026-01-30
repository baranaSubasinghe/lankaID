package com.lankaid.portal.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity // This tells Java: "Map this class to a Database Table"
@Data   // This (Lombok) automatically writes getters and setters for us
public class Citizen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nic;
    private String gender;
    private String requestType;
    private String status = "Pending";
    private int birthYear;
    private String email;

    // We will save the full message too, just for history
    private String verificationStatus;
}