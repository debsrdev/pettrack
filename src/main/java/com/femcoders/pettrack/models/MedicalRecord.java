package com.femcoders.pettrack.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medical_records")
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@Builder
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private double weight;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private MedicalRecordType type;

    @ManyToOne
    @JoinColumn(name = "pet_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Pet pet;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User createdBy;
}
