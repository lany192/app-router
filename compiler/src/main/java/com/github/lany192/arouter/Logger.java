package com.github.lany192.arouter;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public class Logger {
    private final Messager msg;
    /**
     * 调试时手动修改
     */
    private boolean debug;

    public Logger(Messager messager) {
        msg = messager;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Print info log.
     */
    public void info(CharSequence info) {
        if (debug) {
            if (StringUtils.isNotEmpty(info)) {
                msg.printMessage(Diagnostic.Kind.NOTE, Consts.PREFIX_OF_LOGGER + info);
            }
        }
    }

    public void error(CharSequence error) {
        if (debug) {
            if (StringUtils.isNotEmpty(error)) {
                msg.printMessage(Diagnostic.Kind.ERROR, Consts.PREFIX_OF_LOGGER + "An exception is encountered, [" + error + "]");
            }
        }
    }

    public void error(Throwable error) {
        if (debug) {
            if (null != error) {
                msg.printMessage(Diagnostic.Kind.ERROR, Consts.PREFIX_OF_LOGGER + "An exception is encountered, [" + error.getMessage() + "]" + "\n" + formatStackTrace(error.getStackTrace()));
            }
        }
    }

    public void warning(CharSequence warning) {
        if (debug) {
            if (StringUtils.isNotEmpty(warning)) {
                msg.printMessage(Diagnostic.Kind.WARNING, Consts.PREFIX_OF_LOGGER + warning);
            }
        }
    }

    private String formatStackTrace(StackTraceElement[] stackTrace) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : stackTrace) {
            sb.append("    at ").append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
