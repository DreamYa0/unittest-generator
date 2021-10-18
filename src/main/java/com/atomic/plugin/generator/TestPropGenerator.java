package com.atomic.plugin.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * @author dreamyao
 * @title
 * @date 2021/10/18 2:58 下午
 * @since 1.0.0
 */
public class TestPropGenerator {

    public static void generateTestProperties() {
        File f = new File("src/test/resources/test.properties");
        if (!f.exists()) {
            try {
                f.getParentFile().mkdirs();
                BufferedWriter bw = new BufferedWriter(new FileWriter(f));

                bw.write("#测试项目名称(必填)");
                bw.newLine();
                bw.write("project.name=");
                bw.newLine();

                bw.write("#测试执行人(必填)");
                bw.newLine();
                bw.write("runner=");
                bw.newLine();

                bw.write("#dubbo zookeeper地址(dubbo接口测试必填)");
                bw.newLine();
                bw.write("dubbo.zookeeper=");
                bw.newLine();

                bw.write("#dubbo提供者直连地址(选填)");
                bw.newLine();
                bw.write("dubbo.provider.host=");
                bw.newLine();

                bw.write("#dubbo接口版本(选填)");
                bw.newLine();
                bw.write("dubbo.service.version=");
                bw.newLine();

                bw.write("#dubbo服务组(选填)");
                bw.newLine();
                bw.write("dubbo.service.group=");
                bw.newLine();

                bw.write("#http接口域名地址(http接口测试必填)");
                bw.newLine();
                bw.write("http.host=");
                bw.newLine();

                bw.write("#http公共请求头(选填)");
                bw.newLine();
                bw.write("http.header=");
                bw.newLine();

                bw.write("#数据库地址IP+端口(必填)");
                bw.newLine();
                bw.write("database.url=");
                bw.newLine();

                bw.write("#数据库用户名(必填)");
                bw.newLine();
                bw.write("database.username=");
                bw.newLine();

                bw.write("#数据库密码(必填)");
                bw.newLine();
                bw.write("database.password=");
                bw.newLine();

                bw.write("#运行指定测试包(选填)");
                bw.newLine();
                bw.write("run.test.packages=");
                bw.newLine();

                bw.write("#运行指定测试分组(选填)");
                bw.newLine();
                bw.write("run.test.groups=");
                bw.newLine();

                bw.write("#运行指定测试类(选填)");
                bw.newLine();
                bw.write("run.test.classes=");
                bw.newLine();

                bw.flush();
                bw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
