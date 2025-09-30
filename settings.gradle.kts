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
        mavenLocal()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        mavenLocal()

        maven {
            name = "GitHubPackagesFitnessPro"
            url = uri("https://maven.pkg.github.com/nikolasluiz123/FitnessProService")
            credentials {
                username = providers.gradleProperty("gpr.user").orNull ?: System.getenv("GITHUB_ACTOR")
                password = providers.gradleProperty("gpr.key").orNull ?: System.getenv("PACKAGES_TOKEN")
            }
        }

        maven {
            name = "GitHubPackagesAndroidLibs"
            url = uri("https://maven.pkg.github.com/nikolasluiz123/AndroidLibs")
            credentials {
                username = providers.gradleProperty("gpr.user").orNull ?: System.getenv("GITHUB_ACTOR")
                password = providers.gradleProperty("gpr.key").orNull ?: System.getenv("PACKAGES_TOKEN")
            }
        }
    }
}

rootProject.name = "FitnessPro"
include(":app")
include(":fitnesspro-compose-components")
include(":fitnesspro-core")
include(":fitnesspro-model")
include(":fitnesspro-local-data-access")
include(":fitnesspro-service-data-access")
include(":fitnesspro-common-module")
include(":fitnesspro-scheduler-module")
include(":fitnesspro-workout-module")
include(":fitnesspro-firebase-api")
include(":fitnesspro-health-connect")
