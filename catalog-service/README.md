# 04-4 Catalog

사용자가 주문하기전 상풍 목록을 검색하기 위한 Application으로 개발 기존 구성은 코드로 확인, Eureka 등록 정도만 남길 예정입니다.

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
    name: catalog-service
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

catalog-service 를 [apigateway](/apigateway-service/src/main/resources/application.yml) 에 등록 해야 한다. 

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
          uri: lb://CATALOG-SERVICE
          predicates:
            - Path=/catalog-service/**
```

user-serivce 의 health_check 했듯이 확인해보면 된다.