# user-service

## 02. user-service 간단한 등록

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