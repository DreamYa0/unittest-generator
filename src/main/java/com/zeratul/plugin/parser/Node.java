package com.zeratul.plugin.parser;

import java.util.List;

/**
 * @author dreamyao
 * @title
 * @date 2018/4/12 下午10:33
 * @since 1.0.0
 */
public interface Node {

    String getPath();

    List<String> getFiles();

    boolean hasFile();
}
