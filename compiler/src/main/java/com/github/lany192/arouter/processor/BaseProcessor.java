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

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        logger = new Logger(processingEnv.getMessager());
        options = processingEnv.getOptions();
        String value = options.get("APP_ROUTER_DEBUG");
        logger.setDebug(Boolean.parseBoolean(value));
        logger.info("初始化");
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

}
