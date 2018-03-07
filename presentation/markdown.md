layout: true
class: top

---

class: center middle

.logo[![Gradle](img/gradle-logo.png)]

## Survival guide for Java developers

---
class: center middle

.full[![](img/flat.png)]

When you want to build your project…

---

## Maven

* fast and easy to start
* not so easy when you don’t want it the standard way

.half[![](img/ikea.jpg)]

---

## Ant

* you can do everything
* customization requires lot of skill and work

.half[![](img/carpenter.jpg)]

---

## Gradle

* custom made furniture with your guidance

.half[![x](img/furniture.jpg)]


---


## The best of Maven and Ant

* relatively unopinionated (while Maven imposes standards)
* flexible - any project structure is possible
* scriptable - no extra tooling needed, everything is Groovy
* extensible - many plugins available, easy to create new ones

---


## Basic concepts

* project
* project dependencies
* tasks
* plugins

---

## Project

* tree-like structure - *settings.gradle*
  * root project
  * child projects
  * sub-projects
  * all projects
* paths
  * separated by ":"
  * absolute if starts by separator
  * else relative to current
  
---

## Project
  
.half[![x](img/project-tree.png)]

---

## Project dependencies

* grouped into configurations
* types of configs are determined by plugins
* ie Java plugin:
  * compile, testCompile
  * runtime, testRuntime
* deterministic conflict resolution

```groovy
dependencies {
    compile project(':preupgrade')
    compile 'args4j:args4j:2.32'
    testCompile 'com.ab:cd:1.16.0'
    runtime 'org.log:simple:1.7.12'
}
```

---

## Project dependencies - repos

* different types (@see `RepositoryHandler`)
* `mavenCentral()`, `jcenter()`, …
* custom repos (maven, ivy, local directories)



---

## Tasks

* allow the build user to indicate things to execute
* defined directly in the script or in a plugin
* can depend on one another to control execution flow

```groovy                                     
//in script
assemble.dependsOn distZip

//in plugin
project.tasks.getByName('assemble')  dependsOn 'distZip'

```

---

## Project Lifecycle

* initialization
    * determine if single or structure of multiproject
* configuration
    * project: `beforeEvaluate`, `afterEvaluate`
    * `gradle.afterProject` … (`Gradle` interface)
* execution
    * run specified tasks

---

## Project Lifecycle
    
```groovy
println 'Configuration 1'

task someTask() {
    println 'Configuration 2'
} doFirst {
    println 'Execution 1'
} doLast {
    println 'Execution 2'
}

project.afterEvaluate {
    println 'Configuration 3'
}

```    

---

## Using plugins
### core plugins

```groovy
apply plugin: 'java'

```

---

## Using plugins
### 3rd party plugins

'old' syntax

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

Plugin DSL

```groovy
plugins {
    id "org.jetbrains.intellij" version "0.2.11"
}
```


---

## Using plugins
### from script

```groovy
apply from: 'other.gradle'
```

---

## Using plugins
### From class

```groovy
apply plugin: net.researchgate.release.ReleasePlugin
apply plugin: com.example.ExamplePlugin
```

---

## Why my build script looks like JSON?

.half[![x](img/magic.jpg)]

---

## Groovy - dynamic Java

* most of the time, Java 7 is valid Groovy*


```groovy
public void method(Runnable task) {
    whenNotRunning(task, new Closure() {
        Object call() {
            return owner.run("task-" + task);
        }
    });
}
```

***
<sup>*</sup>Java 8 lambda syntax wont compile

---

## Dynamic Java?

But idiomatic Groovy hardly looks like Java

```groovy
def method(task) {
    whenNotRunning(task) {
        run name: "task-${task}"
    }
}
```

.bottom-right[![x](img/dynamic.jpg)]

---

## Method call syntax

* omit braces   

```groovy
method x, y
``` 

```groovy
method(x, y)
``` 

---

## Method call syntax

* pass map literals   

```groovy
apply plugin: 'java'
``` 

```groovy
apply(['plugin': 'java'])
```

---

## Method call syntax

* chain method calls   

```groovy
product 'IDEA'  withVersion '17.0.3' withCode 'IJ17' 
``` 

```groovy
product('IDEA') .withVersion('17.0.3') .withCode('IJ17')
``` 

---

## Method call syntax

* last argument of Closure type   

```groovy
method('x') { action(it) }
``` 

```groovy
method('x', { action(it) })
``` 

---

## Closures

* literals

```groovy 
{ String x -> action(x) }

{-> action( ) }

{ action(it) }

subprojects { println "configuring project $name"}
```


* one can create instance of Closure subclass

TODO example

---

## Closure delegation

* `this` - object of enclosing class
* `owner` - enclosing object (class or closure)
* `delegate` - owner by default, can be changed
* `resolveStrategy` - owner|delegate first|only

---

## Understanding build.gradle

* invoked as instance of gradle.api.Project (?)
* with DSL syntax sugar on top of groovy…
* task syntax
* simplified access to DSL objects
* extensions
* containers
* there is more…

---

## Task syntax

```groovy
task someName(type: Zip)

project.task 'name', type: Zip

someName.dependsOn 'sth'

tasks.getByName('someName') dependsOn 'sth'
```

---

## JSON-like constructs…

```groovy
subprojects {
    apply plugin: 'java'

    dependencies {
        testCompile 'junit:junit:4.12'
    }
}
```

---

## …are method calls

```groovy
subprojects({
    apply(plugin: 'java');

    dependencies({
        testCompile('junit:junit:4.12');
    });
});
```

---

## …and closure delegates

```groovy
subprojects({Project prj ->
    prj.apply(plugin: 'java');

    prj.dependencies({ DependencyHandler dh ->
        dh.testCompile('junit:junit:4.12');
    });
});
```

---

## …but testCompile is not a method?!

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

## …but testCompile is not a method?!

.half[![x](img/wtf.jpg)]

---

## Or is it…

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

## … method added in runtime 

```groovy
@Test
void addMethodToMetaClass() {
    def world = 'World'

    world.metaClass.hello = { "Hello, $delegate!".toString() }

    assertThat world.hello(), equalTo("Hello, World!")
}
```

---

## Less magic - almost Java ;)

```groovy
project.subprojects({Project prj ->
    prj.apply(plugin: 'java');

    prj.dependencies({ DependencyHandler dh ->
        dh.add('testCompile', 'junit:junit:4.12');
    });
});
```


---

## And groovy again

```groovy
subprojects {
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

## Plugins

* implement `org.gradle.api.Plugin<Project>` 
* `apply(Project)` method
* applied in the scripts (`apply plugin: MyPlugin`)
* full access to project
* can create tasks, execute code, manipulate the model
* can be configured via extensions

---

## Extensions

the easiest way of making plugins configurable
* create extension (in the plugin)
* configure (in the build script)
* use the values (in the plugin)

```groovy
TODO generify

extensions.create("someExtension", ApiDiffExtension)

someExtension {
    app 'x', 'y'
}

afterEvaluate {
    println extensions.getByType(ApiDiffExtension).suiteApps
}
```

---

## Extensions - containers

```groovy
class App {
    final String name
    String version
    App(String name) { this.name = name }
}

project.extensions.apps = project.container(App)

apps {
    pc { version = '9.0.4' }
    cc { version = '9.0.3' }
}

afterEvaluate {
    apps.each { println "APP $it.name = $it.version" }
}
```

---

## There is more…

* https://gradle.org/docs/
* http://groovy-lang.org/documentation.html
* Kotlin DSL

---

## Thanks!

.half[![x](img/questions.jpg)]

