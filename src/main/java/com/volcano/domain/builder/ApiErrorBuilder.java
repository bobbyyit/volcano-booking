package com.volcano.domain.builder;

import com.volcano.domain.ApiValidationError;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ApiErrorBuilder {
    List<ApiValidationError> errors;

    public ApiErrorBuilder() {
        errors = new ArrayList<>();
    }

    public List<ApiValidationError> build() {
        return errors;
    }

    public ApiErrorBuilder notNull(String field, String o) {
        if (StringUtils.isBlank(o)) {
            errors.add(new ApiValidationError(field, "This field cannot be empty."));
        }
        return this;
    }

    public ApiErrorBuilder notNull(String field, Integer o) {
        if (o == null) {
            errors.add(new ApiValidationError(field, "This field cannot be empty."));
        }
        return this;
    }

    public ApiErrorBuilder isDateValid(String field, String date) {
        if (!isDateValid(date)) {
            errors.add(new ApiValidationError(field, "This date cannot be parsed. Please use date format: YYYY-MM-DD"));
        }
        return this;
    }

    private boolean isDateValid(String date) {
        try {
            LocalDate.parse(date);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public ApiErrorBuilder notEmpty(String field, Object[] array) {
        if (array == null || array.length == 0) {
            errors.add(new ApiValidationError(field, "This array field cannot be empty."));
        }
        return this;
    }

    public ApiErrorBuilder isNotToday(String field, String date) {
        if (!StringUtils.isBlank(date) && isDateValid(date)) {
            LocalDate parsedDate = LocalDate.parse(date);
            if (LocalDate.now().equals(parsedDate)) {
                errors.add(new ApiValidationError(field, "The booking date cannot be today."));
            }
        }
        return this;
    }

    public ApiErrorBuilder doesNotExceedDays(String field, int days, String from, String to) {
        if (!StringUtils.isBlank(from) && !StringUtils.isBlank(to) && isDateValid(from) && isDateValid(to)) {
            if (ChronoUnit.DAYS.between(LocalDate.parse(from), LocalDate.parse(to)) + 1 > days) {
                errors.add(new ApiValidationError(field, String.format("The booking duration cannot be longer than %d days.", days)));
            }
        }
        return this;
    }

    public ApiErrorBuilder isNotAfterDays(String field, int days, String from) {
        if (isDateValid(from)) {
            LocalDate date = LocalDate.parse(from);
            if (!date.isBefore(LocalDate.now().plusDays(days))) {
                errors.add(new ApiValidationError(field, String.format("The booking date cannot be after %d days.", days)));
            }
        }
        return this;
    }
}
