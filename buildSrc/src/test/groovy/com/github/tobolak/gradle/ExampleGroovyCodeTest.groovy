package com.github.tobolak.gradle

import org.junit.Test

import static org.hamcrest.CoreMatchers.equalTo
import static org.junit.Assert.assertThat

class ExampleGroovyCodeTest {

    @Test
    void shouldFilter() {
        def result = nonNulls(["x", null, "y"])

        assertThat result, equalTo(["x", "y"])
    }

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
}
