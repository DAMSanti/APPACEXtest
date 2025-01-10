pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://maven.pkg.github.com/microsoft/surface-duo-sdk") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "AppACEX"
include(":app")
 