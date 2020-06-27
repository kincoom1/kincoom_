package com.kincoom.pay.springboot.service;

import com.kincoom.pay.springboot.advice.RestException;
import com.kincoom.pay.springboot.dto.*;
import com.kincoom.pay.springboot.mapper.PayMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PayService {
    @Autowired
    private PayMapper payMapper;

    private static SecureRandom secureRandom = new SecureRandom();

    //랜덤문자열을 뽑아내기위한 변수
    private static final String lower = "abcdefghijklmnopqrstuvwxyz";
    private static final String upper = lower.toUpperCase();
    private static final String number = "0123456789";


    public SendDto sendPay(int user, String room, int money, int person){
        SendDto sendDto = new SendDto();
        Map<String, Object> map = new HashMap<String,Object>();
        int result = 0;

        //header 정보 set
        map.put("send_user", user);
        map.put("room", room);

        if(money <= 0){
            throw new RestException(HttpStatus.BAD_REQUEST, "입력한 금액이 올바르지 않습니다.");
        }

        if(person <= 0){
            throw new RestException(HttpStatus.BAD_REQUEST, "입력한 인원이 올바르지 않습니다.");
        }
        //뿌릴금액, 인원 set
        map.put("total_money", money);
        map.put("person", person);

        //DB에 동일한 TOKEN 값 있는지 체크
        boolean check = true;
        String token = "";
        while(check){
            //랜덤 문자열 3자리 토큰 생성
            StringBuilder stringBuilder = new StringBuilder(3);
            //영어 대문자, 소문자, 숫자 섞어서 3자리 만들기
            String randomBase = lower + upper + number;
            for(int i=0; i < 3; i++){
                secureRandom.setSeed(System.currentTimeMillis());
                stringBuilder.append(randomBase.charAt(secureRandom.nextInt(randomBase.length())));
            }

            token = stringBuilder.toString();

            try {
                result = payMapper.selectDupToken(token);
            }catch (Exception e){
                throw new RestException(HttpStatus.EXPECTATION_FAILED, "selectDupToken fail");
            }


            //중복값없을시 true로 변경
            if(result == 0){
                check = false;
            }

        }

        map.put("token", token);

        //뿌리기 종합정보 insert
        try{
            result = payMapper.insertSendRequest(map);
        }catch (Exception e){
            throw new RestException(HttpStatus.EXPECTATION_FAILED, "insertSendRequest fail");
        }


        if(result < 1){
            throw new RestException(HttpStatus.EXPECTATION_FAILED, "뿌리기 실패");
        }

        //뿌릴금액 인원수에 맞게 분배
        int seedMoney = money; // 남은금액
        int sendMoney = 0;
        for(int i = 0; i < person; i++){
            map = new HashMap<String,Object>();
            if(i == person-1){
                //마지막인 경우 금액을 그대로 넣는다
                map.put("receive_money", seedMoney);
            }else{
                secureRandom.setSeed(System.currentTimeMillis());
                //남은금액의 최대 절반까지만 허용
                sendMoney = secureRandom.nextInt((int)(seedMoney*0.5));
                map.put("receive_money", sendMoney);
                seedMoney -= sendMoney;
            }
            map.put("token", token);
            try{
                result = payMapper.insertSendDetail(map);
            }catch (Exception e){
                throw new RestException(HttpStatus.EXPECTATION_FAILED, "insertSendRequest fail");
            }

            if(result < 1){
                throw new RestException(HttpStatus.BAD_REQUEST, "뿌리기 분배 실패");
            }
        }

        sendDto.setToken(token);

        return sendDto;
    }


    public ReceiveDto receive(int user, String room, String token){
        ReceiveDto receiveDto = new ReceiveDto();

        if(token.length() < 3){
            throw new RestException(HttpStatus.BAD_REQUEST, "받기 요청값이 올바르지 않습니다.");
        }

        //남아있는 뿌리기건수
        RequestDto requestDto = null;
        List<DetailDto> detailDtoList = null;
        int receiveCount = 0;

        try {
            requestDto = payMapper.selectRequest(token);
            detailDtoList = payMapper.selectDetailList(token);
            receiveCount = payMapper.selectReceiveCount(token,user);
        }catch (Exception e){
            throw new RestException(HttpStatus.EXPECTATION_FAILED, "selectRequest fail");
        }

        if(requestDto == null || detailDtoList.size() < 1){
            throw new RestException(HttpStatus.BAD_REQUEST, "뿌리기 정보가 존재하지 않습니다.");
        }

        if(user == requestDto.getSend_user()){
            throw new RestException(HttpStatus.BAD_REQUEST, "자신은 받을 수 없습니다.");
        }

        if(!room.equals(requestDto.getRoom())){
            throw new RestException(HttpStatus.BAD_REQUEST, "동일한 대화방에 속한 사용자만 받을 수 있습니다.");
        }

        if(receiveCount > 0 ){
            throw new RestException(HttpStatus.BAD_REQUEST, "뿌리기당 1회만 받을 수 있습니다.");
        }


        int result = 0;
        //순서 랜덤으로 뽑기
        int seq;
        secureRandom.setSeed(System.currentTimeMillis());
        seq = secureRandom.nextInt(detailDtoList.size());

        try{
            result = payMapper.updateSendDetail(user, detailDtoList.get(seq).getId());
        }catch (Exception e){
            throw new RestException(HttpStatus.EXPECTATION_FAILED, "updateSendDetail fail");
        }

        if(result < 1){
            throw new RestException(HttpStatus.EXPECTATION_FAILED, "받기 실패");
        }

        receiveDto.setReceiveMoney(detailDtoList.get(seq).getReceive_money());

        return receiveDto;
    }

    public SearchDto search(int user, String room, String token){
        SearchDto searchDto = null;

        //요청받은 token 검사
        if(token.length() < 3){
            throw new RestException(HttpStatus.BAD_REQUEST, "조회 요청값이 올바르지 않습니다.");
        }

        //뿌린시각, 뿌린금액, 받기 완료된 금액, 받기 완료된정보(받은금액, 받은 사용자 아이디 리스트) 리턴
        try{
            searchDto = payMapper.selectSendInfo(token, user);
        }catch(Exception e){
            throw new RestException(HttpStatus.EXPECTATION_FAILED, "selectSendInfo fail");
        }

        if(searchDto == null){
            throw new RestException(HttpStatus.BAD_REQUEST, "조회 실패");
        }

        List<HashMap<String,Object>> receiveList = null;
        try{
            receiveList = payMapper.selectSendDetail(token);
        }catch(Exception e){
            throw new RestException(HttpStatus.EXPECTATION_FAILED, "updateSendDetail fail");
        }


        searchDto.setReceive_list(receiveList);

        return searchDto;
    }
}
