package io.github.mojtaba.microservice.starter.saga.orchestrator.common;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.ResourceBundle;

public class ErrorCodeReaderUtil {
    static ResourceBundle rb = ResourceBundle.getBundle("errorcodes");

    public static String getResourceProperity(String key) {
        try {
            return rb.getString(key);
        } catch (Exception e) {
//            return MessageFormat.format("UN_HANDLED_EXCEPTION_KEY : {0}", key);
            return MessageFormat.format("{0} : {1}", rb.getString("UNHANDLED_EXCEPTION"), key);
        }

    }

    public static Enumeration<String> getResourceKeys(String key) {
        return rb.getKeys();
    }

}
