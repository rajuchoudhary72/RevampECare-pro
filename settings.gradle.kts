import java.net.URI

pluginManagement {
    includeBuild("build-logic") // Add this line
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = URI("https://jitpack.io") }
    }
}

rootProject.name = "ECareProNewUI"
include(":app")

include(":core:designsystem")

