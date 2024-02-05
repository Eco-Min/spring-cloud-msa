# 04-05 Order

사용자의 정보를 가지고 주문 관련된 부분을 이체 담당하는 application 을 만들 예정입니다.

구성은 코드로 간단 확인후 연결 부분만 확인 하겠습니다.

### dependency

- Spring web
- Spring Boot devTools
- Lombok
- Spring Data Jpa
- Eureka Discovery Client
- H2 database

application.yml

```yml
server:
  port: 0

spring:
  application:
    name: order-service
  h2:
    console:
      enabled: true
      settings:
        web-allow-others: true
      path: /h2-console
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:testdb;MODE=MYSQL
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    properties:
      hibernate:
        format_sql: true
        show_sql: true
    generate-ddl: true

eureka:
  instance:
    instance-id: ${spring.application.name}:${spring.application.instance_id:${random.value}}
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZOne : http://localhost:8761/eureka
```

구성이 끝났다면

order-service 를 [apigateway](/apigateway-service/src/main/resources/application.yml) 에 등록 해야 한다. 

appication.yml

```yml

spring:
  application:
    name: apigateway-service
  cloud:
    gateway:
      default-filters:
        ...
      routes:
        ...
        - id: catalog-service
        ...
        - id: order-service
          uri: lb://ORDER-SERVICE
          predicates:
            - Path=/order-service/**
```

user-serivce 의 health_check 했듯이 확인해보면 된다.