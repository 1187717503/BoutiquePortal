package com.intramirror.order.core.properties;

/**
 * Created on 2019/1/20.
 *
 * @author yfding
 */
public class CcZhangEmailProperties {

    private String confirmedFrom;
    private String confirmedPassword;
    private String confirmedTo;

    private String shippedFrom;
    private String shippedPassword;
    private String shippedTo;

    private String path;

    public String getConfirmedFrom() {
        return confirmedFrom;
    }

    public void setConfirmedFrom(String confirmedFrom) {
        this.confirmedFrom = confirmedFrom;
    }

    public String getConfirmedPassword() {
        return confirmedPassword;
    }

    public void setConfirmedPassword(String confirmedPassword) {
        this.confirmedPassword = confirmedPassword;
    }

    public String getConfirmedTo() {
        return confirmedTo;
    }

    public void setConfirmedTo(String confirmedTo) {
        this.confirmedTo = confirmedTo;
    }

    public String getShippedFrom() {
        return shippedFrom;
    }

    public void setShippedFrom(String shippedFrom) {
        this.shippedFrom = shippedFrom;
    }

    public String getShippedPassword() {
        return shippedPassword;
    }

    public void setShippedPassword(String shippedPassword) {
        this.shippedPassword = shippedPassword;
    }

    public String getShippedTo() {
        return shippedTo;
    }

    public void setShippedTo(String shippedTo) {
        this.shippedTo = shippedTo;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
