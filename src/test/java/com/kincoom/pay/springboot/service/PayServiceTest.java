package com.kincoom.pay.springboot.service;

import com.kincoom.pay.springboot.advice.RestException;
import com.kincoom.pay.springboot.dto.DetailDto;
import com.kincoom.pay.springboot.dto.RequestDto;
import com.kincoom.pay.springboot.mapper.PayMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PayServiceTest {

    @Autowired
    private PayMapper payMapper;

    @Test
    public void sendPay() throws Exception{
        int user = 100;
        String room = "ROOM1";
        int money = 100000;
        int person = 10;
        String token = "abc";

        int success = 1;

        Map<String, Object> map = new HashMap<String,Object>();
        map.put("send_user", user);
        map.put("room", room);
        map.put("total_money", money);
        map.put("person", person);
        map.put("token", token);

        int result = payMapper.insertSendRequest(map);

        assertThat(result).isEqualTo(success);

        int seedMoney = money; // 남은금액
        int sendMoney = 0;

        int i;

        for(i = 0; i < person; i++){
            map = new HashMap<String,Object>();
            if(i == person-1){

                map.put("receive_money", seedMoney);
            }else{
                //마지막이 아닌경우 random, 최대 50퍼센트까지만 배분되게
                sendMoney = (int)seedMoney/person;
                map.put("receive_money", sendMoney);
                seedMoney -= sendMoney;
            }
            map.put("token", token);

            result = payMapper.insertSendDetail(map);

        }

        assertThat(i).isEqualTo(person);

        List<DetailDto> detailDtoList = payMapper.selectDetailList(token);

        assertThat(detailDtoList.get(0).getReceive_money()).isBetween(0, money);


    }
}