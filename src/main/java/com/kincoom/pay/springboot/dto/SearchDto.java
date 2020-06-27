package com.kincoom.pay.springboot.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class SearchDto {
    private String create_date;
    private int total_money;
    private int receive_complete_money;
    private List<HashMap<String, Object>> receive_list;
}
