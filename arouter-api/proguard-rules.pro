#arouter混淆----------------------------------------------
-keep public class com.alibaba.android.arouter.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}
-keep interface * implements com.alibaba.android.arouter.facade.template.IProvider
-keep class * implements com.alibaba.android.arouter.facade.template.IProvider
-dontwarn javax.lang.model.element.Element
#----------------------------------------------
