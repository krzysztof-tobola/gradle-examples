layout: true
class: top

---

class: center middle

.logo[![Gradle](img/gradle-logo.png)]

## Survival guide for Java developers

---
class: center middle

.half[![](img/flat.png)]

When you want to build your project…

---

## Maven

* fast and *easy* to start
* not so *easy* when you don’t want it the standard way
* not *simple* (*complex* lifecycle, phases, goals, etc.)

.half[![](img/ikea.jpg)]

---

## Ant

* you can do everything
* relatively *simple* tool that can be *difficult* to use
* extending is not very *easy*

.half[![](img/carpenter.jpg)]

---

## Gradle

* you can do everything
* relatively *simple* model
* extending via plugins is also *simple*

.half[![x](img/furniture.jpg)]


---

## A balance between Maven and Ant

* relatively unopinionated (while Maven imposes standards)
* flexible - any project structure is possible
* scriptable - no extra tooling needed, everything is Groovy
* extensible - many plugins available, *easy*  to create new ones
* the scripts can themselves become complex
* builds can become *difficult* to understand

---


## Basic concepts

* project
* project dependencies and repositories
* tasks
* lifecycle
* plugins

---

## Basic concepts
### Project

* tree-like structure - *settings.gradle*
  * root project
  * child projects
  * sub-projects
  * all projects
* paths
  * separated by ":"
  * absolute if starts with the separator
  * else relative to the current project
  
---

## Basic concepts
### Project
  
.half[![x](img/project-tree.png)]

---

## Basic concepts
### Project dependencies

* grouped into `configurations`
* types of configurations are determined by plugins, ie. Java plugin:
  * `compile`
  * `testCompile`
  * ...
* deterministic conflict resolution

---

## Basic concepts
### Project dependencies

```groovy
dependencies {
    compile project(':another-project')
    
    compile 'args4j:args4j:2.32'
    
    runtime 'org.log:simple:1.7.12'
    
    testCompile 'com.ab:cd:1.16.0'
}
```

---

## Basic concepts
### Repositories

* different types (@see `RepositoryHandler`)
* `mavenCentral()`, `jcenter()`, …
* custom repos (maven, ivy, local directories)

---

## Basic concepts
### Repositories

```groovy
repositories.ivy {
    url 'http://central.maven.org/maven2/'
    layout('pattern') {
        artifact '[organisation]/[module]/[revision]/[module]-[revision].[ext]'
    }
}

dependencies {
    testCompile 'junit:junit:4.12@jar'
    //...
}
```

---

## Basic concepts
### Tasks

* allow the build user to indicate things to execute
* defined directly in the script or in a plugin
* can depend on one another to control execution flow

---

## Basic concepts
### Tasks - DSL syntax

```groovy
task task1(type: Zip) {
//...
}

task task2 {
//...
}
```

---

## Basic concepts
### Tasks - standard Groovy syntax

```groovy
project.task('task3') {
//...
}

project.task('task4', type: Zip) {
//...
}

```

---

## Basic concepts
### Task dependencies

```groovy
task3.dependsOn task4

tasks.task3.dependsOn 'task4'

tasks.getByName('task3') dependsOn 'task4'
```

---

## Basic concepts
### Project Lifecycle

* initialization
    * determine if single or structure of multiproject
* configuration
    * project: `beforeEvaluate`, `afterEvaluate`
    * `gradle.afterProject` … (`Gradle` interface)
* execution
    * run specified tasks

---

## Basic concepts
### Project Lifecycle
    
```groovy
println 'Configuration 1'

project.afterEvaluate {
    println 'Configuration 3'
}

task someTask {
    println 'Configuration 2'
} doFirst {
    println 'Execution 1'
} doLast {
    println 'Execution 2'
}

```    

---

## Basic concepts
### Using core plugins

```groovy
apply plugin: 'java'

```

---

## Basic concepts
### Using 3rd party plugins via classic syntax

```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'net.researchgate:gradle-release:2.6.0'
    }
}

apply plugin: 'net.researchgate.release'

```

---

## Basic concepts
### Using 3rd party plugins via DSL

```groovy
plugins {
    id "org.jetbrains.intellij" version "0.2.11"
}
```


---

## Basic concepts
### Using plugins from script

```groovy
apply from: 'other.gradle'
```

---

## Basic concepts
### Using plugin classes

```groovy
apply plugin: net.researchgate.release.ReleasePlugin

apply plugin: com.example.ExamplePlugin
```

---

## Why does my build script look like JSON?

---

## Why does my build script look like JSON?

.half[![x](img/magic.jpg)]

---

## Groovy - dynamic Java?

* Most of the time, Java 7 is valid Groovy


```groovy
public <T> List<T> nonNulls(List<T> list) {
    return filter(list, new Closure<Boolean>(this) {
        @Override
        public Boolean call(Object arg) {
            return arg != null;
        }
    });
}

public <T> List<T> filter(List<T> list, Closure<Boolean> closure) {
    List<T> result = new ArrayList<>();
    for (T item : list) {
        if (closure.call(item)) {
            result.add(item);
        }
    }
    return result;
}
```

