plugins { java }

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://libraries.minecraft.net/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.13-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:21.0.1")
    compileOnly("com.mojang:authlib:2.3.31")
    compileOnly("com.mojang:datafixerupper:4.0.26")
    compileOnly(files("D:\\Spigots\\spigots\\spigot-1.17.1.jar"))
}

group = "com.azerusteam"
version = "1.1.0"
