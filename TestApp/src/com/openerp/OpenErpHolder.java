package com.openerp;

import java.util.HashMap;
import java.util.List;

/**
 * Singleton Class to manage connections and data
 */
public class OpenErpHolder {
    private static final OpenErpHolder instance = new OpenErpHolder();

	private OpenErpConnect mOConn;
	private String mModelName;
    private List<HashMap<String,Object>> mData;
    private HashMap<String, Object> mFieldsAttributes;
    private Class mClassTreeActivity;
    private Class mClassFormActivity;

    public Class getmClassTreeActivity() {
        return mClassTreeActivity;
    }

    public void setmClassTreeActivity(Class mClassTreeActivity) {
        this.mClassTreeActivity = mClassTreeActivity;
    }

    public Class getmClassFormActivity() {
        return mClassFormActivity;
    }

    public void setmClassFormActivity(Class mClassFormActivity) {
        this.mClassFormActivity = mClassFormActivity;
    }

    public enum OoType {
        BOOLEAN, INTEGER, FLOAT, CHAR, TEXT, DATE, DATETIME, BINARY, SELECTION, ONE2ONE, MANY2ONE, ONE2MANY, MANY2MANY, RELATED,
    }

    public HashMap<String, Object> getmFieldsAttributes() {
        return mFieldsAttributes;
    }

    protected OpenErpHolder(){
    }
    public static synchronized OpenErpHolder getInstance() {
        return instance;
    }
    public void setmFieldsAttributes(HashMap<String, Object> mFieldsAttributes) {
        this.mFieldsAttributes = mFieldsAttributes;
    }

    public List<HashMap<String, Object>> getmData() {
        return mData;
    }

    public void setmData(List<HashMap<String, Object>> mData) {
        this.mData = mData;
    }

    public String getmModelName() {
        return mModelName;
    }

    public void setmModelName(String mModelName) {
        this.mModelName = mModelName;
    }

    public OpenErpConnect getmOConn() {
        return mOConn;
    }

    public void setmOConn(OpenErpConnect mOConn) {
        this.mOConn = mOConn;
    }
}


