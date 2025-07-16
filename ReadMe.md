# ict-cloud-spring
# ICT-CLOUD 클라우드 사이트 구현

(추후수정..)
구글 드라이브처럼 클라우드 저장기능을 구현한 사이트를 만들었다.
디자인은 https://kiosk.ac/ 을 참고했다.

**프레임워크: mvc 모델2를 적용하기 위해서 spring 사용
개발환경: intellij IDLE
협업도구: github, discord
데이터베이스: mysql
백엔드 언어: java
프론트 언어: html, javascript
가상화 플랫폼: docker
인원수: 3명
제작 기간: 2024.05.27~2024.07.03

***

### http를 https로 리디렉션

HttpToHttpsRedirectConfig.java
```java
package org.hoseo.ictcloudspring.common;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpToHttpsRedirectConfig {

    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };

        tomcat.addAdditionalTomcatConnectors(httpConnector());
        return tomcat;
    }

    @Bean
    public Connector httpConnector() {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setScheme("http");
        connector.setPort(80); // HTTP 포트
        connector.setSecure(false);
        connector.setRedirectPort(443); // HTTPS 포트로 리디렉션
        return connector;
    }
}

```

***

### 데이터베이스
AccessPermissions
- AccessPermissionID
- UserID
- FileID
- FolderID
- AccessType

Favorites
- FavoriteID
- UserID
- FileID
- FolderID
- AddedDateTime

Files
- FileID
- UserID
- FolderID
- Filename
- FileSize
- StoragePath
- UploadDate
- LastModifiedDate
- FileType

Folders
- FolderID
- ParentFolderID
- UserID
- FolderName
- StoragePath

ShareInfo
- ShareID
- OwnerID
- ItemID
- ItemType
- PermissionType
- ExpirationDate
- CreationDate
- SharePassword

ShareTargetUser
- ShareID
- UserID

Token
- Email
- Token

UploadHistory
- UploadHistoryID
- UserID
- FileID
- FolderID
- UploadDateTime

Users
- UserID
- Name
- Email
- Password
- RegistrationDate
- Level
- storageMaxSize

***

### controller
**AdminController.java**
**FileController.java**
**MainController.java**
**NoticeController.java**
**ShareController.java**
**SystemStatusController.java**
-로그로 서버, 데이터베이스, 사용한 저장공간, 총 저장공간 조회
**UserController.java**
-httpsession 이용해서 로그인, 로그아웃, 회원가입, 비밀번호수정, 회원탈퇴
-회원가입시 이메일로 토큰 전송해서 인증
-로그인상태 체크

***

### dto
**AcessPermission**
**DatabaseStatus**
**Favorite**
**File**
**Folder**
**Notice**
**ServerStatus**
**ShareInfo**
**ShareTargetUser**
**StorageUsage**
**Token**
**UploadHistory**
**User**

***

### js
**account.js**
**admin.js**
**header.js**
**share.js**
**sidebar.js**
**upload.js**
**userInfo.js**

***

