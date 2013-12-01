package de.halfreal.spezi.mvc.internal;

import static javax.tools.Diagnostic.Kind.*;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;

import de.halfreal.spezi.mvc.Model;

@SupportedAnnotationTypes("de.halfreal.spezi.mvc.Model")
public class ModelProcessor extends AbstractProcessor {

	private static final String HEADER = "/* Generated code. Do not modify! */\n";
	private static final String MODEL_BASE_CLASS = "de.halfreal.spezi.mvc.AbstractModel";

	private Filer filer;
	private Types typeUtils;

	private void addTypes(Set<String> imports, TypeMirror type) {

		if (type instanceof DeclaredType) {
			DeclaredType declaredType = (DeclaredType) type;
			Element declaredElement = declaredType.asElement();
			imports.add(declaredElement.toString());

			for (TypeMirror typeArgument : declaredType.getTypeArguments()) {
				addTypes(imports, typeArgument);
			}
		} else if (type instanceof WildcardType) {
			WildcardType wildcardType = (WildcardType) type;
			if (wildcardType.getExtendsBound() != null) {
				addTypes(imports, wildcardType.getExtendsBound());
			}
			if (wildcardType.getSuperBound() != null) {
				addTypes(imports, wildcardType.getSuperBound());
			}
		}
	}

	private void appendClassFooter(StringBuilder builder) {
		builder.append("}\n");
	}

	private void appendClassHeader(StringBuilder builder,
			TypeElement typeElement) {
		String className = getClassName(typeElement);

		builder.append("public class ").append(className).append(" extends ")
				.append(typeElement.getSimpleName()).append(" {\n\n");
	}

	private void appendGettersAndSetters(StringBuilder builder,
			TypeElement typeElement) {
		for (Element enclosedElement : typeElement.getEnclosedElements()) {
			if (enclosedElement instanceof VariableElement) {
				VariableElement variableElement = (VariableElement) enclosedElement;

				if (isConstant(variableElement)) {
					continue;
				}

				if (!hasValidModifiers(variableElement)) {
					error(variableElement,
							"Variable must have either constant modifiers (public static final) or no modifiers (%s.%s)",
							typeElement, variableElement);
				}

				appendVariable(builder, enclosedElement,
						variableElement.asType());
			}
		}
	}

	private void appendHeader(StringBuilder builder) {
		builder.append(HEADER);
	}

	private void appendImports(StringBuilder builder, TypeElement typeElement) {
		Set<String> imports = new TreeSet<String>();

		imports.add("de.halfreal.spezi.mvc.Key");

		for (Element enclosedElement : typeElement.getEnclosedElements()) {
			if (enclosedElement instanceof VariableElement) {
				VariableElement variableElement = (VariableElement) enclosedElement;
				addTypes(imports, variableElement.asType());
			}
		}

		for (String importClass : imports) {
			builder.append("import ").append(importClass).append(";\n");
		}

		builder.append("\n");
	}

	private void appendKeysClass(StringBuilder builder, TypeElement typeElement) {

		builder.append("\tpublic static class Keys {\n\n");

		for (Element enclosedElement : typeElement.getEnclosedElements()) {
			if (enclosedElement instanceof VariableElement) {
				VariableElement variableElement = (VariableElement) enclosedElement;
				builder.append("\t\tpublic static final Key<")
						.append(getTypeString(variableElement.asType(), true))
						.append("> ")
						.append(getConstantName(enclosedElement.getSimpleName()))
						.append(" = new Key<")
						.append(getTypeString(variableElement.asType(), true))
						.append(">(\"").append(variableElement.getSimpleName())
						.append("\");\n");
			}
		}

		builder.append("\n\t}\n\n");
	}

	private void appendPackage(StringBuilder builder, TypeElement typeElement) {
		PackageElement packageElement = (PackageElement) typeElement
				.getEnclosingElement();
		builder.append("package ").append(packageElement.getQualifiedName())
				.append(";\n\n");
	}

