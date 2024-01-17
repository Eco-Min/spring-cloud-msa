# apigateway-service

사용자가 설정한 라우팅 설정에 따라서 endPoint 로 클라이언트 대신하여 요청 및 응답을 하는 proxy 역활을 한다.   
시스템의 내부구조는 숨기고 외부에는 가공하여 응답할 수 있다. 

- 인증 및 권한 부여
- 서비스 검색 통합
- 응답 캐싱
- 정책, 회 차단기 및 Qos 다시시도
- 부하분산
- 로깅 추적, 상관관계
- 헤더, 쿼리문자열 청구 변환
- IP 허용 목록에 추가

## spfing cloud 에서의 msa 간 통신

1. RestTemplate

```java
RestTemplate restTemplate = new RestTemplate();
restTemplate.getForObject("http://localhost:8080/", User.class, 200);
```

2. Feign Client

```java
@FeignClient("stores")
public interface StoreClient{
  @RequestMapping(method = ReqestMethod.GET, value = "/stores")
  List<Store> getStores();
}
```

3. Ribbon : Client side Load Balancer (서버에 가해지는 부하를 분산 해주는 장치 또는 기술)  
  load banalacer를 해주기 위한 서비스 -> 비동기 지원(X) 최신에서는 잘 안쓰고 있다고 합니다.
    - 서비스이름으로 호출
    - Health Check

<br>
그래서 현재 maintenance 상태는
- Ribbon
- [Netflix Zuul maintenace 상태](https://spring.io/blog/2018/12/12/spring-cloud-greenwich-rc1-available-now#spring-cloud-netflix-projects-entering-maintenance-mode)

## Spring Gateway service
[first-service](../first-service/), [second-service](../second-service/) 를 api gateway에 붙일 예정이다.

application.yml 
```yml
server:
  port: 8080
eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://localhost:8761/eureka
spring:
  application:
    name: apigateway-service
  cloud:
    gateway:
      routes:
        - id: first-service #service name
          uri: http://localhost:8081/ #uri 설정
          predicates:
            - Path=/first-service/** #gateway 에서 해당 path 로 호출
        - id: second-service
          uri: http://localhost:8082/
          predicates:
            - Path=/first-service/**
```

하지만 ```localhost:8080/first-service/welcome``` 을 호출하면 404 error 가 뜬다 이유가 멀까?
위에 있는 걸 조금더 풀어보면 first-service uri + predicates 가 간다 즉, 
```localhost:8081/first-service/welcome```    
그래서 first-service 의 controller 들 모두 ```@RequestMapping("first-service")``` 로 매핑 시켜야 한다.

## Spring Cloud Gateway - Filter

![filter](../images/gateway%20Filter.png)

- [자바 코드로 하는 방법](./src/main/java/com/msa/apigatewayservice/config/FilterConfig.java)   
자바 코드로 실행을 하려면 yml 내 ```cloud.gateway.route```를 잠시 주석처리하자.   
해당 코드를 실행하여 first-sertice 의 message를 호출하면 log 에 ```first-request-header``` 가 보이는걸 보인다.   
response-header 의 경우 brawoser 의 network 에서 확인이 가능하다.

- yml 에서 filter 적용   
yml 에서 적용 되려면 자바 코드를 막아야한다 그래서 ```@Configuration, @Bean``` 을 주석처리 하자   

applicationl.yml
```yml
...
      routes:
        - id: first-service #service name
          uri: http://localhost:8081/ #uri 설정
          predicates:
            - Path=/first-service/** #gateway 에서 해당 path 로 호출
          filters:
           - AddRequestHeader=first-request, first-request-headerYml
           - AddResponseHeader=first-response, first-response-headerYml          
        - id: second-service
          uri: http://localhost:8082/
          predicates:
            - Path=/first-service/**
          filters:
            - AddRequestHeader=second-request, second-request-headerYml
            - AddResponseHeader=second-response, second-response-headerYml
...
```