### jsp
**admin > admin.jsp** #관리자페이지
-대시보드, 사용자관리, 파일관리, 스토리지 사용량, 설정
![](https://velog.velcdn.com/images/dltmddn/post/b19d33c3-528c-481c-9aea-3d12007f16ad/image.png)

**error > 404.jsp** #에러페이지
**file > upload.jsp** #업로드페이지
-파일, 폴더 추가
-업로드 기능
-공유 기능
![](https://velog.velcdn.com/images/dltmddn/post/970d6f43-40d0-4d53-a17e-870baf9c26c9/image.png)

**share > expired.jsp**
-설정한 공유링크의 기간이 지난 후 링크에 접속했을때
**share > notfound.jsp**
**share > share.jsp**
-공유된 링크 접속 시
-비밀번호 설정이 되있다면 비밀번호 입력
![](https://velog.velcdn.com/images/dltmddn/post/3e9bcc29-9195-4749-a8a1-05bcd9fc842f/image.png)

**user > account.jsp**
![](https://velog.velcdn.com/images/dltmddn/post/d40199d7-d72c-495f-8598-675ca1aa8a4f/image.png)

**user > userInfo.jsp**
![](https://velog.velcdn.com/images/dltmddn/post/8b21cfae-66d4-41ec-9aae-aebc8f753c38/image.png)

**header.jsp**
**main.jsp**
![](https://velog.velcdn.com/images/dltmddn/post/aea74582-ffa3-4f6c-96ff-27fca4d0f809/image.png)


**sidebar.jsp**
![](https://velog.velcdn.com/images/dltmddn/post/3a45d3b8-0e85-4189-bbf1-96916f906aef/image.png)


**write.jsp**

***

### 실행 안되는 에러
로그에 java.lang.ClassNotFoundException: com.mysql.cj.jdbc.Driver
예외가 발생하며 실행이 안되는 버그 발생

**1.참고 -> [com.mysql.cj.jdbc.Driver 에러 해결](https://velog.io/@jin-dooly/Spring-com.mysql.cj.jdbc.Driver-%EC%97%90%EB%9F%AC-%ED%95%B4%EA%B2%B0)**
build.gradle의 dependencies에
```
implementation 'org.springframework.boot:spring-boot-starter'
implementation 'org.springframework.boot:spring-boot-starter-web'
```
있는지 확인, 문제없음


**2.Gradle Dependency refresh 실행**
터미널에 _.\gradlew --refresh-dependencies_ 실행 ->
```
Exception in thread "main" java.lang.NoClassDefFoundError: org/springframework/boot/SpringApplication
	at org.hoseo.ictcloudspring.IctCloudSpringApplication.main(IctCloudSpringApplication.java:9)
Caused by: java.lang.ClassNotFoundException: org.springframework.boot.SpringApplication
	at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:641)
	at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:188)
	at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:525)
```

새로운 에러 발생.
:NoClassDefFoundError.java, ClassNotFoundException.java 확인 -> org.springframework.boot.SpringApplication 클래스가 없다고함.
build.gradle에 이미 있음

**3.기존파일 지우고 다시 빌드**
_./gradlew clean build_ 실행 ->
```
A problem occurred configuring root project 'ict-cloud-spring'.
> Could not resolve all artifacts for configuration ':classpath'.
   > Could not resolve org.springframework.boot:spring-boot-gradle-plugin:3.2.5.
     Required by:
         project : > org.springframework.boot:org.springframework.boot.gradle.plugin:3.2.5
      > No matching variant of org.springframework.boot:spring-boot-gradle-plugin:3.2.5 was found. The consumer was configured to find a library for use during runtime, compatible with Java 8, packaged as a jar, and its dependencies declared externally, as well as attribute 'org.gradle.plugin.api-version' with value '8.7' but:
          - Variant 'apiElements' declares a library, packaged as a jar, and its dependencies declared externally:
              - Incompatible because this component declares a component for use during compile-time, compatible with Java 17 and the consumer needed a component for use during runtime, compatible with Java 8
```
: 현재 spring-boot-gradle-plugin이 java 8을 요구하지만  현재 java버전이 17이다

파일 -> 설정 -> 빌드 -> 빌드도구 -> Gradle 확인
![](https://velog.velcdn.com/images/dltmddn/post/38f5daf5-4d5d-406e-b2a0-54b2189775ed/image.png)

Gradle JVM: corretto-17
-> java-8로 변경하거나, Gradle과 호환되는 java버전을 17로 설정

후자로 진행

build.gradle
```
java {
    sourceCompatibility = '17'
}
```
이미 호환을 원하는 java 버전이 17로 설정되 있음.

**4.Java 환경변수 확인**
java의 환경변수가 17이어야 한다

-> window -> 시스템 환경변수 편집 -> 환경변수
![](https://velog.velcdn.com/images/dltmddn/post/c10036b2-03c5-4e53-afef-55bd7775ff12/image.png)
현재 java 환경변수가 jdk-1.8(java-8)로 설정되있음 -> jdk17 설치후 변경

![](https://velog.velcdn.com/images/dltmddn/post/4008d188-e5ca-440a-ac5b-cf931a7263db/image.png)

**Gradle과 Spring Boot사이 호환성 확인**

Gradle 버전 확인(8.7)
\>./gradlew --version
```
------------------------------------------------------------
Gradle 8.7
------------------------------------------------------------

Build time:   2024-03-22 15:52:46 UTC
Revision:     650af14d7653aa949fce5e886e685efc9cf97c10

Kotlin:       1.9.22
Groovy:       3.0.17
Ant:          Apache Ant(TM) version 1.10.13 compiled on January 4 2023
JVM:          17.0.12 (Oracle Corporation 17.0.12+8-LTS-286)
OS:           Windows 11 10.0 amd64
```

Spring Boot 버전 확인(3.2.5)
```
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.2.5'
    id 'io.spring.dependency-management' version '1.1.4'
}
```
Spring Boot, Gradle 호환성 표

| Spring Boot Version	 | 	Gradle Version |
| - | - |
| 2.5.x	 | 	6.x |
| 2.6.x	 | 	6.x, 7.x |
| 2.7.x	 | 	7.x |
| 3.x	 | 7.x, 8.x |

호환성 일치

\> .\gradlew --refresh-dependencies
\> ./gradlew clean build
실행->

```
IctCloudSpringApplicationTests > contextLoads() FAILED
    java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:180
        Caused by: org.springframework.beans.factory.BeanCreationException at AbstractAutowireCapableBeanFactory.java:1786
            Caused by: org.hibernate.service.spi.ServiceException at AbstractServiceRegistryImpl.java:276                                                                                                
                Caused by: org.hibernate.HibernateException at DialectFactoryImpl.java:191
```
에러 발생
contextLoads()에서 
java.lang.IllegalStateException, BeanCreationException, ServiceException, HibernateException오류 발생



>실행 에러수정 요약
1. build.gradle>dependencies{...}에
implementation 'org.springframework.boot:spring-boot-starter'
implementation 'org.springframework.boot:spring-boot-starter-web'
유무 확인
2. 설정에서 Gradle JVM이 java-17인지 확인
3. 환경변수 java를 jdk17로 변경
4. Gradle, Spring Boot 버전 호환성 확인
