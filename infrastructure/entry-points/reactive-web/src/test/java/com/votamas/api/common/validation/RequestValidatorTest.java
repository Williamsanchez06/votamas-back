package com.votamas.api.common.validation;

import com.votamas.api.potentialvoter.dtos.PotentialVoterCreateRequestDTO;
import com.votamas.api.user.dtos.UserCreateRequestDTO;
import com.votamas.api.user.dtos.UserStatusRequestDTO;
import com.votamas.api.user.dtos.UserUpdateRequestDTO;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RequestValidatorTest {
    private final RequestValidator validator = new RequestValidator(
            Validation.buildDefaultValidatorFactory().getValidator());

    @Test
    void shouldRequirePasswordWhenCreatingUser() {
        var request = new UserCreateRequestDTO("Ana", "Pérez", "ana@example.com", "   ");

        StepVerifier.create(validator.validate(request))
                .expectErrorSatisfies(error -> assertInvalidField(error, "password"))
                .verify();
    }

    @Test
    void shouldValidateUserRequiredFieldsAndEmailFormat() {
        var request = new UserCreateRequestDTO(" ", " ", "invalid-email", "password");

        StepVerifier.create(validator.validate(request))
                .expectErrorSatisfies(error -> {
                    var invalid = (InvalidRequestException) error;
                    assertThat(invalid.errors()).extracting(FieldValidationError::field)
                            .contains("name", "surname", "email");
                })
                .verify();
    }

    @Test
    void shouldNormalizeUserWhenUpdating() {
        var request = new UserUpdateRequestDTO(" Ana ", " Pérez ", " ANA@EXAMPLE.COM ");

        StepVerifier.create(validator.validate(request))
                .assertNext(valid -> {
                    assertThat(valid.name()).isEqualTo("Ana");
                    assertThat(valid.email()).isEqualTo("ana@example.com");
                })
                .verifyComplete();
    }

    @Test
    void shouldValidatePotentialVoterRequiredFields() {
        var request = new PotentialVoterCreateRequestDTO(" ", " ", " ", null);

        StepVerifier.create(validator.validate(request))
                .expectErrorSatisfies(error -> {
                    var invalid = (InvalidRequestException) error;
                    assertThat(invalid.errors()).extracting(FieldValidationError::field)
                            .containsExactlyInAnyOrder("identification", "firstName", "lastName",
                                    "votingTableId");
                })
                .verify();
    }

    @Test
    void shouldAcceptValidPotentialVoter() {
        var request = new PotentialVoterCreateRequestDTO(
                " 123 ", " Ana ", " Pérez ", UUID.randomUUID());

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
