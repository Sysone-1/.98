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

    // Log4j
    implementation("org.apache.logging.log4j:log4j-api:2.23.1") // Use the latest stable version
    runtimeOnly("org.apache.logging.log4j:log4j-core:2.23.1") // Use the latest stable version
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.23.1") // Use the latest stable version

    // calendar
    implementation (files("libs/view-11.12.7.jar"))
    implementation("org.controlsfx:controlsfx:11.1.2")
    implementation("org.kordamp.ikonli:ikonli-core:12.3.1")
    implementation("org.kordamp.ikonli:ikonli-javafx:12.3.1")
    implementation("org.kordamp.ikonli:ikonli-fontawesome-pack:12.3.1")
    // lombok
    compileOnly ("org.projectlombok:lombok:1.18.32")
    annotationProcessor ("org.projectlombok:lombok:1.18.32")
}


application {
    mainClass.set("com.sysone.ogamza.Main")

}

javafx {
    version = javafxVersion
    modules = listOf("javafx.controls", "javafx.fxml")
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, "seconds")
}