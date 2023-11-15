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
- [5.프로젝트 일정 관리](#5-프로젝트-일정-관리)
- [6.동작예시](#6-동작예시)
- [7.API 문서](#7-api-document)



## 1. 개발 기간

`v1.0.0` : 23.11.09 ~ 23.11.16 (=8일)

## 2. 프로젝트 요구사항



### 유저스토리

- **A. 유저**는 본 사이트에 들어와 회원가입을 통해 서비스를 이용합니다.
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

본문이 너무 길어져 상세 사항은 접어두었습니다. 

<details>
    <summary><h4>ERD (자세히)</h4></summary>
<img src="https://github.com/hyerijang/daily-pay/assets/46921979/597a3ed5-cda6-4624-a363-6f1fcfab2fc7" width="70%" />
</details>
<details>
    <summary> <h4> 유저 기능 (자세히) </h4></summary>
<h4> 설계의도</h4>
    
- Spring Security + JWT
- 간단히 구현하라는 요구사항에 맞춰 아래 기능은 생략하였습니다. 
    - 아이디, 비밀번호 조건 검증 (e.g. 아이디는 영문과 숫자만, 비밀번호는 10자 이상)
    - 인가
- 추후 소셜 로그인 등으로 전환 될 가능성을 고려하여 [아이디,비밀번호]가 아닌 [이메일,비밀번호]로 테이블을 구성하였습니다. 
- Spring Securiy 기능 중 **csrf 보호는 적용하지 않았습니다.**
   - 이유 : rest api로서 non-browser clients와 통신하기 때문
      - [Spring Securiy 공식 문서 - When to use CSRF protection](https://docs.spring.io/spring-security/reference/features/exploits/csrf.html#csrf-when)에 따르면 본 서비스는 csrf 보호를 disable해도 되는 서비스입니다.
      - csrf 공격은 일반적으로 사용자의 브라우저를 통해 요청을 위조합니다. 하지만 저희 서비스의 client 들은 웹 브라우저가 아닌 안드로이드/IOS 어플리케이션을 통해 서비스를 이용하게 될 것입니다.   
      -  때문에  매번 api 요청으로부터 csrf 토큰을 받는 것은 **자원 낭비**라고 여겨 csrf를 disable하였습니다. 

<h4>회원가입</h4>

- 로그인시에는  Access토큰과 Refresh 토큰이 발급 됩니다.  (타입: Bearer)
- 비밀번호는 암호화 되어 저장됩니다.


<h4>로그인</h4>

- 로그인시에는  Access토큰과 Refresh 토큰이 발급 됩니다.  (타입: Bearer)
- <img src="https://github.com/hyerijang/daily-pay/assets/46921979/54a64d44-256a-4c40-96f8-690640a6d1b4" width="70%" />

</details>

## 5. 프로젝트 일정 관리

### [Notion - DAILY PAY -예산관리 어플리케이션 일정 관리 ](https://www.notion.so/hyerijang/DAILY-PAY-f87db3c141604f11a4e1da933f25a86c)

![image](https://github.com/hyerijang/daily-pay/assets/46921979/00e6e5d6-41a0-45d1-90bb-d5f8920a51b2)


## 6. 동작예시


## 7. API Document

