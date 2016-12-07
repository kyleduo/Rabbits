package com.kyleduo.rabbits.compiler;

import com.google.auto.service.AutoService;
import com.kyleduo.rabbits.annotations.Page;
import com.kyleduo.rabbits.annotations.PageType;
import com.kyleduo.rabbits.annotations.utils.NameParser;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashSet;
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
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class RabbitsCompiler extends AbstractProcessor {
	private static final String PACKAGE = "com.kyleduo.rabbits";
	private static final String NAVIGATOR_PACKAGE = "com.kyleduo.rabbits.navigator";
	private static final String ROUTER_CLASS = "Router";

	private Filer mFiler;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		mFiler = processingEnv.getFiler();
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
					MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder(methodName)
							.addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
							.returns(Class.class)
							.addStatement("return $T.class", className);
					methods.add(methodSpecBuilder.build());
				} else if (type == PageType.FRAGMENT) {
					String parent = page.parent();
					boolean hasParent = !parent.equals("");
					String methodName = hasParent ? NameParser.parseObtain(name) : NameParser.parseRoute(name);
					ClassName className = ClassName.get((TypeElement) e);
					MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder(methodName)
							.addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC);
					methodSpecBuilder.returns(className)
							.addStatement("return $T.newInstance()", className);
					methods.add(methodSpecBuilder.build());
					if (hasParent) {
						methodName = NameParser.parseRoute(name);
						methodSpecBuilder = MethodSpec.methodBuilder(methodName)
								.addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC);
						methodSpecBuilder.returns(ClassName.get(NAVIGATOR_PACKAGE, "AbstractNavigator"));
						String parentMethodName = NameParser.parseRoute(parent);
						methodSpecBuilder.addStatement("android.os.Bundle bundle = new android.os.Bundle()");
						parseExtras(methodSpecBuilder, page);
						methodSpecBuilder.addStatement("return new $T(null, null, $L(), null, 0, bundle, null)", ClassName.get(NAVIGATOR_PACKAGE, "DefaultNavigator"), parentMethodName);
						methods.add(methodSpecBuilder.build());
					}
				}
			}
		}

		TypeSpec typeSpec = TypeSpec.classBuilder(ROUTER_CLASS)
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.addMethods(methods)
				.build();
		try {
			JavaFile.builder(PACKAGE, typeSpec)
					.build()
					.writeTo(mFiler);
		} catch (Throwable e) {
			e.printStackTrace();
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
