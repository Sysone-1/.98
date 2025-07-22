plugins {
    id("application")
    id("org.openjfx.javafxplugin") version "0.0.14"
}

val javafxVersion = "21"

repositories {
    mavenCentral()
    // 캘린더 API
    maven {
        url = uri("https://jitpack.io")
        mavenContent {
            snapshotsOnly()
        }
    }
}

dependencies {
    implementation("org.openjfx:javafx-controls:$javafxVersion")
    implementation("org.openjfx:javafx-fxml:$javafxVersion")
    implementation( "com.zaxxer:HikariCP:5.1.0")
    implementation ("com.oracle.database.jdbc:ojdbc11-production:21.18.0.0")

    implementation ("org.controlsfx:controlsfx:11.2.0")

    // Log4j
    implementation("org.apache.logging.log4j:log4j-api:2.23.1") // Use the latest stable version
    runtimeOnly("org.apache.logging.log4j:log4j-core:2.23.1") // Use the latest stable version
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.23.1") // Use the latest stable version

    // lombok
    compileOnly ("org.projectlombok:lombok:1.18.32")
    annotationProcessor ("org.projectlombok:lombok:1.18.32")

    // gpt
    implementation ("com.fasterxml.jackson.core:jackson-databind:2.17.0")

}


application {
    mainClass.set("com.sysone.ogamza.Main")

}

javafx {
    version = javafxVersion
    modules = listOf("javafx.controls", "javafx.fxml" )
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, "seconds")
}