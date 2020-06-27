package com.kincoom.pay.springboot.mapper;


import com.kincoom.pay.springboot.dto.DetailDto;
import com.kincoom.pay.springboot.dto.RequestDto;
import com.kincoom.pay.springboot.dto.SearchDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface PayMapper {

    /* 뿌리기 */
    //동일한 토큰값 있는지 체크
    int selectDupToken(String token) throws Exception;

    //뿌리기 정보 insert
    int insertSendRequest(Map<String,Object> map) throws Exception;

    //인원별 뿌릴 금액 insert
    int insertSendDetail(Map<String,Object> map) throws Exception;


    /*받기*/
    //뿌리기정보 가져오기
    RequestDto selectRequest(String token) throws Exception;
    //뿌리기 상세정보 가져오기
    List<DetailDto> selectDetailList(String token) throws Exception;
    //사용자가 받은 내역 있는지 확인
    int selectReceiveCount(String token, int user) throws Exception;
    //받기 처리
    int updateSendDetail(int user, Long id) throws Exception;

    /*조회*/
    //뿌리기 정보
    SearchDto selectSendInfo(String token, int user) throws Exception;
    List<HashMap<String,Object>> selectSendDetail(String token) throws Exception;
}
