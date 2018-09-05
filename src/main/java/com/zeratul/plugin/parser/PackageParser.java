package com.zeratul.plugin.parser;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.List;

/**
 * @author dreamyao
 * @title
 * @date 2018/4/12 下午10:33
 * @since 1.0.0
 */
public class PackageParser {

    private String path;
    private List<Node> nodes = Lists.newArrayList();

    public PackageParser(String path) {
        this.path = path;
    }

    private PackageParser() {
    }

    public List<Node> getNodes() {
        return this.nodes;
    }

    public void parse() {
        File dirFile = new File(this.path);
        if (dirFile.exists() && dirFile.isDirectory()) {
            File[] files = dirFile.listFiles();
            if (null != files && files.length != 0) {
                this.parseAllNode(this.path);
            } else {
                throw new RuntimeException("目录下不存在文件:" + this.path);
            }
        } else {
            throw new RuntimeException("目录不存在或不是目录:" + this.path);
        }
    }

    private void parseAllNode(String path) {
        File dirFile = new File(path);
        File[] files = dirFile.listFiles();
        FlatNode flatNode = new FlatNode(path);
        int len = files.length;

        for (int i = 0; i < len; ++i) {
            File file = files[i];
            if (file.isDirectory()) {
                String subPath = file.getAbsolutePath();
                parseAllNode(subPath);
            } else if (file.getName().contains(".java") && !StringUtils.containsIgnoreCase(file.getName(), "package-info")) {
                flatNode.getFiles().add(file.getAbsolutePath());
            }
        }

        nodes.add(flatNode);
    }
}