* Java 8 lambda syntax won't compile

---

## Groovy - dynamic Java?

* But idiomatic Groovy hardly looks like Java

```groovy
def nonNulls(list) {
    filter(list) { it != null }
}

def filter(list, closure) {
    def result = []
    list.each {
        if (closure(it)) {
            result.add it
        }
    }
    result
}
```

.bottom-right[![x](img/dynamic.jpg)]

---

## Groovy - dynamic Java?
### Method call syntax

* omit brackets and semicolons

```groovy
method(x, y);
``` 


```groovy
method x, y
``` 


---

## Groovy - dynamic Java?
### Method call syntax

* pass map literals   

```groovy
apply(['plugin': 'java'])
```

```groovy
apply plugin: 'java'
``` 

---

## Groovy - dynamic Java?
### Method call syntax

* chain method calls   

```groovy
product 'IDEA'  withVersion '17.0.3' withCode 'IJ17' 
``` 

```groovy
product('IDEA').withVersion('17.0.3').withCode('IJ17')
``` 

---

## Groovy - dynamic Java?
### Method call syntax

* last argument of Closure type   

```groovy
method('x', { action(it) })
``` 

```groovy
method('x') { action(it) }
``` 

---


## Groovy - dynamic Java?
### Closures - literals

```groovy 
{ String x -> action(x) }

{-> action( ) }

{ action(it) }

subprojects { println "configuring project $name"}
```

---

## Groovy - dynamic Java?
### Closures - explicit Closure subclass

```groovy
new Closure(this) {
    @Override
    Object call(Object... args) {
    //...
    }
}
```

---

## Groovy - dynamic Java?
### Closure delegation

* `this` - object of enclosing class
* `owner` - enclosing object (class or closure)
* `delegate` - owner by default, can be changed
* `resolveStrategy` - owner|delegate first|only

---

## Understanding build.gradle

* invoked almost like a `Closure` on `gradle.api.Project`

```groovy
project.with {
//...
}
```

* with DSL task syntax

```groovy
task task1 doLast {
//...
}
```

---

## Understanding build.gradle
### JSON-like constructs...

```groovy
allprojects {


 
        repositories {
        
        
                              mavenCentral()
            
        }
        
        
        apply plugin: 'java'
        dependencies {
        
        
        
                         testCompile "junit:junit:4.12"
                         
        }
        
        
}
```

---

## Understanding build.gradle
### are actually method calls...

```groovy
project.allprojects({



        repositories({
        
        
                              mavenCentral()
            
        })
    
    
        apply([plugin: 'java'])
        dependencies({
        
        
        
                         testCompile("junit:junit:4.12")
                         
        })


})
```

---

## Understanding build.gradle
### invoked on delegates...

```groovy
project.allprojects({



        getDelegate().repositories({
        
        
                getDelegate().mavenCentral()
            
        })
        
        
        getDelegate().apply([plugin: 'java'])
        getDelegate().dependencies({
        
        
                getDelegate()
                        .testCompile("junit:junit:4.12")
            
        })

        
})
```

---

## Understanding build.gradle
### of Groovy Closures...

```groovy
project.allprojects(new Closure(this) {

    Object call(Object... arguments) {
    
        getDelegate().repositories(new Closure(this) {
        
            Object call(Object... args2) {
                getDelegate().mavenCentral()
            }
        })
        
        
        getDelegate().apply([plugin: 'java'])
        getDelegate().dependencies(new Closure(this) {
        
            Object call(Object... args2) {
                getDelegate()
                        .testCompile("junit:junit:4.12")
            }
        })
        
    }
})
```

---

## Understanding build.gradle
### without map literals...

```groovy
project.allprojects(new Closure(this) {

    Object call(Object... arguments) {

        getDelegate().repositories(new Closure(this) {
        
            Object call(Object... args2) {
                getDelegate().mavenCentral()
            }
        })
        Map<String, String> options = new HashMap<>()
        options.put("plugin", "java")
        getDelegate().apply(options)
        getDelegate().dependencies(new Closure(this) {
        
            Object call(Object... args2) {
                getDelegate()
                        .testCompile("junit:junit:4.12")
            }
        })
        
    }
})
```

---

## Understanding build.gradle
### with implicit type casts...

```groovy
project.allprojects(new Closure(this) {

    Object call(Object... arguments) {
        Project delegate = (Project) getDelegate();
        delegate.repositories(new Closure(this) {
        
            Object call(Object... args2) {
                ((RepositoryHandler) getDelegate()).mavenCentral();
            }
        });
        Map<String, String> options = new HashMap<>();
        options.put("plugin", "java");
        delegate.apply(options);
        delegate.dependencies(new Closure(this) {
        
            Object call(Object... args2) {
                ((DependencyHandler) getDelegate())
                        .testCompile("junit:junit:4.12");
            }
        });
        
    }
});
```

---

## Understanding build.gradle
###but DependencyHandler has no testCompile method?!

