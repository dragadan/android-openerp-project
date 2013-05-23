package com.openerp;

public class OpenErpHolder {
	public static OpenErpConnect oc;
	public static String modelName;
    public enum OoType {
        BOOLEAN, INTEGER, FLOAT, CHAR, TEXT, DATE, DATETIME, BINARY, SELECTION, ONE2ONE, MANY2ONE, ONE2MANY, MANY2MANY, RELATED,
    }
}
