package com.volcano.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class Confirmation {
    private User user;
    private Booking booking;
    private List<ApiValidationError> errors;
    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }
}

