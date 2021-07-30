package com.github.lany192.arouter;

import com.squareup.javapoet.ClassName;

public class Consts {
    public static final String PROJECT = "\nAppRouter";

    // Java type
    private static final String LANG = "java.lang";
    public static final String BYTE = LANG + ".Byte";
    public static final String SHORT = LANG + ".Short";
    public static final String INTEGER = LANG + ".Integer";
    public static final String LONG = LANG + ".Long";
    public static final String FLOAT = LANG + ".Float";
    public static final String DOUBEL = LANG + ".Double";
    public static final String BOOLEAN = LANG + ".Boolean";
    public static final String CHAR = LANG + ".Character";
    public static final String STRING = LANG + ".String";
    public static final String CHARSEQUENCE = LANG + ".CharSequence";
    public static final String SERIALIZABLE = "java.io.Serializable";
    public static final String PARCELABLE = "android.os.Parcelable";


    // Custom interface
    private static final String FACADE_PACKAGE = "com.alibaba.android.arouter.facade";
    private static final String TEMPLATE_PACKAGE = ".template";
    public static final String IPROVIDER = FACADE_PACKAGE + TEMPLATE_PACKAGE + ".IProvider";

    // Log
    static final String PREFIX_OF_LOGGER = PROJECT + " -> ";


    // System interface
    public static final String ACTIVITY = "android.app.Activity";
    public static final String FRAGMENT = "android.app.Fragment";
    public static final String FRAGMENT_V4 = "android.support.v4.app.Fragment";
    public static final String FRAGMENT_X = "androidx.fragment.app.Fragment";
    public static final String SERVICE = "android.app.Service";
}