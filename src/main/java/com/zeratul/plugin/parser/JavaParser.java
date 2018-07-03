package com.zeratul.plugin.parser;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zeratul.plugin.java.Field;
import com.zeratul.plugin.java.JavaAstModel;
import com.zeratul.plugin.java.Method;
import com.zeratul.plugin.java.Pair;
import com.zeratul.plugin.util.TypeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.springframework.util.CollectionUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dreamyao
 * @title
 * @date 2018/4/12 下午10:26
 * @since 1.0.0
 */
public class JavaParser {

    private String javaFile;
    private JavaAstModel model;
    private boolean terminate = false;
    private Pattern pattern = Pattern.compile("@.*\\n");
    private List<String> warnInfo = Lists.newArrayList();

    public JavaParser(String javaFile) {
        this.javaFile = javaFile;
        this.model = new JavaAstModel();
    }

    private void addWarnInfo(String info) {
        if (!this.warnInfo.contains(info)) {
            this.warnInfo.add(info);
        }
    }

    public JavaAstModel getModel() {
        return this.model;
    }

    public String getJavaFile() {
        return this.javaFile;
    }

    public void parse() throws IOException, ParseException {

        InputStream in;
        try {
            in = new FileInputStream(javaFile);
        } catch (FileNotFoundException e) {
            in = JavaParser.class.getClassLoader().getResourceAsStream(javaFile);
        }
        CompilationUnit cu;
        try {
            cu = com.github.javaparser.JavaParser.parse(in);
        } finally {
            in.close();
        }

        (new JavaParser.PackageVisitor()).visit(cu, (Object) null);
        (new JavaParser.ImportVisitor()).visit(cu, (Object) null);
        (new JavaParser.EnumVisitor()).visit(cu, (Object) null);
        (new ClazzOrInterfaceVisitor()).visit(cu, (Object) null);
        (new JavaParser.MethodVisitor()).visit(cu, (Object) null);
        (new JavaParser.FieldVisitor()).visit(cu, cu);

        Iterator<String> iterator = this.warnInfo.iterator();
        while (iterator.hasNext()) {
            String warn = iterator.next();
            System.err.println(warn);
        }

        if (this.terminate) {
            System.err.println("convertor产生致命错误,异常退出!!!!!!,比如存在import *.");
            System.exit(8);
        }
    }

    private void useParser(VelocityContext ctx, boolean isParent) {
        Set<String> importList = Sets.newConcurrentHashSet();
        Iterator iterator = this.model.imports.iterator();

        while (iterator.hasNext()) {
            String _import = (String) iterator.next();
            Iterator itor = this.model.depDtos.iterator();

            while (itor.hasNext()) {
                String _depDto = (String) itor.next();
                if (StringUtils.endsWithIgnoreCase(_import, _depDto) && !StringUtils.startsWithIgnoreCase(_import, "\\java")) {
                    importList.add(_import);
                }
            }

            if (isParent && StringUtils.endsWithIgnoreCase(_import, this.model.parents.get(0))) {
                importList.add(_import);
            }
        }
        ctx.put("uses", importList);
    }

    class CheckBaseRequestExtendClazzVisitor extends VoidVisitorAdapter {

        CheckBaseRequestExtendClazzVisitor() {
        }

        public void visit(ClassOrInterfaceDeclaration n, Object arg) {
            boolean addInfo = false;
            if (n.getExtendedTypes() != null) {
                Iterator<ClassOrInterfaceType> itr = n.getExtendedTypes().iterator();

                while (itr.hasNext()) {
                    String tmp = (itr.next()).toString();
                    if (!"BaseRequest".equals(tmp)) {
                        addInfo = true;
                    }
                }
            } else {
                addInfo = true;
            }

            if (addInfo) {
                ((StringBuilder) arg).append("the parameter type [ ").append(n.getName()).append(" ] is not recommended !!， Pls use Request<xx> or (Obj extends BaseRequest) instead.");
            }

        }
    }

