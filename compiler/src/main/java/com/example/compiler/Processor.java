package com.example.compiler;

import com.example.annotation.Post;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

public class Processor extends AbstractProcessor {
    private List<Element> annotationList = new ArrayList<>();
    private Filer filer;
    private Messager messager;
    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        // 准备在gradle的控制台打印信息
        messager.printMessage(Diagnostic.Kind.NOTE, "start: --------------");
        if (!roundEnvironment.processingOver()) {
            buildAnnotatedElement(roundEnvironment, Post.class);
        }else{
            //        ClassName map=ClassName.get("java.util","HashMap");
            ClassName map = ClassName.get("java.util", "ArrayList");
            ClassName string = ClassName.get("java.lang", "String");
            TypeName listOfString = ParameterizedTypeName.get(map, string);
            MethodSpec.Builder getData = MethodSpec.methodBuilder("getData")
                    .returns(listOfString)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addStatement("$T map=new $T<>()", listOfString, map);

//            for (Element element : roundEnvironment.getElementsAnnotatedWith(Post.class)) {
            for (Element element : ElementFilter.fieldsIn(annotationList)) {
                Post post = element.getAnnotation(Post.class);
                if (post != null) {
                    String name = element.getSimpleName().toString();

//            getData.addStatement("map.put($S,$)",post.url());
                    getData.addStatement("map.add($N)", "\"" + (String) post.url() + "\"");
                    messager.printMessage(Diagnostic.Kind.NOTE, name + " --> " + post.url());
                }else{
                    messager.printMessage(Diagnostic.Kind.NOTE,"post     is null");
                }
            }
            getData.addStatement("return map");


            TypeSpec.Builder statisticsUtil = TypeSpec.classBuilder("StatisticsUtil")
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(getData.build());


            //将类写入文件中
            try {
                JavaFile.builder("com.example.newproject",
                        statisticsUtil.build())
                        .build()
                        .writeTo(filer);
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.ERROR, e.toString());
            }

        }
        return true;
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> set = new HashSet<>();
        set.add(Post.class.getCanonicalName());
        return set;
    }

    private void buildAnnotatedElement(RoundEnvironment roundEnv, Class<? extends Annotation> clazz) {
        annotationList.addAll(roundEnv.getElementsAnnotatedWith(clazz));
    }


}