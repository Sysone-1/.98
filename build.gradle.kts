plugins {
    id("application")
    id("org.openjfx.javafxplugin") version "0.0.14"
}

val javafxVersion = "21"

repositories {
    mavenCentral()

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

}


application {
    mainClass.set("com.sysone.ogamza.Main")

}

javafx {
    version = javafxVersion
    modules = listOf("javafx.controls", "javafx.fxml")
}
