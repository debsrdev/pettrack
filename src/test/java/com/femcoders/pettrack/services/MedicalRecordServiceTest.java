package com.femcoders.pettrack.services;

import com.femcoders.pettrack.dtos.medicalRecord.MedicalRecordMapper;
import com.femcoders.pettrack.dtos.medicalRecord.MedicalRecordRequest;
import com.femcoders.pettrack.dtos.medicalRecord.MedicalRecordResponse;
import com.femcoders.pettrack.exceptions.EntityNotFoundException;
import com.femcoders.pettrack.models.*;
import com.femcoders.pettrack.repositories.MedicalRecordRepository;
import com.femcoders.pettrack.repositories.PetRepository;
import com.femcoders.pettrack.repositories.UserRepository;
import com.femcoders.pettrack.security.UserDetail;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayName("MedicalRecordService Unit Tests")
public class MedicalRecordServiceTest {

    @Mock
    MedicalRecordRepository recordRepository;

    @Mock
    PetRepository petRepository;

    @Mock
    MedicalRecordMapper mapper;

    @Mock
    UserRepository userRepository;

    MedicalRecordServiceImpl service;

    private Pet pet;
    private User vet;
    private UserDetail vetPrincipal;
    private UserDetail userPrincipal;

    private MedicalRecordRequest createReq;
    private MedicalRecord entitySaved;
    private MedicalRecordResponse resp;

    @BeforeEach
    void setup() {
        pet = Pet.builder().id(7L).name("Luna").build();
        vet = User.builder().id(100L).username("Vet").role(Role.VETERINARY).build();
        vetPrincipal = new UserDetail(vet);
        userPrincipal = new UserDetail(User.builder().id(2L).username("User").role(Role.USER).build());

        createReq = new MedicalRecordRequest(
                "Vacuna anual", 12.0,
                LocalDate.parse("2024-01-20"),
                MedicalRecordType.VACCINATION,
                7L
        );

        entitySaved = MedicalRecord.builder()
                .id(30L)
                .description("Vacuna anual")
                .weight(12.0)
                .date(LocalDate.parse("2024-01-20"))
                .type(MedicalRecordType.VACCINATION)
                .pet(pet)
                .createdBy(vet)
                .build();

        resp = new MedicalRecordResponse(
                30L,
                "Vacuna anual",
                12.0,
                LocalDate.parse("2024-01-20"),
                MedicalRecordType.VACCINATION,
                "Luna",
                "Vet"
        );
        service = new MedicalRecordServiceImpl(mapper, recordRepository, petRepository, userRepository);
    }

    @Nested @DisplayName("createMedicalRecord()")
    class Create {
        @Test @DisplayName("Should create record when principal is vet and pet exists")
        void shouldCreate_whenVetAndPetExists() {
            given(petRepository.findById(7L)).willReturn(Optional.of(pet));
            given(userRepository.findById(100L)).willReturn(Optional.of(vet));
            given(mapper.dtoToEntity(createReq, pet, vet)).willReturn(entitySaved);
            given(recordRepository.save(entitySaved)).willReturn(entitySaved);
            given(mapper.entityToDto(entitySaved)).willReturn(resp);

            var result = service.createMedicalRecord(createReq, vetPrincipal);

            assertThat(result.id()).isEqualTo(30L);
            verify(petRepository).findById(7L);
            verify(recordRepository).save(entitySaved);
            verify(mapper).entityToDto(entitySaved);
        }

        @Test @DisplayName("Should throw SecurityException when principal is not vet")
        void shouldThrowForbidden_whenNotVet() {
            var thrown = catchThrowable(() -> service.createMedicalRecord(createReq, userPrincipal));

            assertThat(thrown)
                    .isInstanceOf(SecurityException.class)
                    .hasMessageContaining("Only veterinaries");
        }

        @Test @DisplayName("Should throw 404 when pet does not exist")
        void shouldThrowNotFound_whenPetMissing() {
            given(petRepository.findById(7L)).willReturn(Optional.empty());

            var thrown = catchThrowable(() -> service.createMedicalRecord(createReq, vetPrincipal));

            assertThat(thrown)
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Pet not found with id 7");

            verify(petRepository).findById(7L);
        }
    }

    @Nested @DisplayName("updateMedicalRecord()")
    class Update {
        @Test @DisplayName("Should update when id exists and vet")
        void shouldUpdate_whenIdExistsAndVet() {
            var req = new MedicalRecordRequest("Revision", 12.5, LocalDate.parse("2024-02-01"), MedicalRecordType.REVISION, 7L);
            given(recordRepository.findById(30L)).willReturn(Optional.of(entitySaved));
            given(petRepository.findById(7L)).willReturn(Optional.of(pet));
            given(recordRepository.save(entitySaved)).willReturn(entitySaved);
            given(mapper.entityToDto(entitySaved))
                    .willReturn(new MedicalRecordResponse(30L, "Rev", 12.5, LocalDate.parse("2024-02-01"), MedicalRecordType.REVISION, "Luna", "Vet"));

            var result = service.updateMedicalRecord(30L, req, vetPrincipal);

            assertThat(result.description()).isEqualTo("Rev");
            verify(recordRepository).findById(30L);
            verify(recordRepository).save(entitySaved);
        }

