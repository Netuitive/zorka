package com.jitlogic.zorka.common.util;

import java.util.Properties;

/**
 *
 * @author john.king
 */
public class ZorkaProperties extends Properties{

    public ZorkaProperties() {
    }

    public ZorkaProperties(Properties defaults) {
        super(defaults);
    }

    @Override
    public String getProperty(String key) {
        String systemKey = key.toUpperCase().replaceAll("\\.", "_");
        String systemVar = System.getenv(systemKey);
        if(systemVar != null && ! systemVar.isEmpty()){
            return systemVar;
        }
        String javaVar = System.getProperty(key);
        if(javaVar != null && !javaVar.isEmpty()){
            return javaVar;
        }
        return super.getProperty(key);
    }
}
