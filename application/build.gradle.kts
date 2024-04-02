plugins {
    id("java")
}

group = "me.meiallu"
version = "1.0"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    implementation(project(":client"))
}