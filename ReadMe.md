# BuyHood 

<br>

# 목차

- [프로젝트 소개](#프로젝트-소개)
- [팀원 소개 및 역할](#팀원-소개-및-역할)
- [주요 기능](#주요-기능)
- [ERD](#erd)
- [API 명세서](#api-명세서)
- [인프라 아키텍처 & 기술 스택](#인프라-아키텍처--기술-스택)
- [성과 및 회고](#성과-및-회고)

<br>

## 프로젝트 소개
### 📅 프로젝트 기간: 2025/05/26 ~ 2025/06/15

> 📌 **지역 기반 맞춤 상품을 빠르고 편리하게 구매하는 이커머스 서비스**

- 지역 기반 추천으로 내 주변에서 판매 중인 다양한 상품을 한눈에 확인할 수 있습니다. 

- 거주하는 동네의 상점과 소상공인과 직접 연결해 지역 경제 활성화를 돕는 것이 목표입니다.

- 지역 화폐 결제 시스템을 도입해 지역 내 소비를 촉진하고, 빠른 배송으로 편리한 쇼핑 경험을 제공합니다.


<br>

## 팀원 소개 및 역할


| 김기홍  🔗 [Github](https://github.com/KimKiHong-1111) | 문성준    🔗 [Github](https://github.com/sjMun09) | 박용준 🔗[ Github](https://github.com/dereck-jun)           | 서지원   🔗 [Github](https://github.com/jiwonclvl)      |
|-----------------------------------------------------|------------------------------------------------|----------------------------------------------------------|------------|
| **JWT 기반 인증/인가**<br>**회원 기능** | **CI/CD 배포**<br> **카트 모듈 분리**                      | **가게** <br> **상품**  <br> **가게 및 상품 카테고리** <br> **검색 기능** | **카트** <br> **주문**  <br> **결제**

<br>

## 주요 기능


<details>
<summary>🪪 <strong>JWT 기반 인증/인가</strong></summary>

- JWT를 활용해 사용자 인증 및 권한 인가를 처리합니다.
- 사용자 역할 기반 권한 제어 (USER, SELLER, ADMIN)


</details>

<details>
<summary>🔎 <strong>최적의 검색을 통한 상품 및 가게 추천?</strong></summary>



</details>

<details>
<summary>🛒 <strong>주문 및 결제 처리</strong></summary>

**[장바구니]**
- 장바구니에서 상품 추가

**[주문]**

- 주문 상태: `PENDING` ➡️ `ACCEPTED (가게 승인)` ➡️ `COMPLETED` (취소시 **CANCELED**, 가게 거절 시 **REJECTED** )
 

**[결제]**

- 결제 상태: `READY` ➡️ `PAID` (실패시 **FAILED** , 취소시 **CANCELED** )


- 결제 수단으로는 **CARD**, **ZERO_PAY** 등을 지원하며, Iamport 결제 모듈을 연동
- **결제 성공** 시 <ins>결제 상태 업데이트</ins>, **실패** 시 <ins>주문 PENDING 상태</ins>
- ZERO_PAY는 QR 코드 생성을 통해 모의 결제 시뮬레이션을 지원

</details>


<br>

## ERD
```mermaid
erDiagram
    USERS ||--o{ STORES : owns
    USERS {
    bigint id PK
    varchar username "이름"
    varchar email "이메일"
    varchar password "비밀번호"
    varchar address "주소"
    varchar business_number UK "Seller 사업자 번호"
    enum role "USER, Seller, Admin"
    varchar phone_number "전화번호"
    datetime created_at 
    datetime deleted_at
    datetime update_at
    }

    STORES ||--|{ STORE_CATEGORIES : categorized_as
    STORES ||--o{ PRODUCTS : sells
    STORES {
        bigint id PK
        bigint seller_id FK 
        bigint store_category_id FK
        varchar(255) name "가게 이름"
        varchar(255) address "가게 주소"
        varchar(255) description "가게 설명"
        time(6) opened_at "가게 오픈 시간"
        time(6) closed_at "가게 마감 시간"
        bit is_deliverable "배달 가능 여부"
        datetime created_at
        datetime deleted_at
        datetime update_at
    }

    STORE_CATEGORIES {
        bigint id PK
        varchar(255) name "카테고리 이름"
        datetime created_at
        datetime deleted_at
        datetime update_at
    }

    PRODUCT_CATEGORIES ||--o{ PRODUCT_CATEGORIES_MAPPING : maps
    PRODUCTS ||--o{ PRODUCT_CATEGORIES_MAPPING : mapped_to
    PRODUCT_CATEGORIES {
        bigint id PK
        bigint parent_id FK 
        varchar(255) name
        int depth 
        datetime created_at
        datetime deleted_at
        datetime update_at
    }

    PRODUCT_CATEGORIES_MAPPING {
        bigint id PK
        bigint category_id FK
        bigint product_id FK
        datetime created_at
        datetime deleted_at
        datetime update_at
    }

    PRODUCTS {
        bigint id PK
        bigint store_id FK
        varchar(255) name "상품 이름"
        varchar(255) description "상품 설명"
        bigint price "상품 가격"
        bigint stock "상품 재고"
        datetime created_at
        datetime deleted_at
        datetime update_at
    }

    USERS ||--o{ ORDERS : places
    STORES ||--o{ ORDERS : receives
    ORDERS ||--o{ ORDER_HISTORIES : contains
    ORDERS ||--o{ PAYMENTS : has
    ORDERS {
        bigint id PK
        bigint user_id FK
        bigint store_id FK
        varchar(255) name "주문명"
        enum payment_method "결제 방식"
        enum status "주문 상태"
        varchar(255) request_message "요청 메세지"
        time(6) ready_at "준비 시간"
        decimal total_price "총 가격"
        datetime created_at
        datetime deleted_at
        datetime update_at
    }

    ORDER_HISTORIES {
        bigint id PK
        bigint order_id FK 
        bigint product_id FK 
        int quantity "주문 총 수량"
        datetime created_at
        datetime deleted_at
        datetime update_at
    }

    PAYMENTS {
        bigint id PK
        bigint order_id FK
        varchar buyer_email "주문자 메일"
        varchar merchant_uid "결제 고유 아이디"
        varchar pay_status "결제 상태"
        varchar pg "결제 대행사"
        decimal total_price "결제 총 가격"
        datetime created_at
        datetime deleted_at
        datetime update_at
    }
```

<br>

## API 명세서

추가 

<br>

## 인프라 아키텍처 & 기술 스택

### 인프라 아키텍처

![Image](https://github.com/user-attachments/assets/480572d4-709e-461a-95bb-5006ac71933c)

<br>

### 🛠️ Skills
<p align="left">
  <img src="https://img.shields.io/badge/GitHub-181717?style=flat&logo=github&logoColor=white"/>
  <img src="https://img.shields.io/badge/GitHubActions-2088FF?style=flat&logo=githubactions&logoColor=white"/>
  <img src="https://img.shields.io/badge/Java-007396?style=flat&logo=java&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring-6DB33F?style=flat&logo=spring&logoColor=white"/>
  <img src="https://img.shields.io/badge/Gradle-02303A?style=flat&logo=gradle&logoColor=white"/>
  <img src="https://img.shields.io/badge/Jira-0052CC?style=flat&logo=jira&logoColor=white"/>
  <br/>
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white"/>
  <img src="https://img.shields.io/badge/Redis-DC382D?style=flat&logo=redis&logoColor=white"/>
  <img src="https://img.shields.io/badge/RabbitMQ-FF6600?style=flat&logo=rabbitmq&logoColor=white"/>
  <img src="https://img.shields.io/badge/ElasticSearch-005571?style=flat&logo=elasticsearch&logoColor=white"/>
</p>

<br>

## 성과 및 회고

### 성과






<br>

### 보안점


