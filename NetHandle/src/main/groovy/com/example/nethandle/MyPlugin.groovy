package com.example.nethandle

import org.gradle.api.Plugin
import org.gradle.api.Project


public class MyPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        System.out.println("------------------开始----------------------");
        System.out.println("这是我们的自定义插件!");
        System.out.println("------------------结束----------------------->");

        def android

    }
}