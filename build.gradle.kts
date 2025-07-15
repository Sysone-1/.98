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
}

application {
    mainClass.set("org.example.Main")

}

javafx {
    version = javafxVersion
    modules = listOf("javafx.controls", "javafx.fxml")
}
