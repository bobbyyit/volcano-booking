package com.volcano.domain.builder;

import com.volcano.domain.ApiValidationError;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class ApiErrorBuilderTest {

    @Test
    void validatesNonNullString() {
        String nullString = null;
        List<ApiValidationError> result = new ApiErrorBuilder().notNull("a-field", nullString).build();

        assertThat(result, contains(new ApiValidationError("a-field", "This field cannot be empty.")));
    }

    @Test
    void validatesNonNullInteger() {
        Integer nullString = null;
        List<ApiValidationError> result = new ApiErrorBuilder().notNull("a-field", nullString).build();

        assertThat(result, contains(new ApiValidationError("a-field", "This field cannot be empty.")));
    }

    @Test
    void validatesDate() {
        List<ApiValidationError> result = new ApiErrorBuilder().isDateValid("date-field", "bad-date").build();

        assertThat(result, contains(new ApiValidationError("date-field", "This date cannot be parsed. Please use date format: YYYY-MM-DD")));
    }

    @Test
    void validateDateIsNotToday() {
        List<ApiValidationError> result = new ApiErrorBuilder().isNotToday("date-field", LocalDate.now().toString()).build();

        assertThat(result, contains(new ApiValidationError("date-field", "The booking date cannot be today.")));
    }

    @Test
    void validateIsNotToday() {
        List<ApiValidationError> result = new ApiErrorBuilder().isNotToday("date-field", LocalDate.now().plusDays(1).toString()).build();

        assertThat(result, is(empty()));
    }

    @Test
    void validatesDoesNotExceedDays() {
        List<ApiValidationError> result = new ApiErrorBuilder().doesNotExceedDays("date-field", 3, LocalDate.now().toString(), LocalDate.now().plusDays(2).toString()).build();

        assertThat(result, is(empty()));
    }

    @Test
    void validatesExceedDays() {
        List<ApiValidationError> result = new ApiErrorBuilder().doesNotExceedDays("date-field", 3, LocalDate.now().toString(), LocalDate.now().plusDays(3).toString()).build();

        assertThat(result, contains(new ApiValidationError("date-field", "The booking duration cannot be longer than 3 days.")));
    }

    @Test
    void validatesIsNotAfterDays() {
        List<ApiValidationError> result = new ApiErrorBuilder().isNotAfterDays("date-field", 10, LocalDate.now().plusDays(15).toString()).build();

        assertThat(result, contains(new ApiValidationError("date-field", "The booking date cannot be after 10 days.")));
    }
}