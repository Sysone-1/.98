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

    // Medusa 게이지 차트 라이브러리
    implementation("eu.hansolo:medusa:21.0.9")
}

application {
    mainClass.set("com.sysone.ogamza.Main")

}

javafx {
    version = javafxVersion
    modules = listOf("javafx.controls", "javafx.fxml")
}
