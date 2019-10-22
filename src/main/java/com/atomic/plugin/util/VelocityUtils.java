package com.atomic.plugin.util;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 * @author dreamyao
 * @title
 * @date 2018/4/12 下午10:34
 * @since 1.0.0
 */
public class VelocityUtils {

    public static VelocityEngine getVelocityEngine() {
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty("resource.loader", "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.setProperty("input.encoding", "utf-8");
        ve.setProperty("output.encoding", "utf-8");
        ve.init();
        return ve;
    }
}
