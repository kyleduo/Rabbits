package com.kyleduo.rabbits.compiler;

import com.google.auto.service.AutoService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.kyleduo.rabbits.annotations.Page;
import com.kyleduo.rabbits.annotations.PageType;
import com.kyleduo.rabbits.annotations.utils.NameParser;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
public class RabbitsCompiler extends AbstractProcessor {
	private static final String PACKAGE = "com.kyleduo.rabbits";
	private static final String NAVIGATOR_PACKAGE = "com.kyleduo.rabbits.navigator";
	private static final String ROUTER_CLASS = "Router";
	private static final String ROUTER_P_CLASS = "P";

	private Filer mFiler;
	private LinkedHashMap<String, String> mTable = new LinkedHashMap<>();

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		mFiler = processingEnv.getFiler();
		try {
			File mappingsFile = findMappings();
			if (mappingsFile != null && mappingsFile.exists()) {
				Gson gson = new Gson();
				MappingTable table = gson.fromJson(new InputStreamReader(new FileInputStream(mappingsFile)), MappingTable.class);
				for (Map.Entry<String, JsonElement> e : table.mappings.entrySet()) {
					String url = e.getKey();
					String name = table.mappings.get(url).getAsString();
					if (name == null || url == null || "".equals(name) || "".equals(url)) {
						continue;
					}
					if (mTable.containsKey(name)) {
						continue;
					}
					mTable.put(name, url);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private File findMappings() throws IOException, URISyntaxException {

		JavaFileObject dummySourceFile = mFiler.createSourceFile("dummy" + System.currentTimeMillis());
		String dummySourceFilePath = dummySourceFile.toUri().toString();

		if (dummySourceFilePath.startsWith("file:")) {
			if (!dummySourceFilePath.startsWith("file://")) {
				dummySourceFilePath = "file://" + dummySourceFilePath.substring("file:".length());
			}
		} else {
			dummySourceFilePath = "file://" + dummySourceFilePath;
		}

		URI cleanURI = new URI(dummySourceFilePath);
		File dummyFile = new File(cleanURI);
		File projectRoot = null;
		while (projectRoot == null || projectRoot.getAbsolutePath().contains("build")) {
			if (projectRoot == null) {
				projectRoot = dummyFile.getParentFile();
			} else {
				projectRoot = projectRoot.getParentFile();
			}
		}
		return new File(projectRoot.getAbsolutePath() + "/src/main/assets/mappings.json");
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
		List<MethodSpec> methods = new ArrayList<>();

		for (TypeElement te : annotations) {
			for (Element e : roundEnv.getElementsAnnotatedWith(te)) {
				Page page = e.getAnnotation(Page.class);
				PageType type = page.type();
				String name = page.name();
				if (type == PageType.ACTIVTY) {
					String methodName = NameParser.parseRoute(name);
					ClassName className = ClassName.get((TypeElement) e);

					// route
					MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder(methodName)
							.addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
							.returns(Class.class)
							.addStatement("return $T.class", className);
					methods.add(methodSpecBuilder.build());

					// obtain
//					methodName = NameParser.parseObtain(name);
//					methodSpecBuilder = MethodSpec.methodBuilder(methodName)
//							.addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
//							.returns(Class.class)
//							.addStatement("return $T.class", className);
//					methods.add(methodSpecBuilder.build());
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
						String parentMethodName = NameParser.parseRoute(parent);
						methodSpecBuilder.addStatement("android.os.Bundle bundle = new android.os.Bundle()");
						parseExtras(methodSpecBuilder, page);
						ClassName targetClass = ClassName.get(PACKAGE, "Target");
						methodSpecBuilder.addStatement("$T target = new $T(null)", targetClass, targetClass);
						methodSpecBuilder.addStatement("target.setTo($L())", parentMethodName);
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

		TypeSpec routerTypeSpec = TypeSpec.classBuilder(ROUTER_CLASS)
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.addMethods(methods)
				.build();
		try {
			JavaFile.builder(PACKAGE, routerTypeSpec)
					.build()
					.writeTo(mFiler);
		} catch (Throwable e) {
//			e.printStackTrace();
		}

		List<FieldSpec> pFields = new ArrayList<>();
		List<MethodSpec> pMethods = new ArrayList<>();
		for (Map.Entry<String, String> e : mTable.entrySet()) {
			String name = e.getKey();
			String url = e.getValue();
			if (url.contains("{") && url.contains("}")) {
				MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(name)
						.addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
						.returns(String.class);
				Pattern pattern = Pattern.compile("\\{([^{}:]+):?([^{}]*)\\}");
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
					params.add(ParameterSpec.builder(t, paramName).build());
				}
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
					format = format.replaceFirst("\\{([^{}:]+):?([^{}]*)\\}", holder.get(i));
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
//			e.printStackTrace();
		}

		return true;
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
