plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")

    implementation("org.openapitools:jackson-databind-nullable:0.2.6") // JsonNullables (3 states)

    implementation(project(":common"))
    implementation(project(":file"))
}