package com.zeratul.plugin.parser;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author dreamyao
 * @title
 * @date 2018/4/12 下午10:33
 * @since 1.0.0
 */
public class FlatNode implements Node {

    private String path;
    private List<String> files = Lists.newArrayList();

    public FlatNode(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public List<String> getFiles() {
        return this.files;
    }

    public boolean hasFile() {
        return !this.files.isEmpty();
    }

    public void addFile(String filename) {
        if (filename != null) {
            this.files.add(filename);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("FlatNode{");
        sb.append("path=\'").append(this.path).append('\'');
        sb.append(", files=").append(this.files);
        sb.append('}');
        return sb.toString();
    }
}