    private class MethodVisitor extends VoidVisitorAdapter {

        MethodVisitor() {
        }

        public void visit(MethodDeclaration n, Object arg) {
            Method method = new Method();
            method.name = n.getName().getId();
            if (Boolean.FALSE.equals(CollectionUtils.isEmpty(n.getParameters()))) {
                List<Parameter> comments = n.getParameters();
                Iterator<Parameter> matcher = comments.iterator();

                while (matcher.hasNext()) {
                    Parameter parameter = matcher.next();
                    Field field = new Field();
                    field.setName(parameter.getName().getId());
                    field.setJavaType(TypeUtils.getTypeByName("java", parameter.getType()));
                    if (parameter.getType() instanceof ReferenceType && (parameter.getType()).getElementType() instanceof ClassOrInterfaceType) {
                        ClassOrInterfaceType trueType = (ClassOrInterfaceType) (parameter.getType()).getElementType();
                        field.trueType = trueType.getName().getId();
                    }

                    field.setRequest(StringUtils.isNotEmpty(TypeUtils.getTypeByName("isrequst", parameter.getType())));
                    if (method.parameters == null) {
                        method.parameters = Lists.newArrayList();
                    }

                    method.parameters.add(new Pair(field, field.getName()));
                    if (TypeUtils.normalDtoDep(field.getJavaType())) {
                        if (TypeUtils.isList(field.getJavaType())) {
                            model.depDtos.add(TypeUtils.getWarpType(field.getJavaType()));
                        } else {
                            model.depDtos.add(field.getJavaType());
                        }
                    }

                    if (field.trueType != null && !field.trueType.equals(field.getJavaType())) {
                        method.isGeneric = true;
                    }
                }
            } else {
                method.parameters = null;
            }

            handleResult(method, n);
            if (n.getComment().isPresent()) {
                method.comments = n.getComment().get().getContent();
            } else if (n.getJavadoc().isPresent()) {
                method.comments = n.getJavadoc().get().toComment().getContent();
            }

            if (StringUtils.isNotEmpty(method.getComments())) {
                String comments1 = method.getComments();
                Matcher matcher1 = pattern.matcher(comments1);
                method.comments = matcher1.replaceAll("");
            }

            if (Boolean.FALSE.equals(CollectionUtils.isEmpty(n.getModifiers()))) {
                method.modifiers = n.getModifiers();
            }

            if (n.getBody().isPresent()) {
                method.body = n.getBody().get().toString();
            }

            model.methods.add(method);
        }

        private void handleResult(Method method, MethodDeclaration n) {
            String typeStr = n.getType().toString();
            Field field = new Field();
            field.setJavaType(TypeUtils.getTypeByName("java", n.getType()));
            if (TypeUtils.normalDtoDep(field.getJavaType())) {
                String tmpType = null;
                if (TypeUtils.isList(field.getJavaType())) {
                    tmpType = TypeUtils.getWarpType(field.getJavaType());
                } else {
                    tmpType = field.getJavaType();
                }

                if (!tmpType.startsWith("Map<")) {
                    model.depDtos.add(tmpType);
                }
            }

            if (typeStr.startsWith("Result<")) {
                field.setJavaType(StringUtils.substring(typeStr, 7, typeStr.length() - 1));
                field.enableResult = true;
            }

            method.result = field;
        }
    }

    private class EnumVisitor extends VoidVisitorAdapter {

        EnumVisitor() {
        }

        public void visit(EnumDeclaration n, Object arg) {
            model.isEnum = true;
            warnInfo.add("the enum type of [ " + n.getName() + " ] is not recommended !!! Pls modify this API definition.");
        }
    }

    private class FieldVisitor extends VoidVisitorAdapter {

        FieldVisitor() {
        }

