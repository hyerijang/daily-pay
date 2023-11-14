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
- [4.ERD](#4-erd)
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
    <summary>자세히</summary>

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

## 4. ERD

<details>
    <summary>자세히</summary>
  
<img src="https://github.com/hyerijang/daily-pay/assets/46921979/597a3ed5-cda6-4624-a363-6f1fcfab2fc7" width="70%" />


</details>

## 5. 프로젝트 일정 관리

### [Notion - DAILY PAY -예산관리 어플리케이션 일정 관리 ](https://www.notion.so/hyerijang/DAILY-PAY-f87db3c141604f11a4e1da933f25a86c)

![image](https://github.com/hyerijang/daily-pay/assets/46921979/00e6e5d6-41a0-45d1-90bb-d5f8920a51b2)


## 6. 동작예시


## 7. API Document

