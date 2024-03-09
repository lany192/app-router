package com.alibaba.android.arouter.compiler.other.processor;

import com.alibaba.android.arouter.compiler.other.Constants;
import com.alibaba.android.arouter.compiler.utils.Logger;
import com.alibaba.fastjson.JSON;

import org.apache.commons.lang3.StringUtils;

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
    private Map<String, String> options;
    /**
     * 当前模块名称
     */
    protected String module;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        logger = new Logger(processingEnv.getMessager());
        options = processingEnv.getOptions();
        String configInfo = JSON.toJSONString(options);

        logger.setDebug(Boolean.parseBoolean(getValue(Constants.ROUTER_DEBUG, "true")));
        logger.info("初始化......");
        logger.info("配置信息：" + configInfo);

        module = getValue(Constants.MODULE_NAME, "");
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    public String getValue(String key) {
        return getValue(key, "");
    }

    public boolean getBooleanValue(String key) {
        String value = options.get(key);
        if (StringUtils.isEmpty(value)) {
            value = "false";
        }
        return Boolean.parseBoolean(value);
    }

    public String getValue(String key, String defaultValue) {
        String value = options.get(key);
        if (StringUtils.isEmpty(value)) {
            value = defaultValue;
        }
        return value;
    }
}
