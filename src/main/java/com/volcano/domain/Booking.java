package com.volcano.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Booking {
    private int id;
    private int userId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}
