package com.votamas.api.validation;

import com.votamas.api.potentialvoter.dtos.PotentialVoterRequestDTO;
import com.votamas.api.user.dtos.UserRequestDTO;
import com.votamas.api.user.dtos.UserStatusRequestDTO;
import jakarta.validation.Validation;
import jakarta.validation.groups.Default;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RequestValidatorTest {
    private final RequestValidator validator = new RequestValidator(
            Validation.buildDefaultValidatorFactory().getValidator());

    @Test
    void shouldRequirePasswordWhenCreatingUser() {
        var request = new UserRequestDTO("Ana", "Pérez", "ana@example.com", "   ");

        StepVerifier.create(validator.validate(request, Default.class, OnCreate.class))
                .expectErrorSatisfies(error -> assertInvalidField(error, "password"))
                .verify();
    }

    @Test
    void shouldValidateUserRequiredFieldsAndEmailFormat() {
        var request = new UserRequestDTO(" ", " ", "invalid-email", "password");

        StepVerifier.create(validator.validate(request, Default.class, OnCreate.class))
                .expectErrorSatisfies(error -> {
                    var invalid = (InvalidRequestException) error;
                    assertThat(invalid.errors()).extracting(FieldValidationError::field)
                            .containsExactlyInAnyOrder("name", "surname", "email");
                })
                .verify();
    }

    @Test
    void shouldNotRequirePasswordWhenUpdatingUser() {
        var request = new UserRequestDTO(" Ana ", " Pérez ", " ANA@EXAMPLE.COM ", null);

        StepVerifier.create(validator.validate(request))
                .assertNext(valid -> {
                    assertThat(valid.name()).isEqualTo("Ana");
                    assertThat(valid.email()).isEqualTo("ana@example.com");
                })
                .verifyComplete();
    }

    @Test
    void shouldValidatePotentialVoterRequiredFields() {
        var request = new PotentialVoterRequestDTO(" ", " ", " ", null, null);

        StepVerifier.create(validator.validate(request))
                .expectErrorSatisfies(error -> {
                    var invalid = (InvalidRequestException) error;
                    assertThat(invalid.errors()).extracting(FieldValidationError::field)
                            .containsExactlyInAnyOrder("identification", "firstName", "lastName",
                                    "votingTableId", "assignedLeaderId");
                })
                .verify();
    }

    @Test
    void shouldAcceptValidPotentialVoter() {
        var request = new PotentialVoterRequestDTO(" 123 ", " Ana ", " Pérez ",
                UUID.randomUUID(), UUID.randomUUID());

        StepVerifier.create(validator.validate(request))
                .assertNext(valid -> assertThat(valid.identification()).isEqualTo("123"))
                .verifyComplete();
    }

    @Test
    void shouldRequireUserStatus() {
        StepVerifier.create(validator.validate(new UserStatusRequestDTO(null)))
                .expectErrorSatisfies(error -> assertInvalidField(error, "active"))
                .verify();
    }

    private void assertInvalidField(Throwable error, String field) {
        assertThat(error).isInstanceOf(InvalidRequestException.class);
        assertThat(((InvalidRequestException) error).errors())
                .extracting(FieldValidationError::field)
                .contains(field);
    }
}
