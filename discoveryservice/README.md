# discovery_service

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

``` yml
server:
  port: 8761

spring:
  application:
    name: discoveryservice
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```