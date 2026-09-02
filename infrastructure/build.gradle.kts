plugins {
    `java-library`
}

dependencies {
    // Cloudinary
    implementation("com.cloudinary:cloudinary-http44:1.36.0")
    implementation("com.cloudinary:cloudinary-taglib:1.36.0")

    implementation("org.springframework:spring-web")
    implementation("org.springframework:spring-context")

    implementation(project(":common"))
}