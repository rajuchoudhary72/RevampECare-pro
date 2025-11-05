import java.net.URI

include(":feature:splash")


include(":feature:dashboard")


include(":core:location")


pluginManagement {
    includeBuild("build-logic")
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
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":app")

include(":core:designsystem")
include(":core:ui")
include(":core:network")
include(":core:domain")
include(":core:data")
include(":core:database")

include(":feature:onboarding")
include(":feature:schoolcode")
include(":feature:login")
include(":feature:homeselection")


