plugins {
    val kotlinVersion = "1.6.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("net.mamoe.mirai-console") version "2.10.0"
}


group = "org.example"
version = "0.1.0"

repositories {
    maven("https://maven.aliyun.com/repository/public") // 阿里云国内代理仓库
    mavenCentral()
}

dependencies {
    implementation("net.mamoe:mirai-console-terminal:2.0.0") // 自行替换版本
    implementation("net.mamoe:mirai-core:2.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.2.2")
    implementation("com.google.code.gson:gson:2.8.2")
}