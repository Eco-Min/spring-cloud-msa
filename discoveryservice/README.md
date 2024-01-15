# discovery_service

## 01. discovery 등록

- discovery 가 해주는 역확은 사용중인 msa application service를 등록하여 외부에서 msa를 사용하기위해 사용되는 저장소 (key : value) 로 어떤 서비스 어디 위치에 있는지 여기에 다 저장되어 있다.

```java
@SpringBootApplication
@EnableEurekaServer // main 에 해당 어노테이션을 사용하면 EurekaServer -> discovery 사용가능
public class DiscoveryserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiscoveryserviceApplication.class, args);

	}
}
```

application.yml

``` yml
server:
  port: 8761

spring:
  application:
    name: discoveryservice
eureka:
  # client 로 나와도 Eureka lib 가 포함 -> EurekaClient 로 등록을 시도한다.
  # 하지만 Server 이기 때문에 아래 작업을 추가적으로 한다.
  client: 
    # true 로 하면 자기 자신의 정보를 자기 자신에게 등록하게 하지 않기위해
    register-with-eureka: false
    fetch-registry: false
```

http://127.0.0.1:8761/ 해당 service 를 실행시키고 url로 들어가면 Spring Eureka를 볼 수 있다.