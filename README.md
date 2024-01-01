![save (1)](https://github.com/hyerijang/daily-pay/assets/46921979/ca3eccf7-d5ca-46b0-9b32-8647b49333a8)

# Daily Pay - 예산 관리 어플리케이션

<br>

<div align="center">
<img src="https://img.shields.io/badge/Java 17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=Gradle&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Spring Boot 3.1.5-6DB33F?style=for-the-badge&logo=spring&logoColor=white"/></a>
</div>
<div align="center">
<img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white"/></a>
<img src="https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens"/></a>
</div>

<div align="center">
<img src="https://img.shields.io/badge/MySQL 8-4479A1?style=for-the-badge&logo=MySQL&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Spring Data JPA-gray?style=for-the-badge&logoColor=white"/></a>
<img src="https://img.shields.io/badge/QueryDSL-0078D4?style=for-the-badge&logo=Spring Data JPA&logoColor=white"/></a>
</div>

<div align="center">
<img src="https://img.shields.io/badge/swagger-%ffffff.svg?style=for-the-badge&logo=swagger&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Junit-25A162?style=for-the-badge&logo=JUnit5&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Notion-FFFFFF?style=for-the-badge&logo=Notion&logoColor=black"/></a>
<img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"/></a>
</div>

<br>


<br>

Daily Pay는 사용자들이 개인 재무를 관리하고 지출을 추적하는 데 도움을 주는 애플리케이션입니다.
본 서비스는 유저들의 예산, 지출 내역을 기반으로 **예산 추천**, **오늘 지출 추천**, **오늘 지출 점검** 등의 도움을 제공합니다.

<br>

## 목차

- [개발 기간](#개발-기간)
- [프로젝트 요구사항](#프로젝트-요구사항)
- [프로젝트 아키텍처](#프로젝트-아키텍처)
- [설계 및 의도](#설계-및-의도)
    - [[A] 유저 기능](#a-유저-기능)
    - [[B] 예산 설정 및 설계](#b-예산-설정-및-설계)
    - [[C] 지출 기능 구현](#c-지출-기능-구현)
    - [[D] 지출 컨설팅](#d-지출-컨설팅)
    - [[E] 지출 통계](#e-지출-통계)
- [API 문서](#api-문서)
- [프로젝트 일정 관리](#프로젝트-일정-관리)
- [프로젝트 회고](#프로젝트-회고)

## 개발 기간

`v1.0.0` : 23.11.09 ~ 23.11.16 (=8일) (45시간)

`v1.1.0` : 23.11.20 ~ 24.01.01 (=43일) (49시간)

## 프로젝트 요구사항

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

## 프로젝트 아키텍처
![데일리페이 아키텍쳐(GithubAction)](https://github.com/hyerijang/daily-pay/assets/46921979/3e0b2c8b-fcae-4611-85c1-f4d656561616)

<img src="https://github.com/hyerijang/daily-pay/assets/46921979/d4a8925c-a99d-4928-862b-ee248549c0f6" width="80%" />

## 설계 및 의도


### [A] 유저 기능

- Spring Security + JWT
- 추후 소셜 로그인 등으로 전환 될 가능성을 고려하여 [아이디,비밀번호]가 아닌 [이메일,비밀번호]로 테이블을 구성하였습니다.

<h4>회원가입</h4>
- 비밀번호는 BCrypt로 암호화하여 저장됩니다.
- 회원가입 시 Request DTO에서 이메일, 비밀번호 유효성 검증을 진행합니다. (이메일 : 이메일 형식) (비밀번호 : 대/소문자, 숫자, 특수문자를 각각 1개 이상 포함하여 8자 이상)

<h4>로그인</h4>
- 로그인시에는 Access토큰과 Refresh 토큰이 발급 됩니다.  (타입: Bearer)

- <img src="https://github.com/hyerijang/daily-pay/assets/46921979/54a64d44-256a-4c40-96f8-690640a6d1b4" width="60%" />

#### 인증 이후 유저 정보 접근 방식 개선   https://github.com/hyerijang/daily-pay/issues/32

- Spring Security의 UserDetails을 오버라이딩하는  CustomUserDetails 구현
    - 이메일, 비밀번호 외에도 다양한 정보를 Security context에 저장해 두고 사용합니다.
- `@Currentuser`로 Controller 에서 간편하게 유저 정보 접근 가능
- 테스트 시 @WithMockUser 대신 사용할 `@WithMockCurrentUser` 어노테이션 구현 https://github.com/hyerijang/daily-pay/issues/86

### [B] 예산 설정 및 설계

<h4>[B-1] 예산 엔티티 </h4>

- {user id, 년월, 카테고리}를 unique한 복합키로 가집니다. (향후 API 구현에 이용할 예정)
- 카테고리는 식비 , 교통 등 일반적인 지출 카테고리를 의미합니다.
- 관리를 용이하게 하기위해 카테고리는 Enum으로 구현하였습니다.

<h4> [B-2] 카테고리 목록 API</h4>

- 유저가 예산설정에 사용할 수 있도록 모든 카테고리 목록을 반환하는 API입니다.

<h4> [B-3] 예산 설정 API </h4>

- 해당 기간 별 설정한 예산을 설정합니다.
- 예산은 카테고리를 필수로 지정합니다.
- 사용자는 언제든지 예산 금액을 변경할 수 있습니다.

<h4> [B-3] 예산 설계 API </h4>

- 전체 유저의 예산 액을 통해`카테고리 별  평균 예산 비율`을 계산합니다.
- `카테고리 별  평균 예산 비율`과 `유저의 예산총액`을 기반으로 카테고리별 예산액을 추천합니다.


### [C] 지출 기능 구현

<h4> [C-1] 지출 엔티티 </h4>

- 요구사항 : 지출 일시, 지출 금액, 카테고리 와 메모 를 입력하여 생성합니다
- 삭제에는 soft delete 적용하였습니다.

<h4> [C-2] 지출 CRUD (API) </h4>

- 지출을 생성, 수정, 읽기(상세), 읽기(목록), 삭제 , 합계제외 할 수 있습니다.
- 생성한 유저만 위 권한을 가집니다.
- 읽기(목록) 은 아래 기능을 가지고 있습니다.
    - 필수적으로 기간 으로 조회 합니다.
    - 조회된 모든 내용의 지출 합계 , 카테고리 별 지출 합계 를 같이 반환합니다.
    - 목록 조회 시 `Paging`, `동적 정렬`을 적용하여 원하는 지출 내역만 검색할 수 있습니다.
- 합계제외 처리한 지출은 목록에 포함되지만, 모든 지출 합계에서 제외됩니다.

### [D] 지출 컨설팅

<h4> [D-1] 오늘 지출 추천(API) </h4>

해당 API를 통해 유저는 아래 정보를 얻을 수 있습니다.

- 오늘 쓸수 있는 총액 : 목표 달성을 위해 오늘 얼마를 써야하는지 (남은 예산/ 남은날짜)
- 카테고리 별 금액 : 오늘 쓸 수 있는 총액을 카테고리 별로 세분화
- 유저의 상황에 맞는 응원 멘트 (잘 아끼고 있을 때/ 적당히 사용중일때 등.. )

<h5> 유의사항 </h5>
- 지속적인 소비 습관을 생성하기 위한 서비스이므로 예산을 초과하더라도 최소 금액을 기준으로 추천 기능을 제공합니다. (최소 금액은 1만원으로 고정하였습니다.)

<h5> API 응답예시 </h5>

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


<h4> [D-2] 오늘 지출 안내 (API)</h4>

해당 API를 통해 유저는 아래 정보를 얻을 수 있습니다.

- `오늘 지출한 총액` : 오늘 소비한 총액
- `카테고리 별 통계`
    - 일자 기준 오늘 적정 금액 (오늘 기준 사용했으면 적절했을 금액)
    - 일자 기준 오늘 지출 금액 (오늘 기준 사용한 금액)
    - 위험도 : 카테고리별 적정 금액, 지출 금액의 차이를 위험도로 나타냅니다. (퍼센테이지)
        - ex) 문화생활비 카테고리에서, 1일~ 오늘까지 사용한 금액 30,000원/ 한 달 예산 100,000원 이면 30%
        - ex) 문화생활비 카테고리에서, 1일~ 오늘까지 사용한 금액 200,000원/ 한 달 예산 100,000원 이면 200%

<h5>카테고리 별 통계 - 위험도 계산 시 고려사항</h5>

<h6>1안 : 1달 예산 기준으로 현재까지의 소비와 비교하는 경우 (현재 방식)</h6>

<h6>2안 : 1일~ x일까지 사용한 금액 / 1일~x일 사용 가능한 예산 으로 산출하는 경우</h6>

- 유저의 피로도가 가중 될 수 있습니다 (→지속적인 소비 습관 형성에 악영향을 줍니다.)
    - 만약 문화 생활비 예산이 한달에 30만원이고, 유저가 1일에 그 30만원을 다 썼다면, 2안의 경우 유저는 1달 내내 위험도를 초과했다는 알림을 받아야합니다.

```
[2안의 문제점]
1일 -> 30만원 / 1만원 => 위험도 3000%
2일 -> 30만원 / 2만원 => 위험도 1500%
3일 -> 30만원 / 3만원 => 위험도 1000%
...
30일 -> 30만원 /30만원 => 위험도 100%
```

<h5> API 응답예시 </h5>

```
예산 : 30만원
- 오늘까지 권장 지출  : 14만원
- 이번 달 지출 금액: 16만원
카테고리 별 통계 :
[
    [식비, 위험도 9%, 90300원 남음, 9700원 지출, 예산 10만원]
    [생활, 위험도 50%, 49150원 남음, 50850원 지출, 예산 10만원]
...
]
```



### [E] 지출 통계

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
- 데이터 저장 시 saveAll을 써서 단건씩 저장보다 성능을 높였습니다.

<h4> [E-2] 오늘 지출 안내 (API)</h4>
사용자에게 통계 데이터를 제공합니다. 통계 조건은 쿼리 파라미터로 설정합니다.

<h5> API URI</h5>

`Get` /api/v1/statistics?condition={통계 조건}

<h5> {통계 조건}</h5>

- (1)  지난 달 대비 총액 및 카테고리 별 소비율 : last-month
- (2) 지난주 같은 요일 대비 소비율: last-week
- (3) 다른 유저 대비 소비율 : other-user


## API 문서

### Swagger

- URL : `http://server:port/swagger-ui/index.html`
- 인증 API를 제외한 다른 API들은 Acceess Token을 필요로 합니다.

<img src= "https://github.com/hyerijang/daily-pay/assets/46921979/f1d2d79a-577b-4b90-a88c-a53345490a96" width="80%"/>
<img src= "https://github.com/hyerijang/daily-pay/assets/46921979/736025d1-74d4-4779-821a-9c6797352310" width="80%"/>

## 프로젝트 일정 관리

### [Notion - DAILY PAY -예산관리 어플리케이션 일정 관리 ](https://www.notion.so/hyerijang/DAILY-PAY-f87db3c141604f11a4e1da933f25a86c)

![image](https://github.com/hyerijang/daily-pay/assets/46921979/ececa92a-1389-4cae-8ffe-100e1b5b5f91)



## 프로젝트 회고

[DAILY PAY : 예산관리 애플리케이션 v1.0.0 회고](https://dev-jhl.tistory.com/m/entry/%EC%9B%90%ED%8B%B0%EB%93%9C-%ED%94%84%EB%A6%AC%EC%98%A8%EB%B3%B4%EB%94%A9-%EB%B0%B1%EC%97%94%EB%93%9C-%EC%9D%B8%ED%84%B4%EC%8B%AD10%EC%9B%94-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-3-%ED%9A%8C%EA%B3%A0)

[[Spring Security + JWT] Access, Refresh 토큰을 DB에 저장해야하는 이유](https://dev-jhl.tistory.com/entry/Spring-Security-JWT-Access-Refresh-%ED%86%A0%ED%81%B0%EC%9D%84-DB%EC%97%90-%EC%A0%80%EC%9E%A5%ED%95%B4%EC%95%BC%ED%95%98%EB%8A%94-%EC%9D%B4%EC%9C%A0)

[Github Action + Submodule +Docker 로 CICD 하기](https://dev-jhl.tistory.com/entry/Github-Action-Docker-Submodule%EB%A1%9C-CICD-%ED%95%98%EA%B8%B0)

[Stream을 List로 변환하는 방법들의 차이](https://dev-jhl.tistory.com/entry/Stream%EC%9D%84-List%EB%A1%9C-%EB%B3%80%ED%99%98%ED%95%98%EB%8A%94-%EB%B0%A9%EB%B2%95%EB%93%A4%EC%9D%98-%EC%B0%A8%EC%9D%B4)