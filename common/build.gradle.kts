plugins {
    `java-library`
}

dependencies {
    implementation("jakarta.persistence:jakarta.persistence-api")
    implementation("org.springframework.data:spring-data-commons")
    implementation("org.springframework.data:spring-data-jpa")
    implementation("com.fasterxml.jackson.core:jackson-databind")

    implementation("org.apache.tika:tika-core:3.2.2")  // check file signatures
}