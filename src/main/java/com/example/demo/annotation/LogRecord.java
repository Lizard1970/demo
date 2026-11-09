package com.example.demo.annotation;

import java.lang.annotation.*;
@Retention(RetentionPolicy.RUNTIME)

@Target(ElementType.METHOD)
@Documented
public @interface LogRecord {
    String value() default "";
}
