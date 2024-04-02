plugins {
    id("java")
}

group = "me.meiallu.cabbadb"
version = "0.1"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    implementation("com.google.code.gson:gson:2.10.1")
    implementation("io.netty:netty-all:4.1.108.Final")
}