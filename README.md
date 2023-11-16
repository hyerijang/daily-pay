![save (1)](https://github.com/hyerijang/daily-pay/assets/46921979/ca3eccf7-d5ca-46b0-9b32-8647b49333a8)

# Daily Pay - 예산 관리 어플리케이션

<br>

<div align="center">
<img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Spring Boot 3.1.5-6DB33F?style=for-the-badge&logo=spring&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Spring Data JPA-gray?style=for-the-badge&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Junit-25A162?style=for-the-badge&logo=JUnit5&logoColor=white"/></a>
</div>
<div align="center">
<img src="https://img.shields.io/badge/MySQL 8-4479A1?style=for-the-badge&logo=MySQL&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Notion-FFFFFF?style=for-the-badge&logo=Notion&logoColor=black"/></a>
<img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=Postman&logoColor=white">
</div>

<br>
<br>

Daily Pay는 사용자들이 개인 재무를 관리하고 지출을 추적하는 데 도움을 주는 애플리케이션입니다.
본 서비스는 유저들의 예산, 지출 내역을 기반으로 **예산 추천**, **오늘 지출 추천**, **오늘 지출 점검** 등의 도움을 제공합니다.

<br>

## 0. 목차

- [1.개발 기간](#1-개발-기간)
- [2.프로젝트 요구사항](#2-프로젝트-요구사항)
- [3.디렉토리 구조](#3-디렉토리-구조)
- [4.설계 및 의도](#4-설계-및-의도)
    - [본 프로젝트를 통해 배우고자 한 것](#본-프로젝트를-통해-배우고자-한-것)
    - [ERD](#erd-자세히)
    - [[A] 유저 기능](#a-유저-기능-자세히)
    - [[B] 예산 설정 및 설계](#b-예산-설정-및-설계-자세히)
    - [[C] 지출 기능 구현](#c-지출-기능-구현-자세히)
    - [[D] 지출 컨설팅 (메인기능)](#d-지출-컨설팅-메인기능-자세히)
    - [[E] 지출 통계](#e-지출-통계-자세히)
- [5.프로젝트 일정 관리](#5-프로젝트-일정-관리)
- [6.API 문서](#6-api-document)
- [7.프로젝트 회고](#7-프로젝트-회고)

## 1. 개발 기간

`v1.0.0` : 23.11.09 ~ 23.11.16 (=8일) (45시간)
## 2. 프로젝트 요구사항

### 유저스토리

- **A. 유저**는 본 서비스의 애플리케이션에 들어와 회원가입을 통해 서비스를 이용합니다.
- **B. 예산 설정 및 설계 서비스**
    - `월별` 총 예산을 설정합니다.
    - 본 서비스는 `카테고리` 별 예산을 설계(=추천)하여 사용자의 과다 지출을 방지합니다.
- **C. 지출 기록**
    - 사용자는 `지출` 을  `금액`, `카테고리` 등을 지정하여 등록 합니다. 언제든지 수정 및 삭제 할 수 있습니다.
- **D. 지출 컨설팅**
    - `월별` 설정한 예산을 기준으로 오늘 소비 가능한 `지출` 을 알려줍니다.
    - 매일 발생한 `지출` 을 `카테고리` 별로 안내받습니다.
- **E. 지출 통계**
    - `지난 달 대비` , `지난 요일 대비`,  `다른 유저 대비` 등 여러 기준 `카테고리 별` 지출 통계를 확인 할 수 있습니다.

## 3. 디렉토리 구조

<details>
    <summary><h4>디렉토리 구조 (자세히)</h4></summary>

#### main

```
main                           
├─generated                      
├─java
│  └─com
│      └─hyerijang
│          └─dailypay  
│              ├─auth  
│              ├─budget
│              │  ├─controller
│              │  ├─domain
│              │  ├─dto
│              │  ├─repository
│              │  └─service
│              ├─common
│              │  ├─aop
│              │  ├─entity
│              │  ├─exception
│              │  │  ├─advice
│              │  │  └─response
│              │  └─logging
│                  ├─contoller
│                  ├─domain
│                  ├─repository
│                  └─service
└─resources
    ├─static
    └─templates
```

#### test

```
test
└─java
    └─com
        └─hyerijang
            └─dailypay
                ├─budget
                │  ├─controller
                │  └─service
                ├─consulting
                │  └─controller
                └─expense
                    └─controller

```

</details>

## 4. 설계 및 의도

본문이 너무 길어져 상세 사항은 접어두었습니다. 본 서비스의 메인기능은 `[D] 지출 컨설팅` 입니다.
<h4>본 프로젝트를 통해 배우고자 한 것</h4>

<img  src = "https://github.com/hyerijang/daily-pay/assets/46921979/d010649a-875d-491d-91fa-b1c2a5fa27a8" width="40%"  />

<details>
    <summary><h4>ERD (자세히)</h4></summary>
<img src="https://github.com/hyerijang/daily-pay/assets/46921979/85d7e8ce-652b-4a69-a53b-d9a14022bde4" width="70%" />
</details>
<details>
    <summary> <h4>[A] 유저 기능 (자세히)</h4></summary>
<h4> 설계의도</h4>

- Spring Security + JWT
- 간단히 구현하라는 요구사항에 맞춰 아래 기능은 생략하였습니다.
    - 아이디, 비밀번호 조건 검증 (e.g. 아이디는 영문과 숫자만, 비밀번호는 10자 이상)
    - 인가
- 추후 소셜 로그인 등으로 전환 될 가능성을 고려하여 [아이디,비밀번호]가 아닌 [이메일,비밀번호]로 테이블을 구성하였습니다.
- Spring Securiy 기능 중 **csrf 보호는 적용하지 않았습니다.**
    - 이유 : rest api로서 non-browser clients와 통신하기 때문
        - [Spring Securiy 공식 문서 - When to use CSRF protection](https://docs.spring.io/spring-security/reference/features/exploits/csrf.html#csrf-when)
          에 따르면 본 서비스는 csrf 보호를 disable해도 되는 서비스입니다.
        - csrf 공격은 일반적으로 사용자의 브라우저를 통해 요청을 위조합니다. 하지만 저희 서비스의 client 들은 웹 브라우저가 아닌 안드로이드/IOS 어플리케이션을
          통해 서비스를 이용하게 될 것입니다.
        - 때문에 매번 api 요청으로부터 csrf 토큰을 받는 것은 **자원 낭비**라고 여겨 csrf를 disable하였습니다.

<h4>회원가입</h4>

- 로그인시에는 Access토큰과 Refresh 토큰이 발급 됩니다.  (타입: Bearer)
- 비밀번호는 암호화 되어 저장됩니다.

<h4>로그인</h4>

- 로그인시에는 Access토큰과 Refresh 토큰이 발급 됩니다.  (타입: Bearer)
- <img src="https://github.com/hyerijang/daily-pay/assets/46921979/54a64d44-256a-4c40-96f8-690640a6d1b4" width="70%" />

</details>

<details>
    <summary><h4>[B] 예산 설정 및 설계 (자세히)</h4></summary>

<h4>[B-1] 예산 엔티티 </h4>

- 예산 기능을 위한 엔티티를 개발합니다.
- {user id, 년월, 카테고리}를 unique한 복합키로 가집니다. (향후 API 구현에 이용할 예정)
- 카테고리는 식비 , 교통 등 일반적인 지출 카테고리를 의미합니다.
- 관리를 용이하게 하기위해 Enum으로 구현하였음

<h4> [B-2] 카테고리 목록 API</h4>

- 유저가 예산설정에 사용할 수 있도록 모든 카테고리 목록을 반환하는 API입니다.

<h4> [B-3] 예산 설정 API </h4>

- 해당 기간 별 설정한 예산 을 설정합니다.
- 예산은 카테고리 를 필수로 지정합니다.
- 사용자는 언제든지 예산 금액을 변경할 수 있습니다.

<h4> [B-3] 예산 설계 API </h4>

- 전체 유저의 예산 액을 통해`카테고리 별  평균 예산 비율`을 계산합니다.
- `카테고리 별  평균 예산 비율`과 `유저의 예산총액`을 기반으로 카테고리별 예산액을 추천합니다.

</details>

<details>
    <summary><h4>[C] 지출 기능 구현 (자세히)</h4></summary>

<h4> [C-1] 지출 엔티티 </h4>

- 요구사항 : 지출 일시, 지출 금액, 카테고리 와 메모 를 입력하여 생성합니다
- 삭제에는 soft delete 적용하였습니다.

<h4> [C-2] 지출 CRUD (API) </h4>

- 지출을 생성, 수정, 읽기(상세), 읽기(목록), 삭제 , 합계제외 할 수 있습니다.
- 생성한 유저만 위 권한을 가집니다.
- 읽기(목록) 은 아래 기능을 가지고 있습니다.
    - 필수적으로 기간 으로 조회 합니다.
    - 조회된 모든 내용의 지출 합계 , 카테고리 별 지출 합계 를 같이 반환합니다.
- 합계제외 처리한 지출은 목록에 포함되지만, 모든 지출 합계에서 제외됩니다.

</details>

<details>
    <summary><h4>[D] 지출 컨설팅 (메인기능) (자세히)</h4></summary> 

<h4> [D-1] 오늘 지출 추천(API) </h4>

해당 API를 통해 유저는 아래 정보를 얻을 수 있습니다.

- 오늘 쓸수 있는 총액 : 목표 달성을 위해 오늘 얼마를 써야하는지 (남은 예산/ 남은날짜)
- 카테고리 별 금액 : 오늘 쓸 수 있는 총액을 카테고리 별로 세분화
- 유저의 상황에 맞는 응원 멘트 (잘 아끼고 있을 때/ 적당히 사용중일때 등.. )

<h5> 유의사항 </h5>
- 지속적인 소비 습관을 생성하기 위한 서비스이므로 예산을 초과하더라도 최소 금액을 기준으로 추천 기능을 제공합니다. (최소 금액은 1만원으로 고정하였습니다.)

<h5> 응답예시 </h5>

```
이번 달 남은 예산 : 14만원
오늘 쓸 수 있는 금액 : 3만원

카테고리 별 금액:
[
- 식료품 : 2만원
- 교통비 : 7000원
- 기타 : 3000원
}

응원멘트 : "이번 달 예산을 넘어섰어요! 열심히 절약해야겠네요"
```

<img src="https://github.com/hyerijang/daily-pay/assets/46921979/0379a405-e654-46d1-9d51-4011dfcfd76f" width="50%" />

<h4> [D-2] 오늘 지출 안내 (API)</h4>

해당 API를 통해 유저는 아래 정보를 얻을 수 있습니다.

- `오늘 지출한 총액` : 오늘 소비한 총액
- `카테고리 별 통계`
    - 일자 기준 오늘 적정 금액 (오늘 기준 사용했으면 적절했을 금액)
    - 일자 기준 오늘 지출 금액 (오늘 기준 사용한 금액)
    - 위험도 : 카테고리별 적정 금액, 지출 금액의 차이를 위험도로 나타냄 (퍼센테이지)
        - ex) 오늘 사용하면 적당한 금액 10,000원/ 사용한 금액 20,000원 이면 200%
        -

<h5> 응답예시 </h5>

```
예산 : 30만원
- 오늘까지 권장 지출  : 14만원
- 이번 달 지출 금액: 16만원
카테고리 별 통계 :
[
    [식비, 9%, 90300원 남음, 9700원 지출, 예산 10만원]
    [생활, 50%, 49150원 남음, 50850원 지출, 예산 10만원]
...
]
```

<img src="https://github.com/hyerijang/daily-pay/assets/46921979/16894d55-f066-4755-9e2f-3b1ec648e231" width="50%" />
</details>


<details>
    <summary><h4>[E] 지출 통계 (자세히)</h4></summary>

사용자에게 통계 데이터를 제공합니다.
<h4> [E-1] 더미데이터 생성 </h4>
통계 API 동작 확인을 위해 Dummy 데이터를 생성하였습니다.

<h5>필요한 더미 데이터 종류</h5>

- 유저의 지난달 소비 데이터
- 유저의 7일전 (지난주, 같은요일) 소비 데이터
- 다른 유저의 오늘 소비 데이터

<h5>코드로 더미데이터 생성 </h5>

- 저희 서비스의 테스트를 위해서는 '오늘' 기준으로 일주일 전, 한 달 전의 데이터가 필요합니다. 따라서 동적 생성을 위해 코드로 더미데이터를 생성하였습니다.
- SQL 프로시저로 직접 SQL문을 작성할 수도 있지만, 스크립트 문 특성상 유지보수가 어려울 것 같아 java 코드로 작성하였습니다.
- 데이터 저장 시 saveall을 써서 단건씩 저장보다 성능을 높였습니다.

<h4> [E-2] 오늘 지출 안내 (API)</h4>
사용자에게 통계 데이터를 제공합니다. 통계 조건은 쿼리 파라미터로 설정합니다.

<h5> API URI</h5>

`Get` /api/v1/statistics?condition={통계 조건}

<h5> {통계 조건}</h5>

- (1)  지난 달 대비 총액 및 카테고리 별 소비율 : last-month
- (2) 지난주 같은 요일 대비 소비율: last-week
- (3) 다른 유저 대비 소비율 : other-user

</details>

## 5. 프로젝트 일정 관리

### [Notion - DAILY PAY -예산관리 어플리케이션 일정 관리 ](https://www.notion.so/hyerijang/DAILY-PAY-f87db3c141604f11a4e1da933f25a86c)
![image](https://github.com/hyerijang/daily-pay/assets/46921979/ececa92a-1389-4cae-8ffe-100e1b5b5f91)

## 6. API Document

[API Document](https://documenter.getpostman.com/view/15143510/2s9YXo1KBA)는 Postman으로 작성하였습니다.

## 7. 프로젝트 회고

[개발 블로그 - 프로젝트 회고 (1)](https://dev-jhl.tistory.com/m/entry/%EC%9B%90%ED%8B%B0%EB%93%9C-%ED%94%84%EB%A6%AC%EC%98%A8%EB%B3%B4%EB%94%A9-%EB%B0%B1%EC%97%94%EB%93%9C-%EC%9D%B8%ED%84%B4%EC%8B%AD10%EC%9B%94-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-3-%ED%9A%8C%EA%B3%A0)
