package com.kyleduo.rabbits.compiler;

import com.google.auto.service.AutoService;
import com.kyleduo.rabbits.annotations.Page;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class RabbitsCompiler extends AbstractProcessor {
    private static final String PACKAGE = "com.kyleduo.rabbits";
    private static final String ROUTE_TABLE_CLASS = "RouteTable";
    private static final String TARGET_INFO_CLASS = "TargetInfo";
    private static final String ROUTER_P_CLASS = "P";
    private static final String REST_PATTERN = "\\{([^{}:]+):?([^{}]*)}";

    private static final int TYPE_ACTIVITY = 1;
    private static final int TYPE_FRAGMENT = 2;
    private static final int TYPE_FRAGMENT_V4 = 3;

    private Types types;
    private TypeMirror activityType;
    private TypeMirror fragmentType;
    private TypeMirror fragmentV4Type;

    private Filer mFiler;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();

        Elements elements = processingEnv.getElementUtils();
        types = processingEnv.getTypeUtils();

        activityType = elements.getTypeElement("android.app.Activity").asType();
        fragmentType = elements.getTypeElement("android.app.Fragment").asType();
        fragmentV4Type = elements.getTypeElement("android.support.v4.app.Fragment").asType();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(Page.class.getName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.size() == 0) {
            return false;
        }

        ClassName routeTable = ClassName.get(PACKAGE, ROUTE_TABLE_CLASS);
        ClassName targetInfo = ClassName.get(PACKAGE, TARGET_INFO_CLASS);

        MethodSpec.Builder generateBuilder = MethodSpec.methodBuilder("generate")
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC);

        List<PageInfo> pages = new ArrayList<>();
        Set<String> addedPattern = new HashSet<>();
        for (Element e : roundEnv.getElementsAnnotatedWith(Page.class)) {
            Page page = e.getAnnotation(Page.class);
            if (page != null) {
                TypeMirror mirror = e.asType();
                String url = page.value();

                // 只接受这种格式的url
                // (scheme://domain)/(path/path)

                while (url.length() > 1 && url.endsWith("/")) {
                    url = url.substring(0, url.length() - 1);
                }

                // path
                ClassName target = ClassName.get((TypeElement) e);
                int type = 0;

                if (types.isSubtype(mirror, activityType)) {
                    type = TYPE_ACTIVITY;
                } else if (types.isSubtype(mirror, fragmentType)) {
                    type = TYPE_FRAGMENT;
                } else if (types.isSubtype(mirror, fragmentV4Type)) {
                    type = TYPE_FRAGMENT_V4;
                }

                String key = url.replaceAll(REST_PATTERN, "{}");

                if (addedPattern.contains(key)) {
                    throw new IllegalStateException(String.format("Pattern '%s' has already exist.", url));
                }
                addedPattern.add(key);
                pages.add(new PageInfo(url, target, type, page.flags(), page.alias(), true));

                String[] variety = page.variety();
                if (variety.length > 0) {
                    for (String v : variety) {
                        String vKey = v.replaceAll(REST_PATTERN, "{}");
                        if (addedPattern.contains(vKey)) {
                            throw new IllegalStateException(String.format("Pattern '%s' has already exist.", v));
                        }
                        addedPattern.add(vKey);
                        pages.add(new PageInfo(v, target, type, page.flags(), page.alias(), false));
                    }
                }
            }
        }

        // sort pages
        Collections.sort(pages, new Comparator<PageInfo>() {
            @Override
            public int compare(PageInfo p1, PageInfo p2) {
                String u1 = p1.url;
                String u2 = p2.url;

                if (u1.contains("://") && !u2.contains("://")) {
                    return -1;
                } else if (u2.contains("://") && !u1.contains("://")) {
                    return 1;
                }

                int c1 = 0, c2 = 0, last = 0, index;
                index = u1.indexOf("{", last);
                last = index;
                while (index >= 0) {
                    c1++;
                    index = u1.indexOf("{", last + 1);
                    last = index;
                }
                index = u2.indexOf("{", last);
                last = index;
                while (index >= 0) {
                    c2++;
                    index = u2.indexOf("{", last + 1);
                    last = index;
                }

                if (c1 < c2) {
                    return -1;
                } else if (c2 > c1) {
                    return 1;
                }

                return 0;
            }
        });

        for (PageInfo p : pages) {
            generateBuilder.addStatement("$T.map(\"$L\", new $T(\"$L\", $T.class, $L, $L));",
                    routeTable,
                    p.url,
                    targetInfo,
                    p.url,
                    p.target,
                    String.valueOf(p.type),
                    String.valueOf(p.flag));
        }

        TypeSpec router = TypeSpec.classBuilder("Router")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(generateBuilder.build())
                .build();
        try {
            JavaFile.builder(PACKAGE, router)
                    .build()
                    .writeTo(mFiler);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        List<FieldSpec> pFields = new ArrayList<>();
        List<MethodSpec> pMethods = new ArrayList<>();
        for (PageInfo page : pages) {
            debug(page.toString());
            if (!page.main) {
                continue;
            }
            String alias = page.alias;
            String url = page.url;
            final boolean useUrl = isEmpty(alias);

            String name = useUrl ? url : alias;
            if (name.contains("://")) {
                name = name.replaceAll("(://|\\.)", "_");
            }
            while (name.startsWith("/")) {
                name = name.substring(1);
            }
            while (name.endsWith("/")) {
                name = name.substring(0, name.length() - 1);
            }
            name = name.replaceAll("/", "_").toUpperCase();
            if (name.contains(" ")) {
                name = name.replaceAll(" ", "_");
            }
            if (useUrl) {
                name = "P_" + name;
            }

            if (url.contains("{") && url.contains("}")) {
                Pattern pattern = Pattern.compile(REST_PATTERN);
                Matcher matcher = pattern.matcher(url);
                List<ParameterSpec> params = new ArrayList<>();
                List<String> holder = new ArrayList<>();
                while (matcher.find()) {
                    int count = matcher.groupCount() + 1;
                    if (count < 2) {
                        continue;
                    }
                    String paramName = matcher.group(1);
                    String paramType = "";
                    if (count == 3) {
                        paramType = matcher.group(2);
                        paramType = paramType.toLowerCase();
                    }
                    Type t;
                    switch (paramType) {
                        case "i":
                            t = int.class;
                            holder.add("%d");
                            break;
                        case "l":
                            t = long.class;
                            holder.add("%d");
                            break;
                        case "f":
                            t = float.class;
                            holder.add("%f");
                            break;
                        case "d":
                            t = double.class;
                            holder.add("%f");
                            break;
                        case "b":
                            t = boolean.class;
                            holder.add("%b");
                            break;
                        case "s":
                        default:
                            t = String.class;
                            holder.add("%s");
                            break;
                    }
                    if (useUrl) {
                        name = name.replaceFirst(REST_PATTERN, paramName.toUpperCase());
                    }
                    params.add(ParameterSpec.builder(t, paramName).build());
                }
                MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(name)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                        .returns(String.class);
                methodBuilder.addParameters(params);
                StringBuilder objBuilder = new StringBuilder();
                for (int i = 0; i < params.size(); i++) {
                    objBuilder.append("$L, ");
                }
                if (objBuilder.length() > 2) {
                    objBuilder.delete(objBuilder.length() - 2, objBuilder.length());
                }
                String format = url;
                for (int i = 0; i < params.size(); i++) {
                    format = format.replaceFirst(REST_PATTERN, holder.get(i));
                }
                String statement = "return String.format(\"$L\", " + objBuilder.toString() + ")";
                List<String> obj = new ArrayList<>();
                obj.add(format);
                for (int i = 0; i < params.size(); i++) {
                    obj.add(params.get(i).name);
                }
                methodBuilder.addStatement(statement, obj.toArray());
                pMethods.add(methodBuilder.build());
            } else {
                FieldSpec.Builder fieldBuilder = FieldSpec.builder(String.class, name, Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                        .initializer("\"$L\"", url);
                FieldSpec fieldSpec = fieldBuilder.build();
                pFields.add(fieldSpec);
            }
        }

        TypeSpec pTypeSpec = TypeSpec.classBuilder(ROUTER_P_CLASS)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addFields(pFields)
                .addMethods(pMethods)
                .build();
        try {
            JavaFile.builder(PACKAGE, pTypeSpec)
                    .build()
                    .writeTo(mFiler);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return true;
    }

    private boolean isEmpty(String text) {
        return text == null || text.length() == 0;
    }

    private void debug(String message) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message);
    }
}
