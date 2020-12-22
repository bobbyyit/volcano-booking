package com.volcano.domain;

import lombok.Data;

@Data
public class SearchParameters {
    private String from;
    private String to;
    private Integer[] ids;
    private Integer id;
    private Integer userId;
    private String email;
    private String firstName;
    private String lastName;
}
