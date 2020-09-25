package com.example.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import kotlin.random.Random;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Post {
    public String url() default "";
}
