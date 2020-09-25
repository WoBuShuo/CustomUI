package com.example.compiler;

import com.example.annotation.Post;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
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
import javax.tools.Diagnostic;

public class Processor extends AbstractProcessor {
    private HashMap<String, List<Element>> annotationClassMap = new HashMap<>();
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();

    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        // 准备在gradle的控制台打印信息
        Messager messager = processingEnv.getMessager();
        messager.printMessage(Diagnostic.Kind.NOTE, "start: --------------");




//        //        ClassName map=ClassName.get("java.util","HashMap");
//        ClassName map = ClassName.get("java.util", "ArrayList");
//
//
//        MethodSpec.Builder getData = MethodSpec.methodBuilder("getData")
//                .returns(map)
//                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
//                .addStatement("$T map=new $T<>()", map);

        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Post.class);
        for (Element element : elements) {
            Post post = element.getAnnotation(Post.class);

            String name = element.getSimpleName().toString();

//            getData.addStatement("map.put($S,$)",post.url());
//            getData.addStatement("map.add($S)", post.url());
            messager.printMessage(Diagnostic.Kind.NOTE,  name+ " --> " + post.url());
        }
//        getData.addStatement("return map");
//
//
//        TypeSpec.Builder statisticsUtil = TypeSpec.classBuilder("StatisticsUtil")
//                .addModifiers(Modifier.PUBLIC)
//                .addMethod(getData.build());


//        //将类写入文件中
//        try {
//            JavaFile.builder("com.example.compiler",
//                    statisticsUtil.build())
//                    .build()
//                    .writeTo(filer);
//        } catch (IOException e) {
//            messager.printMessage(Diagnostic.Kind.ERROR, e.toString());
//        }




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
}