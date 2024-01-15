# spring-cloud-msa

## index

- [01 discovery_service](./discoveryservice/)

## 02 user-service

### gradle biuld & terminal 실행

./gradlew build  
dir -> build/libs  
java -jar user-service-0.0.1-SNAPSHOT.jar or  
java -jar -Dserver.port=9002 user-service-0.0.1-SNAPSHOT.jar

### server.port = 0

위 상태로 사용하면 랜덤 포트로 등록이 된다 -> eureka 에서는 0 으로 뜨지만 마우스를 대면 port가 바뀐다.  
하지만 같은 port 로 넘어 올경우 표시되는건 하나 이걸 구분하는 방법은 instance를 사용

## 03 API Gateway Service

- 인증 및 권한 부여
- 서비스 검색 통합
- 응답 캐싱
- 정책, 회 차단기 및 Qos 다시시도
- 부하분산
- 로깅 추적, 상관관계
- 헤더, 쿼리문자열 청구 변환
- IP 허용 목록에 추가

### spfing cloud 에서의 msa 간 통신

1. RestTemplate
2. Feign Client

- Ribbon : Client side Load Balancer (서버에 가해지는 부하를 분산 해주는 장치 또는 기술)  
  load banalacer를 해주기 위한 서비스 -> 비동기 지원(X) 최신에서는 잘 안쓰고 있다고 합니다.
  - 서비스이름으로 호출
  - Health Check

[Netflix Zuul maintenace 상태](https://spring.io/blog/2018/12/12/spring-cloud-greenwich-rc1-available-now#spring-cloud-netflix-projects-entering-maintenance-mode)

## 04 first-service, second-service, apigatdway-service

## 05 gateway filter

## 06 LoadBalancer

- first, second 서비스들은 위에 있는 내욜들을 보여주기 위해 했던 내용 앞으로는 작은 프로젝트로해서 대락젹인 msa 구성을 알아볼 예정

## 07 e-commerce user-service 구현

- h2 db, JPA, 간단한 security 적용
