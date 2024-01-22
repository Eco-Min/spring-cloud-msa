# spring-cloud-msa
출처 : [Spring Cloud로 개발하는 마이크로서비스](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%ED%81%B4%EB%9D%BC%EC%9A%B0%EB%93%9C-%EB%A7%88%EC%9D%B4%ED%81%AC%EB%A1%9C%EC%84%9C%EB%B9%84%EC%8A%A4/dashboard)

## index

### [01 discovery_service](./discoveryservice/README.md#01-discovery_service)

### [02 user-service](./user-service/README.md#02-user-service)

### [03 API Gateway Service](./apigateway-service/README.md#03-apigateway-service)

### 04 e-commerce 서비스 구현   
  ![](./images/msa%20간단%20구성.png)
  - 여기서 kubernetes 와 CI/CD를 제외한걸 구성 해불 예정 입니다.
  - 간단하게 API를 만들거라 내부 구현 코드는 간단하게 넘길 예정 입니다.
  - docker, Spring-msa, kafka 를 이용하여 만들 예정 입니다.

| 구성요소 | 설명 |
| ---- | ---- |
| Git repository | 마키으로서비스 소스 관리 및 프로파일 관리 |
| Config Server | git 저장소에 등록된 프로파일 정 보 및 설정 정보 |
| Eureka Server | 마이크로 서비스 등록 및 검색 |
| API Gateway Server | 마이크로서비스 부하 분산 및 서비스 라우팅 |
| Microservices | 회원 MS, 주문 MS, 상품(카테고리) MS  |
| Queuing System | 마이크로서비스간 메시지 발행 및 구독 |