	private void appendVariable(StringBuilder builder, Element enclosedElement,
			TypeMirror declaredType) {
		builder.append("\tpublic ").append(getTypeString(declaredType, false))
				.append(" ")
				.append(getGetterName(enclosedElement.getSimpleName()))
				.append("() {\n");
		builder.append("\t\treturn ").append(enclosedElement.getSimpleName())
				.append(";\n");
		builder.append("\t}\n\n");

		builder.append("\tpublic void ")
				.append(getSetterName(enclosedElement.getSimpleName()))
				.append("(").append(getTypeString(declaredType, false))
				.append(" ").append(enclosedElement.getSimpleName())
				.append(") {\n");
		builder.append("\t\t").append(getTypeString(declaredType, false))
				.append(" oldValue = this.")
				.append(enclosedElement.getSimpleName()).append(";\n");
		builder.append("\t\tthis.").append(enclosedElement.getSimpleName())
				.append(" = ").append(enclosedElement.getSimpleName())
				.append(";\n");
		builder.append("\t\tfireChange(").append("Keys.")
				.append(getConstantName(enclosedElement.getSimpleName()))
				.append(", oldValue, ").append(enclosedElement.getSimpleName())
				.append(");\n");
		builder.append("\t}\n\n");
	}

	private String createModelBody(TypeElement typeElement, TypeMirror type) {
		StringBuilder builder = new StringBuilder();

		appendHeader(builder);
		appendPackage(builder, typeElement);
		appendImports(builder, typeElement);
		appendClassHeader(builder, typeElement);
		appendKeysClass(builder, typeElement);
		appendGettersAndSetters(builder, typeElement);
		appendClassFooter(builder);

		return builder.toString();
	}

	private void createModelClass(TypeElement typeElement) {
		TypeMirror type = typeElement.asType();

		Element element = typeElement.getEnclosingElement();
		if (element instanceof PackageElement) {
			PackageElement packageElement = (PackageElement) element;
			String fileName = getPackageName(packageElement) + "."
					+ getClassName(typeElement);
			String modelBody = createModelBody(typeElement, type);

			writeModelClass(fileName, modelBody, typeElement);
		} else {
			// TODO add support for inner classes
			error(element, "Cannot generate Model for inner Classes. %s",
					element.toString());
		}
	}

	private void error(Element element, String message, Object... args) {
		processingEnv.getMessager().printMessage(ERROR,
				String.format(message, args), element);
	}

	private void error(TypeElement typeElement, Exception e) {
		processingEnv.getMessager().printMessage(ERROR,
				e.getClass().getName() + ": " + e.getMessage());
	}

	private String getClassName(TypeElement typeElement) {
		if (typeElement.getSimpleName().toString().endsWith("Stub")) {
			return typeElement.getSimpleName().toString()
					.substring(0, typeElement.getSimpleName().length() - 4);
		} else {
			return typeElement.getSimpleName() + "Model";
		}
	}

	private String getConstantName(Name simpleName) {
		String regex = "([a-z])([A-Z])";
		String replacement = "$1_$2";
		return simpleName.toString().replaceAll(regex, replacement)
				.toUpperCase();
	}

	private String getGetterName(Name simpleName) {
		return "get" + getUppercaseName(simpleName);
	}

	private String getPackageName(PackageElement packageElement) {
		return packageElement.getQualifiedName().toString();
	}

	private String getSetterName(Name simpleName) {
		return "set" + getUppercaseName(simpleName);
	}

	private String getTypeName(Element declaredElement) {
		String[] names = declaredElement.toString().split("\\.");
		return names[names.length - 1];
	}

