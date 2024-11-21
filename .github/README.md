# 🧩 MinecraftRemapperGradle

**MinecraftRemapperGradle** is a [Gradle](https://gradle.com/) plugin that allows you to easily code with Minecraft
client and server sources.

✨ **Features**

- Automatic remapping of classes at compile time
- Support all versions greater than or equal to 1.14.4
- Support for both client and server
- Really fast and lightweight (and cache)

## ⚙️ How to use

Below is a configuration of the plugin for Gradle Groovy.
You can find a concrete example of use on
this [other project](https://github.com/YvanMazy/RemotedMinecraft/blob/master/example/build.gradle).

Latest version: ![Release](https://jitpack.io/v/YvanMazy/MinecraftRemapperGradle.svg)
```groovy
buildscript() {
    repositories {
        maven {
            url 'https://jitpack.io'
        }
    }
    dependencies {
        classpath 'com.github.YvanMazy.MinecraftRemapperGradle:MinecraftRemapperGradle:<PLUGIN VERSION>'
    }
}

plugins {
    // your plugins section
    // ...
}

apply plugin: 'be.yvanmazy.minecraftremapper.gradle'

// Your gradle script
// ...

minecraftRemapper {
    // Minecraft version (MANDATORY)
    version = "1.21.3"
    // CLIENT or SERVER (MANDATORY)
    directionType = 'CLIENT'
    // Path to your Minecraft installation (OPTIONAL)
    // Default value: Gradle user home directory
    homePath = file('myHomeDirectory')
    // Set to false if you don't want to remap classes at compile time
    // Default value: true
    remapOnCompile = true
    // Set to false if you don't want to cancel compile cache
    // This avoids the compilation UP-TO-DATE that prevents remapping.
    // Default value: true
    cancelCompileCache = true
    // Set to true if you want the sources to be added to the module's dependencies.
    // Default value: true
    includeDependency = true
    // Set to true if you want the libraries used in the game to be added to the module's dependencies.
    // Default value: false
    includeLibrariesDependency = false
    // List of configurations to add the sources and libraries to.
    // Important to modify if you only want to use the plugin for testing.
    // Default value: ['compileOnly']
    dependenciesConfigurations = ['compileOnly']
}
```

## ❓ Why ?

This plugin was originally designed to work with [RemotedMinecraft](https://github.com/YvanMazy/RemotedMinecraft), where
the plugin is used to write the agent code directly from the same module.
Technically, the plugin can be used for any program that requires using original game classes.