package com.github.tobolak.gradle

import org.junit.Test

import static org.hamcrest.CoreMatchers.equalTo
import static org.junit.Assert.assertThat

class MetaClassesTest {

    @Test
    void addMethodToMetaClass() {
        def world = 'World'

        world.metaClass.hello = { "Hello, $delegate!".toString() }

        assertThat world.hello(), equalTo("Hello, World!")
    }

    @Test
    void changeMethodViaMetaClass() {
        def object = new SomeClass()

        def oldMethod = object.metaClass.getMetaMethod('aMethod')
        object.metaClass.aMethod = { arg -> oldMethod.invoke(object, 'World') }

        assertThat object.aMethod('x'), equalTo('Hello World')
    }

    class SomeClass {
        String aMethod(arg) {
            return "Hello $arg"
        }
    }
}
