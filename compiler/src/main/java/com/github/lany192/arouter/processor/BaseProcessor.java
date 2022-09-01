package com.github.lany192.arouter.processor;

import com.github.lany192.arouter.Logger;

import java.util.Map;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;

public abstract class BaseProcessor extends AbstractProcessor {
    /**
     * 日志打印
     */
    protected Logger logger;
    /**
     * 配置传递的参数
     */
    protected Map<String, String> options;
    //模块名称
    public final String MODULE_NAME = "AROUTER_MODULE_NAME";
    //是否debug模式
    public final String ROUTER_DEBUG = "ROUTER_DEBUG";
    //是否打印JS路由文档
    public final String JS_ROUTER_DOC = "JS_ROUTER_DOC";
    //Uri Scheme标识
    public final String ROUTER_SCHEME = "ROUTER_SCHEME";
    //JS路由调用方法
    public final String ROUTER_JS_FUN = "ROUTER_JS_FUN";

    protected String module;
    protected Boolean debug;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        logger = new Logger(processingEnv.getMessager());
        options = processingEnv.getOptions();
        debug = Boolean.parseBoolean(getValue(ROUTER_DEBUG));
        module = getValue(MODULE_NAME);

        logger.setDebug(debug);
        logger.info("初始化");
        logger.info("模块名称：" + module);
        logger.info("ROUTER_DEBUG：" + getValue(ROUTER_DEBUG));
        logger.info("JS_ROUTER_DOC：" + getValue(JS_ROUTER_DOC));
        logger.info("ROUTER_SCHEME：" + getValue(ROUTER_SCHEME));
        logger.info("ROUTER_JS_FUN：" + getValue(ROUTER_JS_FUN));
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    public String getValue(String key) {
        return options.get(key);
    }
}
