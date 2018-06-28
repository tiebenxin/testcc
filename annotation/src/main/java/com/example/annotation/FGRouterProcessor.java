package com.example.annotation;

import com.google.auto.service.AutoService;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

/**
 * date on 2018/3/19
 * author ll147996
 * describe
 */

@AutoService(Processor.class)
public class FGRouterProcessor extends AbstractProcessor {

    private Filer mFiler;
    private Map<String, String> mStaticRouterMap = new HashMap<>();


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        set.add(Path.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        mStaticRouterMap.clear();

        Set<? extends Element> mainAppElement = roundEnvironment.getElementsAnnotatedWith(Path.class);
        if (mainAppElement.isEmpty()) {
            return false;
        } else {
            for (Element e : mainAppElement) {
                if (! (e instanceof TypeElement)) { continue;}
                TypeElement typeElement = (TypeElement) e;
                String pattern = typeElement.getAnnotation(Path.class).value();
                String className = typeElement.getQualifiedName().toString();
                mStaticRouterMap.put(pattern, className);
            }
            try {
                writeFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return true;
    }

    private void writeFile() throws IOException {
        String fileName = RouteConfig.FILE_NAME;
        JavaFileObject javaFileObject = mFiler.createSourceFile(fileName);
        PrintWriter printWriter = new PrintWriter(javaFileObject.openWriter());

        printWriter.println("package " + RouteConfig.PACKAGE_NAME + ";");

        printWriter.println("public class " + RouteConfig.CLASS_NAME + " {");
        printWriter.println("public static void " +  RouteConfig.METHOD_NAME + "() {");

        for(Map.Entry<String, String> entry : mStaticRouterMap.entrySet()) {
            printWriter.println(RouteConfig.ACTIVITYS_ROUTER + "("
                + "\"" + entry.getKey() + "\"" + ", " + entry.getValue() + ".class);");
        }

        printWriter.println("}}");
        printWriter.flush();
        printWriter.close();
    }
}
