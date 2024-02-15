# 02. user-service

## user-service 간단한 등록

discoveryServer 에 이제 해당 서비스를 등록 하려고 한다.

```java
@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
```

application.yml

``` yml
server:
  port: 9001

spring:
  application:
    name: user-service
eureka:
  client:
    register-with-eureka: true
    # Eureka 서버로 부터 인터슨터들의 정보를 주기적으로 가져울 것인지 설정하는 속성
    fetch-registry: true
    # Eureka server 에 등록 하는 작업
    service-url:
      defaultZone : http://localhost:8761/eureka
```

defaultZone 으로 접근하면 등록된걸 확인할 수 있다.

여러개의 서비스를 등록하여 보고 싶다면 ```server.port``` 의 값을 다른 값으로 바꾼후 빌드한걸 실행 하면 된다.

### gradle biuld & terminal 실행
```shell
./gradlew build
dir -> build/libs  
java -jar user-service-0.0.1-SNAPSHOT.jar or  
java -jar -Dserver.port=9002 user-service-0.0.1-SNAPSHOT.jar
```

### user-service loadBalance

```server.port= 0``` 사용하면 스프링 시작시 port 가 랜덤으로 표기된다.   
```[  restartedMain] o.s.b.d.a.OptionalLiveReloadServer       : LiveReload server is running on port 35729```
해당 service 를 두개 실행시키면 EurekaServer 에 두개가 등록 될까?
그렇지 않다, Eureka Server 에가서 확인해보면 ```192.068.0.8:user-service:0``` 로 하나만 실행된다.
위와같은 사항을 해결 하려면 어떻게 해야할까??

application.yml

```yml
...
# 만약 port 가 0(random) 인경우 구분하기위해
# instance-id: ${spring.cloud.client.hostname}:${spring.application.instance_id: {random.value}}
eureka:
  instance-id: ${spring.application.name}:${spring.application.instance_id:${random.value}}
...
```

위 내용처럼 ```yml``` 을 수정하면 EurekaServer 에 ```user-service:8f00b71e2bf43d17aacf66fff4a94b12``` 로 변경되어 실행된다.

# 04-1 Users MicroService

기존 [02 user-service](#02-user-service) 위에 만들 예정이다. 앞이랑 다른점은 만들때 dependecy 도 함께 적을 예정이며 코드는 바로가기로 만들어 둘 예정이다.

시작전 yml 에서 port와 service instnace-id 를 다른것과 겹치지 않게 하기위해 손 볼 예정이다.

``` yml
server:
  port: 0

spring:
  application:
    name: user-service

eureka:
  instance:
    instance-id: ${spring.application.name}:${spring.application.instance_id:${random.value}}
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone : http://localhost:8761/eureka


greeting:
  message: Welcome to the Simple E-commerce.
```

### welcome

간단히 api 호출이 되는지 확인 해보는 method 이다. [UserController.java](./src/main/java/com/eureka/userservice/controller/UserController.java) 의 ```public String stats()```,
 ```public String welcome()``` 로 간단히 env(application.java) 값을 가져오는 작업을 한다.

### H2 Db

build.gradle dependencies 에 추가

```gradle 
	// https://mvnrepository.com/artifact/com.h2database/h2
	implementation 'com.h2database:h2'
```

application.yml

```yml
....
spring:
  application:
    name: user-service
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
....
```

yml 에 h2 db 정보를 입력 하고 ```h2.console.path``` 를 통해 web에서 확인할 수 있다.

## 04-2 JPA

build.gradle

```gradle
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
```

application.yml

```yml
spring:
  application:
    name: user-service
  h2: ...
  datasource: ...
  jpa:
    hibernate:
      ddl-auto: create
    properties:
      hibernate:
        format_sql: true
        show_sql: true 
```
각 각 추가

## 04-3 Security & Spring Cloude Gateway 연동

build.gradle

```gradle
implementation 'org.springframework.boot:spring-boot-starter-security'
```

- [security](./src/main/java/com/eureka/userservice/security/) 를 작성 합니다.

sercurity 자세한 작성은 후에 확인할 것입니다. 간단하게 알아보자면

WebSerucity.java
``` java
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurity {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filter(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests
                                .requestMatchers("/**")
                                .permitAll();
                )


        http.headers(httpSecurityHeadersConfigurer ->
                httpSecurityHeadersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        return http.build();
    }
```

작성후 UserServiceImpl.java 에 BycryptePaswordEncoder 를 DI 받아 사용한다.

<br>

user-service 를 [apigateway](/apigateway-service/src/main/resources/application.yml) 에 등록 해야 한다. 

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
        - id: user-service
          uri: lb://USER-SERVICE
          predicates:
            - Path=/user-service/**
```

user-service controller 의 health_check 를 실행 하면 확인할 수 있다.

- http://localhost:{user-service IP}/user-service/health_check (gateway Ip 가아닌 직접 호출)
- http://localhost:8080/user-service/heatlh_check (gateway Ip 를 통한 호출)

둘다 확인이 되어야 한다.
조금 깊게 이야기 하자면 만약 ```userController.java``` 에 /user-service 로 prefix 적용이 안되어 있으면 routes등록을 하더라도 gateway 에서 접근이 불가능하다. 즉, apiGateway 에서 predicates 에 값을 지정하더라도 실제 url 은 /user-service/ 를 포함한 값으로 넘어가기 때문이다.

## 04-6 Spring Security 설정

### AuthenticationFilter 추가

- [AuthenticationFilter](/src/main/java/com/eureka/userservice/security/AuthenticationFilter.java)
- [LoginService](/src/main/java/com/eureka/userservice/security/login/LoginService.java)
- [WebSecurity](/src/main/java/com/eureka/userservice/security/WebSecurity.java)

AuthenticationFilter, LoginService 만든후 WebSecurity 에서 filter 를 등록하면 된다.
