package com.github.tobolak.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class ExampleGroovyPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.with {
            allprojects {
                repositories {
                    mavenCentral()
                }

                apply plugin: 'java'

                dependencies {
                    testCompile 'junit:junit:4.12'
                }
            }
        }
    }
}
