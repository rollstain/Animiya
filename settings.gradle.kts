pluginManagement {
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
    }
}

rootProject.name = "Animiya"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// core
include(":core_utils")
include(":core_network:api")
include(":core_network:impl")

// data
include(":data_anime:api")
include(":data_anime:impl")

// ui
include(":uikit")

// feature
include(":feature_catalog")
include(":feature_release")

// app
include(":composeApp")
include(":androidApp")
