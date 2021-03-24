[![](https://jitpack.io/v/lany192/app-router.svg)](https://jitpack.io/#lany192/app-router)
# AppRouter
    基于Arouter的增强工具
    
# 项目根build.gradle引入 
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
# app模块build.gradle引入 
    dependencies {
        annotationProcessor 'com.github.lany192:app-router:1.0.0'
    }

### 调用方法
    
    AppRouter.get().one(66, true, 10.5f, 'w', "哈哈", (byte) 1, "流利")
