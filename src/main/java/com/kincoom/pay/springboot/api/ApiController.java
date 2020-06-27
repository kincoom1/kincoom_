package com.kincoom.pay.springboot.api;

import com.kincoom.pay.springboot.dto.ReceiveDto;
import com.kincoom.pay.springboot.dto.SearchDto;
import com.kincoom.pay.springboot.dto.SendDto;
import com.kincoom.pay.springboot.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ApiController {

    @Autowired
    PayService payService;

    //뿌리기(token return)
    @PostMapping("/api/sendPay")
    public SendDto sendPay(@RequestHeader("X-USER-ID") int user, @RequestHeader("X-ROOM-ID") String room, @RequestParam("money") int money, @RequestParam("person") int person ){

        SendDto sendDto = payService.sendPay(user, room, money, person);

        return sendDto;
        
    }

    //받기(받은 금액 return)
    @PutMapping("/api/receivePay")
    public ReceiveDto receivePay(@RequestHeader("X-USER-ID") int user, @RequestHeader("X-ROOM-ID") String room, @RequestParam("token") String token){

        ReceiveDto receiveDto = payService.receive(user, room, token);

        return receiveDto;
    }
    
    @GetMapping("/api/search")
    public SearchDto sendList(@RequestHeader("X-USER-ID") int user, @RequestHeader("X-ROOM-ID") String room, @RequestParam("token") String token){
        


        SearchDto searchDto = payService.search(user, room, token);
        
        return searchDto;
    }
}