        @Test @DisplayName("Should throw 404 when id does not exist")
        void shouldThrowNotFound_whenIdMissing() {
            var req = new MedicalRecordRequest("Revision", 12.5, LocalDate.parse("2024-02-01"), MedicalRecordType.REVISION, 7L);
            given(recordRepository.findById(999L)).willReturn(Optional.empty());

            var thrown = catchThrowable(() -> service.updateMedicalRecord(999L, req, vetPrincipal));

            assertThat(thrown)
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("MedicalRecord not found with id 999");

            verify(recordRepository).findById(999L);
        }
    }

    @Nested @DisplayName("deleteMedicalRecord()")
    class Delete {
        @Test @DisplayName("Should delete when id exists and vet")
        void shouldDelete_whenIdExistsAndVet() {
            given(recordRepository.findById(30L)).willReturn(Optional.of(entitySaved));

            var res = service.deleteMedicalRecord(30L, vetPrincipal);

            assertThat(res.get("message")).contains("has been deleted");
            verify(recordRepository).findById(30L);
            verify(recordRepository).delete(entitySaved);
        }

        @Test @DisplayName("Should throw 404 when id does not exist")
        void shouldThrowNotFound_whenIdMissing() {
            given(recordRepository.findById(999L)).willReturn(Optional.empty());

            var thrown = catchThrowable(() -> service.deleteMedicalRecord(999L, vetPrincipal));

            assertThat(thrown)
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("MedicalRecord not found with id 999");

            verify(recordRepository).findById(999L);
        }
    }

    @Nested @DisplayName("Read Medical Records")
    class Read {

        @Test @DisplayName("Should return all records for VETERINARY (admin)")
        void shouldReturnAll_whenVeterinary() {
            var recordList = List.of(entitySaved);
            given(recordRepository.findAll()).willReturn(recordList);
            given(mapper.entityToDto(entitySaved)).willReturn(resp);

            var result = service.getAllMedicalRecords(vetPrincipal);

            assertThat(result).hasSize(1);
            verify(recordRepository).findAll();
        }

        @Test @DisplayName("Should return only own records for USER")
        void shouldReturnOwn_whenUser() {
            var recordList = List.of(entitySaved);
            given(recordRepository.findByPet_User_Id(2L)).willReturn(recordList);
            given(mapper.entityToDto(entitySaved)).willReturn(resp);

            var result = service.getAllMedicalRecords(userPrincipal);

            assertThat(result).hasSize(1);
            verify(recordRepository).findByPet_User_Id(2L);
        }

        @Test @DisplayName("Should return record by id if VETERINARY")
        void shouldReturnById_whenVeterinary() {
            given(recordRepository.findById(30L)).willReturn(Optional.of(entitySaved));
            given(mapper.entityToDto(entitySaved)).willReturn(resp);

            var result = service.getMedicalRecordById(30L, vetPrincipal);

            assertThat(result.id()).isEqualTo(30L);
            verify(recordRepository).findById(30L);
        }

        @Test @DisplayName("Should return record by id if USER is owner")
        void shouldReturnById_whenUserIsOwner() {
            pet.setUser(userPrincipal.getUser());
            given(recordRepository.findById(30L)).willReturn(Optional.of(entitySaved));
            given(mapper.entityToDto(entitySaved)).willReturn(resp);

            var result = service.getMedicalRecordById(30L, userPrincipal);

            assertThat(result.id()).isEqualTo(30L);
        }

        @Test @DisplayName("Should throw SecurityException if USER is not owner")
        void shouldThrowForbidden_whenUserNotOwner() {
            pet.setUser(User.builder().id(999L).build());
            given(recordRepository.findById(30L)).willReturn(Optional.of(entitySaved));

            var thrown = catchThrowable(() -> service.getMedicalRecordById(30L, userPrincipal));

            assertThat(thrown).isInstanceOf(SecurityException.class);
        }

        @Test @DisplayName("Should return records by pet name if VETERINARY")
        void shouldReturnByPetName_whenVet() {
            var recordList = List.of(entitySaved);
            given(recordRepository.findByPetNameIgnoreCase("Luna")).willReturn(recordList);
            given(mapper.entityToDto(entitySaved)).willReturn(resp);

            var result = service.getMedicalRecordsByPetName("Luna", vetPrincipal);

            assertThat(result).hasSize(1);
        }

        @Test @DisplayName("Should return records by pet name if USER is owner")
        void shouldReturnByPetName_whenUserIsOwner() {
            pet.setUser(userPrincipal.getUser());
            var recordList = List.of(entitySaved);
            given(recordRepository.findByPetNameIgnoreCase("Luna")).willReturn(recordList);
            given(mapper.entityToDto(entitySaved)).willReturn(resp);

            var result = service.getMedicalRecordsByPetName("Luna", userPrincipal);

            assertThat(result).hasSize(1);
        }

        @Test @DisplayName("Should throw SecurityException if USER is not owner of pet name")
        void shouldThrowForbidden_whenUserNotOwnerByName() {
            pet.setUser(User.builder().id(99L).build());
            given(recordRepository.findByPetNameIgnoreCase("Luna")).willReturn(List.of(entitySaved));

            var thrown = catchThrowable(() -> service.getMedicalRecordsByPetName("Luna", userPrincipal));

            assertThat(thrown).isInstanceOf(SecurityException.class);
        }

        @Test @DisplayName("Should throw 404 if pet name not found")
        void shouldThrowNotFound_whenPetNameMissing() {
            given(recordRepository.findByPetNameIgnoreCase("Luna")).willReturn(List.of());

            var thrown = catchThrowable(() -> service.getMedicalRecordsByPetName("Luna", vetPrincipal));

            assertThat(thrown).isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Pet not found with name 'Luna'");
        }
    }

}
