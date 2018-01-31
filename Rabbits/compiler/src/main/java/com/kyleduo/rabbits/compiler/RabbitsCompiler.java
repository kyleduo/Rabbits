package com.kyleduo.rabbits.compiler;

import com.google.auto.service.AutoService;
import com.kyleduo.rabbits.annotations.Module;
import com.kyleduo.rabbits.annotations.Page;
import com.kyleduo.rabbits.annotations.PageType;
import com.kyleduo.rabbits.annotations.TargetInfo;
import com.kyleduo.rabbits.annotations.utils.NameParser;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

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
    private static final String NAVIGATOR_PACKAGE = "com.kyleduo.rabbits.navigator";
    private static final String ROUTER_CLASS = "Router";
    private static final String ROUTERS_CLASS = "Routers";
    private static final String ROUTER_P_CLASS = "P";

    private Elements elements;
    private Types types;
    private TypeMirror activityType;
    private TypeMirror fragmentType;
    private TypeMirror fragmentV4Type;

    private Filer mFiler;
    private LinkedHashMap<String, String> mTable = new LinkedHashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();

        elements = processingEnv.getElementUtils();
        types = processingEnv.getTypeUtils();

        activityType = elements.getTypeElement("android.app.Activity").asType();
        fragmentType = elements.getTypeElement("android.app.Fragment").asType();
        fragmentV4Type = elements.getTypeElement("android.support.v4.app.Fragment").asType();
    }

