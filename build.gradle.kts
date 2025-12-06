plugins {
    java
    jacoco
    id("io.quarkus")
    id("com.diffplug.spotless") version "6.25.0"
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project
val lombokVersion: String by project
val mapstructVersion: String by project
val lombokMapstructVersion: String by project

dependencies {
    implementation(enforcedPlatform("$quarkusPlatformGroupId:$quarkusPlatformArtifactId:$quarkusPlatformVersion"))
    implementation("io.quarkus:quarkus-rest")
    implementation("io.quarkus:quarkus-amazon-lambda-http")
    implementation("io.quarkus:quarkus-smallrye-openapi")
    implementation("io.quarkus:quarkus-rest-jackson")
    implementation("io.quarkus:quarkus-rest-client-jackson")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-config-yaml")
    implementation("io.quarkus:quarkus-hibernate-orm-panache")
    implementation("io.quarkus:quarkus-jdbc-postgresql")
    implementation("io.quarkus:quarkus-flyway")
    implementation("io.quarkus:quarkus-hibernate-validator")
    implementation("io.hypersistence:hypersistence-utils-hibernate-71:3.12.0")

    implementation("io.quarkus:quarkus-smallrye-jwt")
    implementation("io.quarkus:quarkus-smallrye-jwt-build")

    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:$lombokMapstructVersion")

    testImplementation("io.quarkus:quarkus-test-security-jwt")
    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("org.assertj:assertj-core:3.25.1")
    testImplementation("io.quarkus:quarkus-junit5-mockito")
}

group = "com.agora"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
    jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")
}

tasks.withType<JavaCompile> {
    dependsOn("spotlessApply")
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

spotless {
    java {
        // Basic formatting without AST parsing (workaround for Java 25 compatibility)
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }

    kotlinGradle {
        ktlint("1.1.1")
        trimTrailingWhitespace()
        endWithNewline()
    }
}

// Format task hooks
tasks.register("formatCode") {
    dependsOn("spotlessApply")
    description = "Format code using Spotless"
}

tasks.register("checkFormat") {
    dependsOn("spotlessCheck")
    description = "Check code formatting without applying changes"
}

// JaCoCo code coverage configuration
jacoco {
    toolVersion = "0.8.14"
}

tasks.test {
    finalizedBy("jacocoTestReport")
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/test/html"))
    }
}
