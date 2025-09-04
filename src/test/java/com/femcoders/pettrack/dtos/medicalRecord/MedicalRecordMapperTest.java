package com.femcoders.pettrack.dtos.medicalRecord;

import com.femcoders.pettrack.models.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class MedicalRecordMapperTest {

    private final MedicalRecordMapperImpl mapper = new MedicalRecordMapperImpl();

    @Test
    void entityToDto_ok() {
        var pet = Pet.builder().id(7L).build();
        var vet = User.builder().id(100L).username("VetUser").role(Role.VETERINARY).build();

        var entity = MedicalRecord.builder()
                .id(3L)
                .date(LocalDate.parse("2024-05-10"))
                .type(MedicalRecordType.VACCINATION)
                .description("Rabia")
                .pet(pet)
                .createdBy(vet)
                .build();

        var dto = mapper.entityToDto(entity);

        assertThat(dto.id()).isEqualTo(3L);
        assertThat(dto.date()).isEqualTo(LocalDate.parse("2024-05-10"));
        assertThat(dto.type()).isEqualTo(MedicalRecordType.VACCINATION);
        assertThat(dto.description()).isEqualTo("Rabia");
    }

    @Test
    void dtoToEntity_ok() {
        var req = new MedicalRecordRequest(
                "Revisión anual",
                12.5,
                LocalDate.parse("2024-01-20"),
                MedicalRecordType.REVISION,
                7L
        );
        var pet = Pet.builder().id(7L).build();
        var vet = User.builder().id(100L).username("VetUser").role(Role.VETERINARY).build();


        var entity = mapper.dtoToEntity(req, pet, vet);

        assertThat(entity.getDate()).isEqualTo(LocalDate.parse("2024-01-20"));
        assertThat(entity.getType()).isEqualTo(MedicalRecordType.REVISION);
        assertThat(entity.getDescription()).isEqualTo("Revisión anual");
        assertThat(entity.getPet().getId()).isEqualTo(7L);
    }
}
