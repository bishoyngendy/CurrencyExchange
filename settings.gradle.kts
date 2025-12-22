pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
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

rootProject.name = "CurrencyExchange"
include(":app")

// Core modules
include(":core:designsystem")
include(":core:config")
include(":core:network")
include(":core:dispatcher")
include(":core:mvi")

// Feature modules
include(":feature:currency:api")
include(":feature:currency:impl")
 