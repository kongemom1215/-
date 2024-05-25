# :bulb: GAMZA-ZOA : 감자 요리 정보 공유 커뮤니티  [![Hits](https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2Fkongemom1215%2Fhit-counter&count_bg=%2379C83D&title_bg=%23555555&icon=&icon_color=%23E7E7E7&title=hits&edge_flat=false)](https://hits.seeyoufarm.com)
> 감자 맛집, 레시피 등 감자 관련 정보 공유 커뮤니티입니다.  
 
URL : <https://www.gamza-zoa.com> 

 
## 개발기간
*2024.02.29 ~ 2024.05.19* 기획, 설계, 구현

*2024.05.20* 상용 배포일

*2024.05.20 ~ 현재* 유지/보수

## 개발환경
Gradle/Java 17/Spring 3.2.3

##  Stacks
**:bookmark_tabs: Environment**

IntelliJ, Git, Github, AWS

**:bookmark_tabs: Frontend**

HTML5, javascript ES6, CSS3, Jquery, Thymeleaf, BootStrap

**:bookmark_tabs: Backend**

Spring, Spring Security, Java, JPA, REST API, QueryDSL

**:bookmark_tabs: Database**

Redis, MySQL, Ehcache, AWS S3

## 기능 소개

### **1. 인트로**
![image](https://github.com/kongemom1215/-/assets/72897088/3d0dab06-6db1-4ec4-bcc7-15c3306750fb)
Three.js를 이용하여 감자 3D 그래픽 추가

### **2. 메인 페이지**
![image](https://github.com/kongemom1215/-/assets/72897088/cb67410e-6dec-401b-a37b-6c05543943b8)
카드 소개
* 맛집 네비게이션 : kakao map API를 활용하여 맛집 좌표 표시
* 감자조아 인기글 : 인기글로 등록된 게시글 표시
* 요즘 뜨는 감자요리는? : 유튜브 쇼츠 iframe 삽입
* 맛도리 감자 레시피 : 감자레시피 게시판으로 이동
* AI가 알려주는 레시피 : OPEN AI API를 연동하여 레시피를 질문 기능 제공
* 월드컵 : 월드컵 게시판으로 이동 및 로그인 시 월드컵 1,2,3,4 등 표시 (chart.js)
* 한줄 등록하기 : 한줄 등록하기 기능 제공. 5분마다 사용자가 등록한 글중 랜덤하게 선택하여 표시(스프링 부트 스케줄러)

### **3. 로그인**
![image](https://github.com/kongemom1215/-/assets/72897088/c935c69b-1090-49a1-9bbe-4f7ed5e3fdf4)

* 로그인/자동로그인 : 스프링 시큐리티 설정(rememberMe)
* 비밀번호 찾기 : 이메일로 임시 발급 비밀번호 전송

### **4. 회원가입**
![image](https://github.com/kongemom1215/-/assets/72897088/b1ddc7b8-6d36-4745-9b4c-91f6c61299b8)

① 이메일로 인증코드 전송하여 인증 (redis로 인증횟수 체크)

![image](https://github.com/kongemom1215/-/assets/72897088/d5108973-dbee-4c91-ac1a-fa6b9ccf4658)

② 생년월일, 닉네임(중복확인 체크-redis), 비밀번호, 이용약관 동의 입력 받음

form validation - jquery validate plugin 이용

비밀번호 저장 : bcrypt

![image](https://github.com/kongemom1215/-/assets/72897088/2266f14e-13ad-4161-b13e-cc2f5787e511)

③ 가입 성공 페이지 : 암호화된 회원 ID 값으로 N번째 회원인치 체크(AES128)

### **4. 알림**
![image](https://github.com/kongemom1215/-/assets/72897088/c50b9b7c-f656-4ea6-82b8-8f68f2602392)

①  알림 처리 설계
ApplicationEventPublisher와 스프링 @Async 기능을 사용해서 비동기 이벤트로 알림 처리

②  알림수 표시
핸들러 인터셉터 등록. 핸들러 처리 이후, 뷰 랜더링 전 스프링 웹 MVC HandlerInterceptor로 알림수 넘김

## **5. 프로필**
![image](https://github.com/kongemom1215/-/assets/72897088/a7872f93-7ed1-484f-9379-326be45c299e)
①  프로필 탭
프로필 이미지, 소개글 설정

![image](https://github.com/kongemom1215/Potato-Community-Project/assets/72897088/fc762cea-b02c-475f-8e17-014c8721b4a4)
②  계정관리 탭
비밀번호, 닉네임 변경

![image](https://github.com/kongemom1215/-/assets/72897088/ac86e891-772a-4611-8394-e9ab961130b6)
③  커뮤니티 활동
작성한글, 댓글단글, 좋아요한 글 표시

## **7. 게시판**
![image](https://github.com/kongemom1215/-/assets/72897088/d077b255-0649-40b6-abcb-af6a5d2a6bbf)
①  게시글 목록
최신순, 인기순 정렬
스프링 Pageable을 통해 페이징 처리

![image](https://github.com/kongemom1215/-/assets/72897088/36d1ecbb-03be-4d41-bbe5-9eee3c091468)
②  게시글 상세
좋아요, 댓글달기, 대댓글 달기 기능
댓글 새로고침 기능 

![image](https://github.com/kongemom1215/-/assets/72897088/81d1bbb7-1bad-4dd5-9387-d17add65d743)
③ 글쓰기
Toast UI Editor 적용
이미지 등록시 AWS S3 에 적재

## **8. 맛집게시판**
![image](https://github.com/kongemom1215/-/assets/72897088/a13a654f-2c63-4f99-8ad4-0b862962e12f)
①  글쓰기
지도 검색을 통해 맛집 검색후 맛집 등록 기능

![image](https://github.com/kongemom1215/-/assets/72897088/685e8b1a-f928-4755-a8a3-e1a67a398b96)
②  글 상세
등록한 맛집 표시 및 클릭시 장소 상세 이동

## **9. 월드컵 **
![image](https://github.com/kongemom1215/-/assets/72897088/2a9cc037-9608-4e34-904c-99cc2fc3f6e0)
![image](https://github.com/kongemom1215/-/assets/72897088/92319fcd-68e8-4511-9b8b-77dade31edbd)
![image](https://github.com/kongemom1215/-/assets/72897088/65d78c16-90e5-4639-936e-8695ec54c66a)

월드컵 구현 

## **10. 인기글 모음**
![image](https://github.com/kongemom1215/-/assets/72897088/1247a206-5cf8-45cb-9313-f88d4d843ac2)

* 일간 : 자정에 스케쥴러가 인기도 측정하여 인기글 20개 선정
* 주간 : 일요일 자정마다 스케쥴러 가 인기도 측정하여 인기글 20개 선정
* 월간 : 1일 자정마다 스케쥴러가 인기도 측정하여 인기글 20개 선정


## **11. 검색**
![image](https://github.com/kongemom1215/-/assets/72897088/82b8e997-f044-4cd3-a2ae-34c33131c33c)

전체, 게시판별 검색 기능 제공
