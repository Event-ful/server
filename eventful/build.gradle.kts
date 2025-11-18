plugins {
    java
    id("org.springframework.boot") version "3.5.4"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "side"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // 환경변수
    implementation("me.paulschwarz:spring-dotenv:4.0.0")
    // verificationCode용 랜던 함수 사용
    implementation("org.apache.commons:commons-text:1.10.0")

    // 메일 API
    implementation("com.sendgrid:sendgrid-java:4.10.3")

    //스웨거
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.13")

    // Session
    implementation("org.springframework.session:spring-session-core")
    implementation("org.springframework.session:spring-session-jdbc")

    // Oracle Object Storage
    implementation("com.oracle.oci.sdk:oci-java-sdk-objectstorage:3.76.1")
    implementation("com.oracle.oci.sdk:oci-java-sdk-common:3.76.1")
    implementation("com.oracle.cloud.spring:spring-cloud-oci-starter:1.4.5")

    // Apache HTTP Client 사용 (Jersey 대신 - Spring Boot 3.x 호환성 향상)
    implementation("com.oracle.oci.sdk:oci-java-sdk-common-httpclient-jersey3:3.76.1")

    // Jersey 3.x HTTP Client
    implementation("org.glassfish.jersey.core:jersey-client:3.1.5")
    implementation("org.glassfish.jersey.inject:jersey-hk2:3.1.5")
    implementation("org.glassfish.jersey.media:jersey-media-json-jackson:3.1.5")
    implementation("org.glassfish.jersey.connectors:jersey-apache-connector:3.1.5")

    // Jakarta API (Jersey 3.x 필수)
    implementation("jakarta.ws.rs:jakarta.ws.rs-api:3.1.0")
    implementation("jakarta.annotation:jakarta.annotation-api:2.1.1")

    compileOnly("org.projectlombok:lombok")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("com.h2database:h2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
