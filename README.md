# spring-cloud-msa

## 01 discovery_service

## 02 user-service

### gradle biuld & terminal 실행

./gradlew build  
dir -> build/libs  
java -jar user-service-0.0.1-SNAPSHOT.jar or  
java -jar -Dserver.port=9002 user-service-0.0.1-SNAPSHOT.jar

### server.port = 0

위 상태로 사용하면 랜덤 포트로 등록이 된다 -> eureka 에서는 0 으로 뜨지만 마우스를 대면 port가 바뀐다.  
하지만 같은 port 로 넘어 올경우 표시되는건 하나 이걸 구분하는 방법은 instance를 사용
