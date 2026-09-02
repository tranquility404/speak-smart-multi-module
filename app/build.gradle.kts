plugins {
    id("org.springframework.boot")
}

dependencies {
//    Spring Security
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")

//    JPA config, base classes etc
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

//    All modules
    implementation(project(":auth"))
    implementation(project(":common"))
    implementation(project(":file"))
    implementation(project(":infrastructure"))
    implementation(project(":user-module"))

}