	private String getTypeString(TypeMirror type, boolean convertPrimitives) {

		StringBuilder typeBuilder = new StringBuilder();
		if (type instanceof DeclaredType) {
			DeclaredType declaredType = (DeclaredType) type;
			Element declaredElement = declaredType.asElement();

			typeBuilder.append(getTypeName(declaredElement));

			List<? extends TypeMirror> typeArguments = declaredType
					.getTypeArguments();

			if (typeArguments.size() > 0) {
				typeBuilder.append("<");
				boolean first = true;
				for (TypeMirror typeArgument : typeArguments) {
					if (first) {
						first = false;
					} else {
						typeBuilder.append(",");
					}
					typeBuilder.append(getTypeString(typeArgument,
							convertPrimitives));
				}
				typeBuilder.append(">");
			}

			return typeBuilder.toString();
		} else if (type instanceof WildcardType) {

			WildcardType wildcardType = (WildcardType) type;
			if (wildcardType.getExtendsBound() == null
					&& wildcardType.getSuperBound() == null) {
				// ?
				return "?";
			} else if (wildcardType.getExtendsBound() != null) {
				// ? extends Number
				return String.format(
						"? extends %s",
						getTypeString(wildcardType.getExtendsBound(),
								convertPrimitives));
			} else {
				// ? super T
				return String.format(
						"? super %s",
						getTypeString(wildcardType.getSuperBound(),
								convertPrimitives));
			}
		} else if (type instanceof PrimitiveType) {
			PrimitiveType primitiveType = (PrimitiveType) type;
			if (convertPrimitives) {
				TypeElement boxedClass = typeUtils.boxedClass(primitiveType);
				return getTypeString(boxedClass.asType(), convertPrimitives);
			} else {
				return primitiveType.toString();
			}
		} else {
			// TODO provide an error
			return "";
		}
	}

	private String getUppercaseName(Name simpleName) {
		return simpleName.toString().substring(0, 1).toUpperCase()
				+ simpleName.toString().substring(1);
	}

	private boolean hasValidModifiers(VariableElement variableElement) {
		Set<Modifier> modifiers = variableElement.getModifiers();

		if (modifiers.size() != 0) {
			return false;
		}

		return true;
	}

	@Override
	public synchronized void init(ProcessingEnvironment env) {
		super.init(env);

		filer = env.getFiler();
		typeUtils = env.getTypeUtils();
	}

	private boolean isConstant(VariableElement variableElement) {
		Set<Modifier> modifiers = variableElement.getModifiers();

		if (modifiers.size() != 3) {
			return false;
		}

		return modifiers.contains(Modifier.PUBLIC)
				&& modifiers.contains(Modifier.STATIC)
				&& modifiers.contains(Modifier.FINAL);
	}

	private boolean isSubtypeOfAbstractModel(Element element) {
		if (!(element instanceof TypeElement)) {
			return false;
		}
		TypeElement typeElement = (TypeElement) element;
		return isSubtypeOfAbstractModel(typeElement.getSuperclass());
	}

	private boolean isSubtypeOfAbstractModel(TypeMirror typeMirror) {
		if (!(typeMirror instanceof DeclaredType)) {
			return false;
		}
		DeclaredType declaredType = (DeclaredType) typeMirror;
		if (declaredType.toString().equals(MODEL_BASE_CLASS)) {
			return true;
		} else {
			return isSubtypeOfAbstractModel(declaredType.asElement());
		}
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {

		Set<? extends Element> modelElements = roundEnv
				.getElementsAnnotatedWith(Model.class);

		for (Element element : modelElements) {
			if (element instanceof TypeElement) {
				processElement((TypeElement) element);
			}
		}

		return true;
	}

	private void processElement(TypeElement element) {
		if (isSubtypeOfAbstractModel(element)) {
			createModelClass(element);
		} else {
			error(element,
					"Model stub class must extend de.halfreal.spezi.mvc.AbstractModel (%s).",
					element);
		}
	}

	private void writeModelClass(String fileName, String modelBody,
			TypeElement typeElement) {
		try {
			JavaFileObject jfo = filer.createSourceFile(fileName);
			Writer writer = jfo.openWriter();
			writer.write(modelBody);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			error(typeElement, e);
		}
	}

}
