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
    version = "26.2"
    // CLIENT or SERVER (MANDATORY)
    directionType = 'CLIENT'
    // Path to your Minecraft installation (OPTIONAL)
    // Default value: Gradle user home directory
    homePath = file('myHomeDirectory')
    // Set to false if you don't want to remap classes at compile time
    // Has no effect on versions that are not obfuscated, see the note above.
    // Default value: true
    remapOnCompile = true
    // Set to false if you don't want to cancel compile cache
    // This avoids the compilation UP-TO-DATE that prevents remapping.
    // Default value: true
    cancelCompileCache = true
    // Set to true if you want the remapped jar to be added to the module's dependencies.
    // Default value: true
    includeRemappedJarDependency = true
    // Set to true if you want the original version jar to be added to the module's dependencies.
    // Default value: false
    includeRawJarDependency = false
    // Set to true if you want the libraries used in the game to be added to the module's dependencies.
    // Default value: false
    includeLibrariesDependency = false
    // List of configurations to add the remapped jar to.
    // Important to modify if you only want to use the plugin for testing.
    // Default value: ['compileOnly']
    remappedJarDependenciesConfigurations = ['compileOnly']
    // List of configurations to add the original version jar to.
    // Default value: ['compileOnly']
    rawJarDependenciesConfigurations = ['compileOnly']
    // List of configurations to add the game libraries to.
    // Default value: ['compileOnly']
    librariesDependenciesConfigurations = ['compileOnly']
}
```

Each kind of dependency has its own list of configurations, so they can be wired independently. For example, to
compile against the remapped jar but run against the original one:

```groovy
minecraftRemapper {
    includeRawJarDependency = true
    includeLibrariesDependency = true
    remappedJarDependenciesConfigurations = ['integrationTestCompileOnly']
    librariesDependenciesConfigurations = ['integrationTestCompileOnly']
    rawJarDependenciesConfigurations = ['integrationTestRuntimeOnly']
}
```

## ❓ Why ?

This plugin was originally designed to work with [RemotedMinecraft](https://github.com/YvanMazy/RemotedMinecraft), where
the plugin is used to write the agent code directly from the same module.
Technically, the plugin can be used for any program that requires using original game classes.