        public void visit(FieldDeclaration n, Object arg) {
            Field field = new Field();
            if (n.getComment().isPresent()) {
                field.comment = n.getComment().get().getContent();
            }

            field.modifiers = n.getModifiers();
            field.javaType = TypeUtils.getTypeByName("java", n.getElementType());
            String wrapJType = TypeUtils.getTypeByName("wrapJType", n.getElementType());
            if (StringUtils.isNotEmpty(wrapJType)) {
                field.wrapJType = wrapJType;
            }

            List variables = n.getVariables();
            if (variables != null && variables.size() > 0) {
                Iterator cu = variables.iterator();

                for (VariableDeclarator variableDeclarator = null; cu.hasNext(); field.name = variableDeclarator.getName().getId()) {
                    variableDeclarator = (VariableDeclarator) cu.next();
                }
            }

            field.enableCheck = true;
            if (TypeUtils.normalDtoDep(field.getJavaType())) {
                if (TypeUtils.isList(field.getJavaType())) {
                    String cu1 = TypeUtils.getWarpType(field.getJavaType());
                    if (cu1.length() == 1) {
                        field.enableCheck = false;
                    }

                    model.depDtos.add(cu1);
                } else {
                    if (field.getJavaType().length() == 1) {
                        field.enableCheck = false;
                    }

                    model.depDtos.add(field.getJavaType());
                }
            }

            if (!StringUtils.equalsIgnoreCase("serialVersionUID", field.name)) {
                model.fields.add(field);
            }

            CompilationUnit cu2 = (CompilationUnit) arg;
            if (StringUtils.equalsIgnoreCase(TypeUtils.getTypeByName("java", n.getElementType()), "Date")) {
                (new VoidVisitorAdapter() {
                    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
                        addWarnInfo(n.getName() + " found Date !!");
                    }
                }).visit(cu2, (Object) null);
            } else if (StringUtils.equalsIgnoreCase(TypeUtils.getTypeByName("java", n.getElementType()), "BigDecimal")) {
                (new VoidVisitorAdapter() {
                    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
                        addWarnInfo(n.getName() + " found BigDecimal !!");
                    }
                }).visit(cu2, (Object) null);
            }

        }
    }

    private class ClazzOrInterfaceVisitor extends VoidVisitorAdapter {
        ClazzOrInterfaceVisitor() {
        }

        public void visit(ClassOrInterfaceDeclaration n, Object arg) {
            model.isInterfazz = n.isInterface();
            model.className = n.getName().getId();
            if (n.getJavadoc().isPresent()) {
                model.comments = n.getJavadoc().toString();
            }

            if (model.comments != null && n.getComment().isPresent()) {
                model.comments = n.getComment().get().getContent();
            }

            Iterator<ClassOrInterfaceType> itr;
            if (Boolean.FALSE.equals(CollectionUtils.isEmpty(n.getExtendedTypes()))) {
                itr = n.getExtendedTypes().iterator();
                while (itr.hasNext()) {
                    model.parents.add((itr.next()).getName().getId());
                }
            }

            if (Boolean.FALSE.equals(CollectionUtils.isEmpty(n.getImplementedTypes()))) {
                itr = n.getExtendedTypes().iterator();
                while (itr.hasNext()) {
                    model.interfazzs.add((itr.next()).getName().getId());
                }
            }
        }
    }

    private class ImportVisitor extends VoidVisitorAdapter {
        ImportVisitor() {
        }

        public void visit(ImportDeclaration n, Object arg) {
            if (n.isAsterisk()) {
                warnInfo.add("---import * 不支持---" + n.getName().toString());
                terminate = true;
            }

            model.imports.add("\\" + StringUtils.replace(n.getName().toString(), ".", "\\"));
            model.importsMap.put(n.getName().getId(), n.getName().toString());
        }
    }

    private class PackageVisitor extends VoidVisitorAdapter {
        PackageVisitor() {
        }

        public void visit(PackageDeclaration n, Object arg) {
            model.packageName = n.getName().toString();
        }
    }
}
