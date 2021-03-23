package com.github.lany192.arouter.entity;


import com.alibaba.fastjson.annotation.JSONField;

import org.apache.commons.lang3.StringUtils;

public class Param {
    @JSONField(ordinal = 1)
    private String key;
    @JSONField(ordinal = 2)
    private String type;
    @JSONField(ordinal = 3)
    private String description;
    @JSONField(ordinal = 4)
    private boolean required;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (StringUtils.isNotEmpty(description)) {
            this.description = description;
        }
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