//    private File findMappings(String srcPath) throws IOException, URISyntaxException {
//        FileObject resource = mFiler.createResource(StandardLocation.SOURCE_OUTPUT, PACKAGE, "dummy" + System.currentTimeMillis() + ".tmp");
//        String dummySourceFilePath = resource.toUri().toString();
//
//        if (dummySourceFilePath.startsWith("file:")) {
//            if (!dummySourceFilePath.startsWith("file://")) {
//                dummySourceFilePath = "file://" + dummySourceFilePath.substring("file:".length());
//            }
//        } else {
//            dummySourceFilePath = "file://" + dummySourceFilePath;
//        }
//
//        URI cleanURI = new URI(dummySourceFilePath);
//        File dummyFile = new File(cleanURI);
//        File projectRoot = null;
//        while (projectRoot == null || projectRoot.getAbsolutePath().contains("build")) {
//            if (projectRoot == null) {
//                projectRoot = dummyFile.getParentFile();
//            } else {
//                projectRoot = projectRoot.getParentFile();
//            }
//        }
//        File assets = new File(projectRoot.getAbsolutePath() + "/src/" + srcPath + "/assets");
//        if (assets.exists()) {
//            String[] filenames = assets.list(new FilenameFilter() {
//                @Override
//                public boolean accept(File dir, String name) {
//                    return name.startsWith("mappings") && name.endsWith(".json");
//                }
//            });
//            if (filenames == null || filenames.length == 0) {
//                return null;
//            }
//            return new File(assets, filenames[0]);
//        }
//        return null;
//    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(Page.class.getName());
        types.add(Module.class.getName());
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

        ClassName mappingTable = ClassName.get(PACKAGE, "MappingTable");
        ClassName targetInfo = ClassName.get(PACKAGE + ".annotations", "TargetInfo");

        MethodSpec.Builder generateBuilder = MethodSpec.methodBuilder("generate")
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC);

        List<PageInfo> pages = new ArrayList<>();
        for (Element e : roundEnv.getElementsAnnotatedWith(Page.class)) {
            Page page = e.getAnnotation(Page.class);
            if (page != null) {
                TypeMirror mirror = e.asType();
                String url = page.value();

                // 只接受这种格式的url
                // (scheme://domain)/path

                while (url.endsWith("/")) {
                    url = url.substring(url.length() - 1);
                }

                // path
                ClassName target = ClassName.get((TypeElement) e);
                int type = TargetInfo.TYPE_NOT_FOUND;

                if (types.isSubtype(mirror, activityType)) {
                    type = TargetInfo.TYPE_ACTIVITY;
                } else if (types.isSubtype(mirror, fragmentType)) {
                    type = TargetInfo.TYPE_FRAGMENT;
                } else if (types.isSubtype(mirror, fragmentV4Type)) {
                    type = TargetInfo.TYPE_FRAGMENT_V4;
                }

                pages.add(new PageInfo(url, target, type, page.flags()));
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
                    mappingTable,
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


//        Module module = parseRabbits(roundEnv);
//        if (module == null) {
//            throw new IllegalStateException("A Module annotation is required.");
//        }
//        String moduleName = module.name();
//        String[] subModules = module.subModules();
//        boolean standalone = module.standalone();
//
//        if (standalone) {
//            if (subModules.length == 0) {
//                if (moduleName.length() != 0) {
//                    // In standalone mode.
//                    String[] m = {moduleName};
//                    generateRouters(m);
//                }
//            } else {
//                String[] m = new String[subModules.length + 1];
//                m[0] = "";
//                System.arraycopy(subModules, 0, m, 1, subModules.length);
//                generateRouters(m);
//            }
//        }
//
//        List<MethodSpec> methods = parsePages(roundEnv);
//        String routerClassName = getRouterClassName(moduleName);
//
//        TypeSpec routerTypeSpec = TypeSpec.classBuilder(routerClassName)
//                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
//                .addMethods(methods)
//                .build();
//        try {
//            JavaFile.builder(PACKAGE, routerTypeSpec)
//                    .build()
//                    .writeTo(mFiler);
//        } catch (Throwable e) {
////			e.printStackTrace();
//        }
//
//        generateP(moduleName, module.srcPath());

        return true;
    }

    private String getRouterClassName(String moduleName) {
        String routerClassName = ROUTER_CLASS;
        if (moduleName.length() > 0) {
            moduleName = moduleName.substring(0, 1).toUpperCase() + moduleName.substring(1).toLowerCase();
            routerClassName += moduleName;
        }
        return routerClassName;
    }

    private Module parseRabbits(RoundEnvironment roundEnv) {
        Module module = null;
        for (Element e : roundEnv.getElementsAnnotatedWith(Module.class)) {
            module = e.getAnnotation(Module.class);
            if (module != null) {
                break;
            }
        }
        return module;
    }

    private List<MethodSpec> parsePages(RoundEnvironment roundEnv) {
        List<MethodSpec> methods = new ArrayList<>();
        for (Element e : roundEnv.getElementsAnnotatedWith(Page.class)) {
            Page page = e.getAnnotation(Page.class);
            if (page != null) {
                PageType type = PageType.ACTIVITY;
                String name = "";
                if (type == PageType.ACTIVITY) {
                    String methodName = NameParser.parseRoute(name);
                    ClassName className = ClassName.get((TypeElement) e);

                    // route
                    MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder(methodName)
                            .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                            .returns(Class.class)
                            .addStatement("return $T.class", className);
                    methods.add(methodSpecBuilder.build());
                } else if (type == PageType.FRAGMENT) {
                    String parent = page.parent();
                    boolean hasParent = !parent.equals("");

                    ClassName className = ClassName.get((TypeElement) e);
                    // route
                    String methodName = NameParser.parseRoute(name);
                    MethodSpec.Builder methodSpecBuilder;
                    if (hasParent) {
                        methodSpecBuilder = MethodSpec.methodBuilder(methodName)
                                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC);
                        methodSpecBuilder.returns(ClassName.get(NAVIGATOR_PACKAGE, "AbstractNavigator"));
                        String parentMethodName;
                        ClassName moduleClass = null;
                        if (parent.contains(".")) {
                            String[] parts = parent.split("\\.");
                            moduleClass = ClassName.get(PACKAGE, getRouterClassName(parts[0]));
                            parentMethodName = NameParser.parseRoute(parts[1]);
                        } else {
                            parentMethodName = NameParser.parseRoute(parent);
                        }
                        methodSpecBuilder.addStatement("android.os.Bundle bundle = new android.os.Bundle()");
                        parseExtras(methodSpecBuilder, page);
                        ClassName targetClass = ClassName.get(PACKAGE, "Target");
                        methodSpecBuilder.addStatement("$T target = new $T(null)", targetClass, targetClass);
                        if (moduleClass != null) {
                            methodSpecBuilder.addStatement("target.setTo($T.$L())", moduleClass, parentMethodName);
                        } else {
                            methodSpecBuilder.addStatement("target.setTo($L())", parentMethodName);
                        }
                        methodSpecBuilder.addStatement("target.setExtras(bundle)");
                        methodSpecBuilder.addStatement("return new $T(null, target, null)", ClassName.get(NAVIGATOR_PACKAGE, "DefaultNavigator"));
                        methods.add(methodSpecBuilder.build());

                        // obtain
                        methodName = NameParser.parseObtain(name);
                        methodSpecBuilder = MethodSpec.methodBuilder(methodName)
                                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC);
                        methodSpecBuilder.returns(className)
                                .addStatement("return new $T()", className);
                        methods.add(methodSpecBuilder.build());
                    } else {
                        methodSpecBuilder = MethodSpec.methodBuilder(methodName)
                                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC);
                        methodSpecBuilder.returns(className)
                                .addStatement("return new $T()", className);
                        methods.add(methodSpecBuilder.build());
                    }
                }
            }
        }
        return methods;
    }

