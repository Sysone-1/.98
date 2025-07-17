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
    implementation("eu.hansolo:Medusa:11.7")
}


application {
    mainClass.set("com.sysone.ogamza.Main")

}

javafx {
    version = javafxVersion
    modules = listOf("javafx.controls", "javafx.fxml")
}
