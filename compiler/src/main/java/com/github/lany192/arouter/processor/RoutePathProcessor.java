package com.github.lany192.arouter.processor;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.github.lany192.arouter.Logger;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * @author Administrator
 */
@AutoService(Processor.class)
public class RoutePathProcessor extends AbstractProcessor {
    private Logger logger;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        logger = new Logger(processingEnv.getMessager());
        logger.info("初始化");
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new LinkedHashSet<>();
        set.add(Route.class.getCanonicalName());
        return set;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Route.class);
        if (elements != null && !elements.isEmpty()) {
            List<FieldSpec> fields = new ArrayList<>();
            for (Element element : elements) {
                Route route = element.getAnnotation(Route.class);
                String path = route.path();
                String fieldName = path.replace("/", "_").toUpperCase();
                //去掉第一个下划线
                fieldName = fieldName.substring(1);
                FieldSpec fieldSpec = FieldSpec
                        .builder(String.class, fieldName, Modifier.STATIC, Modifier.PUBLIC, Modifier.FINAL)
                        .addJavadoc("类位置：{@link " + ClassName.get((TypeElement) element) + "}")
                        .initializer("\"" + path + "\"")
                        .build();
                fields.add(fieldSpec);
            }
            TypeSpec.Builder builder = TypeSpec.classBuilder("RoutePath")
                    .addJavadoc("路径集合,自动生成,请勿编辑!")
                    .addModifiers(Modifier.PUBLIC);
            builder.addFields(fields);
            builder.addField(FieldSpec
                    .builder(String.class, "KEY_ROUTE_PATH", Modifier.STATIC, Modifier.PUBLIC, Modifier.FINAL)
                    .addJavadoc("登录界面需要携带的路径参数key(非界面路径)")
                    .initializer("\"route_path\"")
                    .build());
            JavaFile javaFile = JavaFile
                    .builder("com.alibaba.android.arouter", builder.build())
                    // 设置表示缩进的字符串
                    .indent("    ")
                    .build();
            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (Exception e) {
                logger.error(e);
            }
            logger.info("忽略异常提示");
            //这里要注意，要返回false，并且要放在Processor的前面，否则会影响arouter的Processor。
            return false;
        }
        return true;
    }
}
