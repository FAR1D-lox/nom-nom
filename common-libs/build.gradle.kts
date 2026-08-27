plugins {
    id("java-library")
}

group = "com.nomnom"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    val springBom = platform("org.springframework.boot:spring-boot-dependencies:4.1.0")

    implementation(springBom)
    annotationProcessor(springBom)
    testImplementation(springBom)
    testCompileOnly(springBom)
    testAnnotationProcessor(springBom)

    api("jakarta.validation:jakarta.validation-api")
    implementation("io.jsonwebtoken:jjwt:0.12.6")

    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
}

tasks.test {
    useJUnitPlatform()
}