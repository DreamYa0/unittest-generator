package com.atomic.plugin.generator;

class HttpCase {
    /**
     * 测试用例标题
     *
     */
    private String caseName;

    /**
     * 测试用例开关，运行测试用例
     *
     */
    private boolean testOnly;

    /**
     * 测试用例Http请求类型
     *
     */
    private String mode;

    /**
     * 测试用例Http请求host
     *
     */
    private String host;

    /**
     * 测试用例Http请求method
     *
     */
    private String method;

    /**
     * 测试用例入参数据
     *
     */
    private String data;

    /**
     * 测试用例欲求返回结果
     *
     */
    private String expectResult;

    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    public boolean isTestOnly() {
        return testOnly;
    }

    public void setTestOnly(boolean testOnly) {
        this.testOnly = testOnly;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getExpectResult() {
        return expectResult;
    }

    public void setExpectResult(String expectResult) {
        this.expectResult = expectResult;
    }

    private HttpCase() {
        this.caseName = "用例标题";
        this.testOnly = true;
        this.mode = "";
        this.host = "";
        this.method = "";
        this.data = "";
        this.expectResult = "";
    }

    private static class HttpCaseClassIntance {
        private static final HttpCase instance = new HttpCase();
    }

    public static HttpCase getInstance() {
        return HttpCase.HttpCaseClassIntance.instance;
    }
}
