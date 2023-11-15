import io.papermc.paperweight.util.constants.PAPERCLIP_CONFIG

plugins {
    java
    `maven-publish`
    id("net.linguica.maven-settings") version "0.5"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.papermc.paperweight.patcher") version "1.5.9"
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/") {
        content { onlyForConfigurations(PAPERCLIP_CONFIG) }
    }
}

dependencies {
    remapper("net.fabricmc:tiny-remapper:0.8.6:fat")
    decompiler("net.minecraftforge:forgeflower:2.0.627.2")
    paperclip("io.papermc:paperclip:3.0.3")
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "org.gradle.maven-publish")
    apply(plugin = "net.linguica.maven-settings")

    java {
        toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://ci.emc.gs/nexus/content/groups/aikar/")
        maven("https://repo.aikar.co/content/groups/aikar")
        maven("https://repo.md-5.net/content/repositories/releases/")
        maven("https://hub.spigotmc.org/nexus/content/groups/public/")
        maven("https://jitpack.io")
    }

    publishing {

        publications {

            create<MavenPublication>("shadow") {

                shadow{
                    component(this@create)
                }

                repositories {
                    fyre()
                }

            }

        }

    }
}

paperweight {
    serverProject.set(project(":pufferfish-server"))

    remapRepo.set("https://maven.fabricmc.net/")
    decompileRepo.set("https://files.minecraftforge.net/maven/")

    usePaperUpstream(providers.gradleProperty("paperRef")) {
        withPaperPatcher {
            apiPatchDir.set(layout.projectDirectory.dir("patches/api"))
            serverPatchDir.set(layout.projectDirectory.dir("patches/server"))

            apiOutputDir.set(layout.projectDirectory.dir("pufferfish-api"))
            serverOutputDir.set(layout.projectDirectory.dir("pufferfish-server"))
        }
    }
}

fun RepositoryHandler.fyre() {
    maven {
        name = "fyre"
        url = uri("https://maven.pkg.jetbrains.space/fyre/p/fyre-games/maven")
        applyCredentials()
    }
}

fun MavenArtifactRepository.applyCredentials() {

    //TODO ehh
    if (System.getenv().containsKey("JB_SPACE_CLIENT_ID") && System.getenv().containsKey("JB_SPACE_CLIENT_SECRET")) {
        credentials {
            // Automation has a special account for authentication in Space
            // account credentials are accessible via env vars
            username = System.getenv()["JB_SPACE_CLIENT_ID"]
            password = System.getenv()["JB_SPACE_CLIENT_SECRET"]
        }
    }

}
