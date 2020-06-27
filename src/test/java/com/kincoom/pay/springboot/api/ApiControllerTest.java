package com.kincoom.pay.springboot.api;

import com.kincoom.pay.springboot.mapper.PayMapper;
import com.kincoom.pay.springboot.service.PayService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.AutoConfigureMybatis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ApiController.class)
@AutoConfigureMybatis
public class ApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PayService payService;

    @Autowired
    private PayMapper payMapper;

    @Test
    public void sendPay() throws Exception{
        mvc.perform(post("/api/sendPay")
        .header("X-USER-ID", "123")
        .header("X-ROOM-ID", "abc")
        .param("money", "50000")
        .param("person", "3"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void receivePay() throws Exception{
        mvc.perform(put("/api/receivePay")
                .header("X-USER-ID", "123")
                .header("X-ROOM-ID", "abc")
                .param("token", "aaa"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void sendList() throws Exception{
        mvc.perform(get("/api/search")
                .header("X-USER-ID", "123")
                .header("X-ROOM-ID", "abc")
                .param("token", "aaa"))
                .andExpect(status().isOk())
                .andDo(print());
    }
}