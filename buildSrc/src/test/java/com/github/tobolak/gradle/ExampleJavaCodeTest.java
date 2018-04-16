package com.github.tobolak.gradle;

import groovy.lang.Closure;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class ExampleJavaCodeTest {

    @Test
    public void shouldFilter() throws Exception {
        List<String> result = nonNulls(Arrays.asList("x", null, "y"));

        assertThat(result, equalTo(Arrays.asList("x", "y")));
    }

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
}
