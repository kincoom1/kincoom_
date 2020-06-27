package com.kincoom.pay.springboot.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RequestDto {
    private LocalDateTime create_date;
    private int send_user;
    private String room;
}
