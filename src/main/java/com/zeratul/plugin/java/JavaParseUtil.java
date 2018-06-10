package com.zeratul.plugin.java;

import com.google.common.collect.Lists;
import com.zeratul.plugin.parser.Node;
import com.zeratul.plugin.parser.PackageParser;

import java.util.Iterator;
import java.util.List;

/**
 * @author dreamyao
 * @title
 * @date 2018/4/12 下午10:27
 * @since 1.0.0
 */
public class JavaParseUtil {

    public static List<JavaParser> parse(String path) {
        List<JavaParser> javas = Lists.newArrayList();
        if(path != null && !path.equals("")) {
            PackageParser packageParser = new PackageParser(path);
            packageParser.parse();
            List<Node> nodes = packageParser.getNodes();
            Iterator<Node> iterator = nodes.iterator();

            while(true) {
                Node node;
                do {
                    if(!iterator.hasNext()) {
                        return javas;
                    }

                    node = iterator.next();
                } while(!node.hasFile());

                Iterator<String> ite = node.getFiles().iterator();

                while(ite.hasNext()) {
                    String javaFile = ite.next();

                    try {
                        JavaParser javaParser = new JavaParser(javaFile);
                        javaParser.parse();
                        javas.add(javaParser);
                    } catch (Exception e) {
                    }
                }
            }
        } else {
            return javas;
        }
    }
}