```groovy
public interface DependencyHandler {
    Dependency add(String configurationName, Object dependencyNotation);
    Dependency add(String configurationName, Object dependencyNotation, 
        Closure configureClosure);
    Dependency create(Object dependencyNotation);
    Dependency create(Object dependencyNotation, Closure configureClosure);
    Dependency module(Object notation);
    Dependency module(Object notation, Closure configureClosure);
    Dependency project(Map<String, ?> notation);
    Dependency gradleApi();
    Dependency gradleTestKit();
    Dependency localGroovy();
    ComponentMetadataHandler getComponents();
    void components(Action<? super ComponentMetadataHandler> configureAction);
    ComponentModuleMetadataHandler getModules();
    void modules(Action<? super ComponentModuleMetadataHandler> configureAction);
    ArtifactResolutionQuery createArtifactResolutionQuery();
    AttributesSchema attributesSchema(
        Action<? super AttributesSchema> configureAction);
    AttributesSchema getAttributesSchema();
    void registerTransform(Action<? super VariantTransform> registrationAction);
}
```

---

## Understanding build.gradle
###but DependencyHandler has no testCompile method?!

.half[![x](img/wtf.jpg)]

---

## Understanding build.gradle
### Or maybe it does…

```java
public class DefaultDependencyHandler 
    extends GroovyObjectSupport 
    implements DependencyHandler, MethodMixIn {
    //...    
}

/**
 * A decorated domain object type may optionally 
 * implement this interface to dynamically expose methods 
 * in addition to those declared statically on the type.
 *
 * Note that when a type implements this interface, 
 * dynamic Groovy dispatch will not be used to 
 * discover opaque methods. That is, methods such 
 * as methodMissing() will be ignored.
 */
public interface MethodMixIn {
    MethodAccess getAdditionalMethods();
}
```

---

## Understanding build.gradle
### …have methods added in runtime 

```groovy
@Test
void addMethodToMetaClass() {
    def world = 'World'

    world.metaClass.hello = { "Hello, $delegate!".toString() }

    assertThat world.hello(), equalTo("Hello, World!")
}
```

---


## Understanding build.gradle
### No magic - valid Java

```groovy
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
```


---

## Understanding build.gradle
### And groovy again

```groovy
allprojects {
    repositories {
        mavenCentral()
    }

    apply plugin: 'java'

    dependencies {
        testCompile 'junit:junit:4.12'
    }
}
```

.bottom-right[![x](img/cute.png)]

---

## Modularizing the build

* `buildSrc` sub-project
* included automatically in the build classpath
* has access to Gradle and Groovy API
* it’s a full featured project - can have `build.gradle`, tests etc

---

## Modularizing the build
### Plugins

* implement `org.gradle.api.Plugin<Project>` 
* `apply(Project)` method
* applied in the scripts (`apply plugin: MyPlugin`)
* full access to project
* can create tasks, execute code, manipulate the model
* can be configured via extensions

---

## Modularizing the build
### Extensions

* the simplest way of making plugins configurable
    * define the extension class
    * create extension instance (in the plugin)
    * configure (in the build script)
    * use the values (in the plugin)

---

## Modularizing the build
### Extensions

The goal - configure the plugin in a script

```groovy
apply plugin: ExamplePlugin

appsExtension {
    app 'idea', '2018.1'
    app 'eclipse', 'oxygen'
}
```

---

## Modularizing the build
### Extensions
Create the extension class 

```groovy
class AppsExtension {
    def apps = []

    def app(name, version) {
        apps += "$name-$version"
    }
}
```

---

## Modularizing the build
### Extensions
Create the extension instance and use it from the plugin

```groovy
class ExamplePlugin implements Plugin<Project> {
    @Override
    void apply(Project target) {
        target.with {
            extensions.create("appsExtension", AppsExtension)

            afterEvaluate {
                logger.quiet "After evaluate, apps = $appsExtension.apps"
            }
        }
    }
}
```

---

## Modularizing the build
### Extensions - containers

The goal - a cleaner syntax for multiple named items

```groovy
apply plugin: ExamplePlugin

appsExtension {
    idea { version = '2018.1' }
    eclipse { version = 'oxygen' }
}
```

---

## Modularizing the build
### Extensions - containers
Create the single item class 

```groovy
class App {
    def name, version
    
    App(name) { this.name = name }
    
    String toString() { "$name-$version" }
}
```

---

## Modularizing the build
### Extensions - containers
Create the container and use it from the plugin

```groovy
class ExamplePlugin implements Plugin<Project> {
    @Override
    void apply(Project target) {
        target.with {
            extensions.appsExtension = container(App)

            afterEvaluate {
                logger.quiet "After evaluate, apps = $appsExtension"
            }
        }
    }
}
```

---

## Summary

---

### Gradle is code 

## so

# DRY SOLID KISS

---

## There is more…

* https://gradle.org/docs/
* http://groovy-lang.org/documentation.html
* Kotlin DSL

---

## Thanks!

.half[![x](img/questions.jpg)]

