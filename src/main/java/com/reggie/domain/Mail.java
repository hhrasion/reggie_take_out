package com.reggie.domain;

import lombok.Data;

@Data
public class Mail {
    private String from;
    private String to;
    private String subject;
    private String text;
    private String code;
}
