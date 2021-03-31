package com.ntocc.autotest;

import com.atomic.plugin.generator.GenerateModule;
import com.g7.ntocc.api.PayService;

public class TestCaseGenerate {
    public static void main(String[] args) {
        // 生成dubbo接口测试代码
        // ConsigneeService.class
        new GenerateModule(PayService.class);
        // 生成rest接口测试代码
        // HttpGenerator.createRestApiCase("electronicReceipt", "cashdeskTruckBroker");
    }
}
