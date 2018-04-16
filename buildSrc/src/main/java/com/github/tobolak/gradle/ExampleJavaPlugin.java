package com.github.tobolak.gradle;

import groovy.lang.Closure;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.artifacts.dsl.RepositoryHandler;

import java.util.HashMap;
import java.util.Map;

public class ExampleJavaPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.allprojects(new Closure(this) {
            @Override
            public Object call(Object... arguments) {
                Project delegate = (Project) getDelegate();
                delegate.repositories(new Closure(this) {
                    @Override
                    public Object call(Object... args2) {
                        return ((RepositoryHandler) getDelegate()).mavenCentral();
                    }
                });
                Map<String, String> options = new HashMap<>();
                options.put("plugin", "java");
                delegate.apply(options);
                delegate.dependencies(new Closure(this) {
                    @Override
                    public Object call(Object... args2) {
                        return ((DependencyHandler) getDelegate())
                                .add("testCompile", "junit:junit:4.12");
                    }
                });
                return null;
            }
        });
    }
}
