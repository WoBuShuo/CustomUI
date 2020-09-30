package com.example.compiler;

import com.example.annotation.Post;

import com.google.auto.service.AutoService;
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
import javax.annotation.processing.Completion;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class MyProcessor extends AbstractProcessor {

    Messager mMessager;
    Filer mFiler;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        mMessager = processingEnv.getMessager();
        mFiler = processingEnv.getFiler();
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        // 打印日志
        mMessager.printMessage(Diagnostic.Kind.NOTE, "process start");
        Map<String, List<String>> collectInfos = new HashMap<>();
        for (TypeElement annotation : annotations) {
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(annotation);
            for (Element element : elements) {
                // 检查element是否符合我们定义的规范
//                if (!checkValid(element)){
//                    mMessager.printMessage(Diagnostic.Kind.NOTE, "checkValid not pass");
//                    return false;
//                }else {
                if (element.getAnnotation(Post.class) != null) {
                    ExecutableElement executableElement = (ExecutableElement) element;
                    // 获取被注解的方法所在的类
                    TypeElement typeElement = (TypeElement) executableElement.getEnclosingElement();
                    // 获取类的全名，包括包名
                    String classFullName = typeElement.getQualifiedName().toString();
                    // 被注解的方法的名字
                    String methodName = executableElement.getSimpleName().toString();

                    String parameter = executableElement.getAnnotation(Post.class).url();

                    mMessager.printMessage(Diagnostic.Kind.NOTE, parameter);
                    List<String> methods = collectInfos.get(classFullName);
                    if (methods == null) {
                        methods = new ArrayList<>();
                        collectInfos.put(classFullName, methods);
                    }
                    methods.add(parameter);
                }

//                }
            }
        }

        for (Map.Entry<String, List<String>> entry : collectInfos.entrySet()) {
            mMessager.printMessage(Diagnostic.Kind.NOTE, entry.getKey());
            // 生成java源文件
            createJavaFile(entry.getValue());
        }

        return true;
    }


    private void createJavaFile(List<String> list) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, "annotationList: "+list.size());
        //        ClassName map=ClassName.get("java.util","HashMap");
        ClassName map = ClassName.get("java.util", "ArrayList");
        ClassName string = ClassName.get("java.lang", "String");
        TypeName listOfString = ParameterizedTypeName.get(map, string);
        MethodSpec.Builder getData = MethodSpec.methodBuilder("getData")
                .returns(listOfString)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addStatement("$T map=new $T<>()", listOfString, map);

        for (int i = 0; i < list.size(); i++) {
            getData.addStatement("map.add($N)", "\"" + (String)list.get(i) + "\"");
        }

        getData.addStatement("return map");


        TypeSpec.Builder statisticsUtil = TypeSpec.classBuilder("StatisticsUtil")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(getData.build());


        //将类写入文件中
        try {
            JavaFile.builder("com.example.annproject",
                    statisticsUtil.build())
                    .build()
                    .writeTo(mFiler);
        } catch (IOException e) {
            mMessager.printMessage(Diagnostic.Kind.ERROR, e.toString());
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportAnnotationTypes = new HashSet<>();
        supportAnnotationTypes.add(Post.class.getCanonicalName());
        return supportAnnotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}