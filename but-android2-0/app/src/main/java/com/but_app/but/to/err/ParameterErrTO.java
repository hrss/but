package com.but_app.but.to.err;

/**
 * Created by iGor Montella on 07/10/2014.
 */
public class ParameterErrTO {

    private String expectedType;
    private String offendingValue;

    public String getExpectedType() {
        return expectedType;
    }

    public void setExpectedType(String expectedType) {
        this.expectedType = expectedType;
    }

    public String getOffendingValue() {
        return offendingValue;
    }

    public void setOffendingValue(String offendingValue) {
        this.offendingValue = offendingValue;
    }


}
