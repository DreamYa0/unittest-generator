package com.zeratul.plugin.java;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zeratul.plugin.util.TypeUtils;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.*;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;

import java.io.FileInputStream;
import java.io.IOException;
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

        FileInputStream in = new FileInputStream(this.javaFile);
        CompilationUnit cu;
        try {
            cu = japa.parser.JavaParser.parse(in);
        } finally {
            in.close();
        }

        (new JavaParser.PackageVisitor()).visit(cu, (Object) null);
        (new JavaParser.ImportVisitor()).visit(cu, (Object) null);
        (new JavaParser.EnumVisitor()).visit(cu, (Object) null);
        (new ClazzOrInterfaceVisitor()).visit(cu, (Object) null);
        (new JavaParser.MethodVisitor()).visit(cu, (Object) null);
        (new JavaParser.FieldVisitor()).visit(cu, cu);
        Iterator iterator = this.warnInfo.iterator();

        while (iterator.hasNext()) {
            String warn = (String) iterator.next();
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
            if (n.getExtends() != null) {
                Iterator itr = n.getExtends().iterator();

                while (itr.hasNext()) {
                    String tmp = ((ClassOrInterfaceType) itr.next()).toString();
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
            method.name = n.getName();
            if (n.getParameters() != null) {
                List comments = n.getParameters();
                Iterator matcher = comments.iterator();

                while (matcher.hasNext()) {
                    Parameter parameter = (Parameter) matcher.next();
                    Field field = new Field();
                    field.setName(parameter.getId().getName());
                    field.setJavaType(TypeUtils.getTypeByName("java", parameter.getType()));
                    if (parameter.getType() instanceof ReferenceType && ((ReferenceType) parameter.getType()).getType() instanceof ClassOrInterfaceType) {
                        ClassOrInterfaceType trueType = (ClassOrInterfaceType) ((ReferenceType) parameter.getType()).getType();
                        field.trueType = trueType.getName();
                    }

                    field.setRequest(StringUtils.isNotEmpty(TypeUtils.getTypeByName("isrequst", parameter.getType())));
                    if (method.parameters == null) {
                        method.parameters = Lists.newArrayList();
                    }

                    method.parameters.add(new Pair(field, field.getName()));
                    if (TypeUtils.normalDtoDep(field.getJavaType())) {
                        if (TypeUtils.isList(field.getJavaType())) {
                            JavaParser.this.model.depDtos.add(TypeUtils.getWarpType(field.getJavaType()));
                        } else {
                            JavaParser.this.model.depDtos.add(field.getJavaType());
                        }
                    }

                    if (field.trueType != null && !field.trueType.equals(field.getJavaType())) {
                        method.isGeneric = true;
                    }
                }
            } else {
                method.parameters = null;
            }

            this.handleResult(method, n);
            if (n.getComment() != null) {
                method.comments = n.getComment().getContent();
            } else if (null != n.getJavaDoc()) {
                method.comments = n.getJavaDoc().getContent();
            }

            if (StringUtils.isNotEmpty(method.getComments())) {
                String comments1 = method.getComments();
                Matcher matcher1 = JavaParser.this.pattern.matcher(comments1);
                method.comments = matcher1.replaceAll("");
            }

            if (n.getModifiers() > 0) {
                method.modifiers = n.getModifiers();
            }

            if (n.getBody() != null) {
                method.body = n.getBody().toString();
            }

            JavaParser.this.model.methods.add(method);
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
                    JavaParser.this.model.depDtos.add(tmpType);
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
            JavaParser.this.model.isEnum = true;
            JavaParser.this.warnInfo.add("the enum type of [ " + n.getName() + " ] is not recommended !!! Pls modify this API definition.");
        }
    }

    private class FieldVisitor extends VoidVisitorAdapter {

        FieldVisitor() {
        }

        public void visit(FieldDeclaration n, Object arg) {
            Field field = new Field();
            if (n.getComment() != null) {
                field.comment = n.getComment().getContent();
            }

            field.modifiers = n.getModifiers();
            field.javaType = TypeUtils.getTypeByName("java", n.getType());
            String wrapJType = TypeUtils.getTypeByName("wrapJType", n.getType());
            if (StringUtils.isNotEmpty(wrapJType)) {
                field.wrapJType = wrapJType;
            }

            List variables = n.getVariables();
            if (variables != null && variables.size() > 0) {
                Iterator cu = variables.iterator();

                for (VariableDeclarator variableDeclarator = null; cu.hasNext(); field.name = variableDeclarator.getId().getName()) {
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

                    JavaParser.this.model.depDtos.add(cu1);
                } else {
                    if (field.getJavaType().length() == 1) {
                        field.enableCheck = false;
                    }

                    JavaParser.this.model.depDtos.add(field.getJavaType());
                }
            }

            if (!StringUtils.equalsIgnoreCase("serialVersionUID", field.name)) {
                JavaParser.this.model.fields.add(field);
            }

            CompilationUnit cu2 = (CompilationUnit) arg;
            if (StringUtils.equalsIgnoreCase(TypeUtils.getTypeByName("java", n.getType()), "Date")) {
                (new VoidVisitorAdapter() {
                    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
                        JavaParser.this.addWarnInfo(n.getName() + " found Date !!");
                    }
                }).visit(cu2, (Object) null);
            } else if (StringUtils.equalsIgnoreCase(TypeUtils.getTypeByName("java", n.getType()), "BigDecimal")) {
                (new VoidVisitorAdapter() {
                    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
                        JavaParser.this.addWarnInfo(n.getName() + " found BigDecimal !!");
                    }
                }).visit(cu2, (Object) null);
            }

        }
    }

    private class ClazzOrInterfaceVisitor extends VoidVisitorAdapter {
        ClazzOrInterfaceVisitor() {
        }

        public void visit(ClassOrInterfaceDeclaration n, Object arg) {
            JavaParser.this.model.isInterfazz = n.isInterface();
            JavaParser.this.model.className = n.getName();
            if (n.getJavaDoc() != null) {
                JavaParser.this.model.comments = n.getJavaDoc().toString();
            }

            if (JavaParser.this.model.comments != null) {
                JavaParser.this.model.comments = n.getComment().getContent();
            }

            Iterator<ClassOrInterfaceType> itr;
            if (n.getExtends() != null) {
                itr = n.getExtends().iterator();

                while (itr.hasNext()) {
                    JavaParser.this.model.parents.add((itr.next()).getName());
                }
            }

            if (n.getImplements() != null) {
                itr = n.getImplements().iterator();

                while (itr.hasNext()) {
                    JavaParser.this.model.interfazzs.add((itr.next()).getName());
                }
            }

        }
    }

    private class ImportVisitor extends VoidVisitorAdapter {
        ImportVisitor() {
        }

        public void visit(ImportDeclaration n, Object arg) {
            if (n.isAsterisk()) {
                JavaParser.this.warnInfo.add("---import * 不支持---" + n.getName().toString());
                JavaParser.this.terminate = true;
            }

            JavaParser.this.model.imports.add("\\" + StringUtils.replace(n.getName().toString(), ".", "\\"));
            JavaParser.this.model.importsMap.put(n.getName().getName(), n.getName().toString());
        }
    }

    private class PackageVisitor extends VoidVisitorAdapter {
        PackageVisitor() {
        }

        public void visit(PackageDeclaration n, Object arg) {
            JavaParser.this.model.packageName = n.getName().toString();
        }
    }
}
