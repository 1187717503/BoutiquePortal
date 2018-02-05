package com.intramirror.web.util;

import com.intramirror.web.properties.MicroProperties;

/**
 * Created on 2018/2/5.
 *
 * @author YouFeng.Zhu
 */
public class Facility {
    private static Facility ourInstance = new Facility();

    private MicroProperties microProperties;

    public static Facility getInstance() {
        return ourInstance;
    }

    private Facility() {
    }

    public MicroProperties getMicroProperties() {
        return microProperties;
    }

    public void setMicroProperties(MicroProperties microProperties) {
        this.microProperties = microProperties;
    }
}
