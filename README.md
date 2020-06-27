# 머니 뿌리기 API

## 환경
- Java 1.8
- Springboot 2.1.9
- lombok
- mybatis
- h2 database

## 문제해결
- 뿌리기
    - 토큰발급
        - 영문 대,소문자, 숫자를 조합한 문자열에서 3가지 문자를 난수 발생시켜 토큰생성
        - SecureRandom 클래스를 이용하여 난수 발생시키고 시드를 System.currentTimeMillis()를 이용하여 불규칙하게 나오도록 함
        - INSERT 전 같은 토큰이 있는지 체크 후 INSERT
    - 데이터 저장
        - SEND_REQUEST(뿌리기 요청), SEND_DETAIL(인원별 뿌릴금액) 2개의 테이블을 구성
        - 미리 인원수 만큼 나눠서 뿌릴금액을 난수로 결정

- 받기
    - 뿌리기 요청(SEND_REQUEST) 테이블 조회
        - 결과없을 경우(10분경과, 뿌릴대상없음 DB 조회시 WHERE절에서 처리) Exception
        - 뿌리기 요청자 ID와 받기 요청자 ID 같을 경우 Exception
        - 대화방 ID가 다를 경우 Exception
    - 인원별 뿌릴금액(SEND_DETAIL) 테이블 조회
        - 자신이 받은내역 있는지 여부 체크 후 있으면 Exception

- 조회
    - DB 조회시 7일 이내건만 조회되도록 처리

##보완할 점
-단위테스트 코드 작성 부족
-Mybatis 대신 JPA 적용했을 경우 비즈니스 로직에 좀 더 집중 가능?
-응답코드 세분화
-금액 배분, 토큰생성 등에서 SecureRandom 말고 다른 방법으로 구현
