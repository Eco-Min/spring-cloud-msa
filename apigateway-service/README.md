# 03. apigateway-service

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

## Spring Cloud Gateway - Custom Filter

제목 그대로 custom하게 만드는 filter 이다.
CustomFilter 는 반드시 ```AbstractGatewayFilterFactory``` 를 상속받아 자기 자신의 내부 클래스인 
```Config``` 를 만들어야한다

<details>
<summary>CustomFilter</summary>

```java
@Component
@Slf4j
public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config> {

    public CustomFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        // Custom Pre Filter
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Custom PRE filter: request id -> {}", request.getId());

            return chain.filter(exchange).then(Mono.fromRunnable(() ->
                    log.info("Custom Post filter: response code -> {}", response.getStatusCode())
            ));
        });
    }

    public static class Config {
        // Put the Configuration properties
    }
}
```

</details>

위으 코드를 만들었다면 yml 에 있는 기존 filter 를 주석처리 하여야 한다.

application.yml

```yml
        ...
        - id: first-service
          uri: lb://MY-FIRST-SERVICE
          predicates:
            - Path=/first-service/**
          filters:
#            - AddRequestHeader=first-request, first-request-headerYml
#            - AddResponseHeader=first-response, first-response-headerYml
            - CustomFilter
        ...
```
</details>
<br>

## Spring Cloud Gateway - Global Filter

개별 적인 filter 가 아닌 해당 api 가 실행될때 gateway 에서 먼저 실행 해주는 전역 설정 filter 이다.
일반적인 거랑 비슷하지만 이번에는 ```Config``` 를 사용 할 예정 입니다.

<details>
<summary>globalFilter</summary>

```java
@Component
@Slf4j
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    public GlobalFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        // Custom Pre Filter
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Global filter: {}", config.baseMessage);

            if (config.preLogger) {
                log.info("Global filter Start: request id -> {}", request.getId());
            }

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                        if (config.postLogger) {
                            log.info("Global filter end: response code -> {}", response.getStatusCode());
                        }
                    }
            ));
        });
    }

    @Data
    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }
}
```
</details>
<br>

이제 위 의 코드를 작성 하였다면 yml에 저장 해야 한다.

application.yml

```yml
...
spring:
  application:
    name: apigateway-service
  cloud:
    gateway:
      default-filters:
        - name: GlobalFilter
          args:
            baseMessage: Spring Cloud Gateway Global Filter
            preLogger: true
            postLogger: true
      routes:
      ...
```

```default-filters``` 를 보면 우리가 ```GlobalFilter``` 에서 본 ```Config``` 의 값 을 입력하는걸 알 수 있다.

## Spring Cloud Gateway - Login Filter

Login filter 를 간단하게 만들어 볼 예정이다. second-service 에서만 넣을 거지만 Login fitler를 사용하게 되면


Gatdway Client -> Gatdway Handler -> Global Filter -> Custom Filter -> Login Filter -> Proxied Service

순서대로 실행 될 예정입니다.

<details>
<summary>loginfilter</summary>

```java
@Component
@Slf4j
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {

    public LoggingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        GatewayFilter filter = new OrderedGatewayFilter((exchange, chain) ->{
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Logging filter: {}", config.baseMessage);

            if (config.preLogger) {
                log.info("Logging Pre filter : request id -> {}", request.getId());
            }

            return chain.filter(exchange).then(Mono.fromRunnable(() ->
                    log.info("Logging Post filter : response code -> {}", response.getStatusCode())
            ));
        }, Ordered.LOWEST_PRECEDENCE);

//        Ordered.HIGHEST_PRECEDENCE -> filter의 우선순위 설정 값이다 -> 최우선
//        Ordered.LOWEST_PRECEDENCE -> filter의 우선순위 설정 값이다 -> 나중

        return filter;
    }

    @Data
    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }
}
```
</details>
<br>


이제 위 의 코드를 작성 하였다면 yml에 저장 해야 한다.

application.yml

```yml
        ....
        - id: second-service
          uri: http://localhost:8082/
          predicates:
            - Path=/second-service/**
          filters:
            - AddRequestHeader=second-request, second-request-headerYml
            - AddResponseHeader=second-response, second-response-headerYml
            - name: CustomFilter
            - name: LoggingFilter
              args:
                baseMessage: Hi, there. Logging Filter
                preLogger: true
                postLogger: true
          ....
```

## Spring cloude Gateway - load balance

Eureka 서버에 등록 하여 들어온 요청 url 로 포워딩 하는 역활을 한다.

client 요청 -> API Gateway -> Eureka (등록된 Api 다시 Gateway 전달) -> API Gateway -> 해당 url 서버

buil.gradle

```gradle
implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'
```

위처럼 작동 하게 하려면 yml 에 있는 gateway 설정 정보를 조금 손봐야한다.

application.yml
<details>
<summary>gateway load balanace</summary>

```yml 
spring:
  application:
    name: apigateway-service
  cloud:
    gateway:
      default-filters:
        ....
      routes:
        - id: first-service
          uri: lb://MY-FIRST-SERVICE
          predicates:
            - Path=/first-service/**
          filters:
            - CustomFilter
        - id: second-service
          uri: lb://MY-SECOND-SERVICE
          predicates:
            - Path=/second-service/**
          filters:
            - AddRequestHeader=second-request, second-request-headerYml
            - AddResponseHeader=second-response, second-response-headerYml
            - name: CustomFilter
            - name: LoggingFilter
              args:
                baseMessage: Hi, there. Logging Filter
                preLogger: true
                postLogger: true
```
</details>
<br>


uri 는 Eureka 에 등록된 각 API 서비스의 이름 으로 되체된다.

load balancer 를 확인 하려면 first-service의 port 를 변경한후 first-service를 두개 실행 하면
Euereak server 에 MY-FIRST-SERVICE 인스턴스가 2개 생성된다. -> http://127.0.0.1/first-service/check 실행 해보자.

first-service/chekck 는 port 번호를 출력 한다.


