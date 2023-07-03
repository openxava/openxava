package org.openxava.formatters;

import org.openxava.model.meta.MetaProperty;

import javax.servlet.http.HttpServletRequest;


public class BooleanListFormatter implements IMetaPropertyFormatter {

    public String format(HttpServletRequest request, MetaProperty p, Object booleanValue) {

        if (booleanValue == null) {
            return "";
        }
        return toBoolean(booleanValue)? "Yes" : "No";
    }

    public Object parse(HttpServletRequest request, MetaProperty p, String string) {
        return null; // Not needed for list
    }

    static boolean toBoolean(Object booleanValue) {
        if (booleanValue instanceof Boolean) {
            return ((Boolean) booleanValue).booleanValue();

        }
        if (booleanValue instanceof Number) {
            return ((Number) booleanValue).intValue() != 0;
        }
        return false;
    }

}