//    private void generateP(String moduleName, String srcPath) {
//        try {
//            File mappingsFile = findMappings(srcPath);
//            if (mappingsFile != null && mappingsFile.exists()) {
//                Gson gson = new Gson();
//                MappingTable table = gson.fromJson(new InputStreamReader(new FileInputStream(mappingsFile)), MappingTable.class);
//                for (Map.Entry<String, JsonElement> e : table.mappings.entrySet()) {
//                    String url = e.getKey();
//                    String name = table.mappings.get(url).getAsString();
//                    if (name == null || url == null || "".equals(name) || "".equals(url)) {
//                        continue;
//                    }
//                    if (mTable.containsKey(name)) {
//                        continue;
//                    }
//                    mTable.put(name, url);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        List<FieldSpec> pFields = new ArrayList<>();
//        List<MethodSpec> pMethods = new ArrayList<>();
//        for (Map.Entry<String, String> e : mTable.entrySet()) {
//            String name = e.getKey();
//            String url = e.getValue();
//            if (url.contains("{") && url.contains("}")) {
//                MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(name)
//                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
//                        .returns(String.class);
//                Pattern pattern = Pattern.compile("\\{([^{}:]+):?([^{}]*)\\}");
//                Matcher matcher = pattern.matcher(url);
//                List<ParameterSpec> params = new ArrayList<>();
//                List<String> holder = new ArrayList<>();
//                while (matcher.find()) {
//                    int count = matcher.groupCount() + 1;
//                    if (count < 2) {
//                        continue;
//                    }
//                    String paramName = matcher.group(1);
//                    String paramType = "";
//                    if (count == 3) {
//                        paramType = matcher.group(2);
//                    }
//                    Type t;
//                    switch (paramType) {
//                        case "i":
//                            t = int.class;
//                            holder.add("%d");
//                            break;
//                        case "l":
//                            t = long.class;
//                            holder.add("%d");
//                            break;
//                        case "f":
//                            t = float.class;
//                            holder.add("%f");
//                            break;
//                        case "d":
//                            t = double.class;
//                            holder.add("%f");
//                            break;
//                        case "b":
//                            t = boolean.class;
//                            holder.add("%b");
//                            break;
//                        case "s":
//                        default:
//                            t = String.class;
//                            holder.add("%s");
//                            break;
//                    }
//                    params.add(ParameterSpec.builder(t, paramName).build());
//                }
//                methodBuilder.addParameters(params);
//                StringBuilder objBuilder = new StringBuilder();
//                for (int i = 0; i < params.size(); i++) {
//                    objBuilder.append("$L, ");
//                }
//                if (objBuilder.length() > 2) {
//                    objBuilder.delete(objBuilder.length() - 2, objBuilder.length());
//                }
//                String format = url;
//                for (int i = 0; i < params.size(); i++) {
//                    format = format.replaceFirst("\\{([^{}:]+):?([^{}]*)\\}", holder.get(i));
//                }
//                String statement = "return String.format(\"$L\", " + objBuilder.toString() + ")";
//                List<String> obj = new ArrayList<>();
//                obj.add(format);
//                for (int i = 0; i < params.size(); i++) {
//                    obj.add(params.get(i).name);
//                }
//                methodBuilder.addStatement(statement, obj.toArray());
//                pMethods.add(methodBuilder.build());
//            } else {
//                FieldSpec.Builder fieldBuilder = FieldSpec.builder(String.class, name, Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
//                        .initializer("\"$L\"", url);
//                FieldSpec fieldSpec = fieldBuilder.build();
//                pFields.add(fieldSpec);
//            }
//
//        }
//
//        String className = ROUTER_P_CLASS;
//        if (moduleName != null && moduleName.length() > 0) {
//            className += "_" + moduleName.toUpperCase();
//        }
//
//        TypeSpec pTypeSpec = TypeSpec.classBuilder(className)
//                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
//                .addFields(pFields)
//                .addMethods(pMethods)
//                .build();
//        try {
//            JavaFile.builder(PACKAGE, pTypeSpec)
//                    .build()
//                    .writeTo(mFiler);
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
//    }

    private void generateRouters(String[] subModules) {
        ArrayList<String> routerNames = new ArrayList<>();
        for (String module : subModules) {
            routerNames.add(getRouterClassName(module));
        }

        StringBuilder init = new StringBuilder();
        for (String name : routerNames) {
            init.append('"').append(name).append('"').append(",");
        }
        init.deleteCharAt(init.length() - 1);

        FieldSpec routersField = FieldSpec.builder(String[].class, "routers", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("new String[]{$L}", init.toString())
                .build();

        TypeSpec routersTypeSpec = TypeSpec.classBuilder(ROUTERS_CLASS)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(routersField)
                .build();
        try {
            JavaFile.builder(PACKAGE, routersTypeSpec)
                    .build()
                    .writeTo(mFiler);
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    private void parseExtras(MethodSpec.Builder methodSpecBuilder, Page page) {
        String[] extras = page.stringExtras();
        if (extras.length >= 2) {
            for (int i = 0; i < extras.length; i += 2) {
                String key = extras[i];
                String value = extras[i + 1];
                methodSpecBuilder.addStatement("bundle.putString($S, $S)", key, value);
            }
        }
        extras = page.intExtras();
        if (extras.length >= 2) {
            for (int i = 0; i < extras.length; i += 2) {
                String key = extras[i];
                int value = Integer.parseInt(extras[i + 1]);
                methodSpecBuilder.addStatement("bundle.putInt($S, $L)", key, value);
            }
        }
        extras = page.floatExtras();
        if (extras.length >= 2) {
            for (int i = 0; i < extras.length; i += 2) {
                String key = extras[i];
                float value = Float.parseFloat(extras[i + 1]);
                methodSpecBuilder.addStatement("bundle.putFloat($S, $L)", key, value);
            }
        }
        extras = page.doubleExtras();
        if (extras.length >= 2) {
            for (int i = 0; i < extras.length; i += 2) {
                String key = extras[i];
                double value = Double.parseDouble(extras[i + 1]);
                methodSpecBuilder.addStatement("bundle.putDouble($S, $L)", key, value);
            }
        }
    }

    private void debug(String message) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message);
    }
}
