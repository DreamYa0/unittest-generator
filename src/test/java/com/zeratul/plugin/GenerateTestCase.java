package com.zeratul.plugin;

import com.ntocc.invoice.api.service.InvoiceTitleService;
import com.zeratul.plugin.generator.GenerateModule;

/**
 * @author dreamyao
 * @title
 * @date 2019-05-09 10:15
 * @since 1.0.0
 */
public class GenerateTestCase {

    public static void main(String[] args) {
        new GenerateModule(InvoiceTitleService.class);
    }
}
