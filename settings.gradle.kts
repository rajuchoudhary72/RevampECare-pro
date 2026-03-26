import java.net.URI

include(":feature:student_id")


include(":feature:notice")


include(":feature:profile")


include(":feature:setting")


include(":feature:report")


include(":feature:fee")


include(":feature:ebook")


include(":feature:library")


include(":feature:libary")


include(":feature:menu")


include(":feature:update_record")


include(":feature:gallery")


include(":feature:announcement")





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
include(":core:location")
include(":core:download")

include(":feature:onboarding")
include(":feature:schoolcode")
include(":feature:login")
include(":feature:homeselection")
include(":feature:syllabus")
include(":feature:timetable")
include(":feature:calendar")
include(":feature:survey")
include(":feature:splash")
include(":feature:dashboard")
include(":feature:docviewer")
include(":feature:testingmenu")
include(":feature:leave")
include(":feature:feed")
include(":feature:assignment")
include(":feature:questionner")
include(":feature:home")
include(":feature:studentprofile")
include(":feature:discipline")
include(":feature:taskmanger")
include(":feature:marksmanager")
include(":feature:knowyourteacher")
include(":feature:classteacher")
include(":feature:staffprofile")
include(":feature:studentprofile")
include(":feature:message")
include(":feature:transport-att")
include(":feature:smsdailyconsumption")
include(":feature:conversationreport")
include(":feature:globalsearch")
include(":feature:questionpaper")
include(":core:navigation")
include(":core:mylibrary")


