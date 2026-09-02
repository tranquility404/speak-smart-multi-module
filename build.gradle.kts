import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    id("org.springframework.boot") version "3.5.6" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    java
}

group = "com.tranquility"
version = "0.0.1-SNAPSHOT"

allprojects {
    group = "com.tranquility"
    version = "0.0.1-SNAPSHOT"
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    configure<DependencyManagementExtension> {
        imports {
            mavenBom(SpringBootPlugin.BOM_COORDINATES)
        }
    }

    dependencies {
        // SpringDoc
        implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")

        // Spring Boot
//        implementation("org.springframework.boot:spring-boot-starter-actuator")
//        implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

        // Micrometer
//        implementation("io.micrometer:micrometer-core")
//        implementation("io.micrometer:micrometer-registry-prometheus")

        // Audio Processing
//        implementation("com.github.st-h:TarsosDSP:2.4.1")
//        implementation("org.apache.commons:commons-math3:3.6.1")
//        implementation("org.knowm.xchart:xchart:3.8.8")

        // Cloudinary
//        implementation("com.cloudinary:cloudinary-http44:1.36.0")
//        implementation("com.cloudinary:cloudinary-taglib:1.36.0")

        // Validation
        implementation("org.springframework.boot:spring-boot-starter-validation")

        // Lombok
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")
    }
}