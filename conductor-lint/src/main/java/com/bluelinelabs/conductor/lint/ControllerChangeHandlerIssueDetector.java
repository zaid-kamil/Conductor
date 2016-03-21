package com.bluelinelabs.conductor.lint;

import com.android.annotations.NonNull;
import com.android.tools.lint.client.api.JavaParser.ResolvedClass;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.android.tools.lint.detector.api.Speed;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;

import lombok.ast.ClassDeclaration;
import lombok.ast.ConstructorDeclaration;
import lombok.ast.Node;
import lombok.ast.NormalTypeBody;
import lombok.ast.StrictListAccessor;
import lombok.ast.TypeMember;
import lombok.ast.VariableDefinition;

public final class ControllerChangeHandlerIssueDetector extends Detector implements Detector.JavaScanner, Detector.ClassScanner {

    public static final Issue ISSUE =
            Issue.create("ValidControllerChangeHandler", "ControllerChangeHandler not instantiatable",
                    "Non-abstract ControllerChangeHandler instances must have a default constructor for the"
                            + " system to re-create them in the case of the process being killed.",
                    Category.CORRECTNESS, 6, Severity.FATAL,
                    new Implementation(ControllerChangeHandlerIssueDetector.class, Scope.JAVA_FILE_SCOPE));

    public ControllerChangeHandlerIssueDetector() { }

    @NonNull
    @Override
    public Speed getSpeed() {
        return Speed.FAST;
    }

    @Override
    public List<String> applicableSuperClasses() {
        return Collections.singletonList("com.bluelinelabs.conductor.ControllerChangeHandler");
    }

    @Override
    public void checkClass(@NonNull JavaContext context, ClassDeclaration node,
                           @NonNull Node declarationOrAnonymous, @NonNull ResolvedClass cls) {

        if (node == null) {
            return;
        }

        final int flags = node.astModifiers().getEffectiveModifierFlags();
        if ((flags & Modifier.ABSTRACT) != 0) {
            return;
        }

        if ((flags & Modifier.PUBLIC) == 0) {
            String message = String.format("This ControllerChangeHandler class should be public (%1$s)", cls.getName());
            context.report(ISSUE, node, context.getLocation(node.astName()), message);
            return;
        }

        if (cls.getContainingClass() != null && (flags & Modifier.STATIC) == 0) {
            String message = String.format("This ControllerChangeHandler inner class should be static (%1$s)", cls.getName());
            context.report(ISSUE, node, context.getLocation(node.astName()), message);
            return;
        }

        boolean hasConstructor = false;
        boolean hasDefaultConstructor = false;
        NormalTypeBody body = node.astBody();
        if (body != null) {
            for (TypeMember member : body.astMembers()) {
                if (member instanceof ConstructorDeclaration) {
                    hasConstructor = true;
                    ConstructorDeclaration constructor = (ConstructorDeclaration)member;

                    if (constructor.astModifiers().isPublic()) {
                        StrictListAccessor<VariableDefinition, ConstructorDeclaration> params = constructor.astParameters();
                        if (params.isEmpty()) {
                            hasDefaultConstructor = true;
                            break;
                        }
                    }
                }
            }
        }

        if (hasConstructor && !hasDefaultConstructor) {
            String message = String.format(
                    "This ControllerChangeHandler needs to have a public default constructor (`%1$s`)",
                    cls.getName());
            context.report(ISSUE, node, context.getLocation(node.astName()), message);
        }
    }
}