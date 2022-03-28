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
        annotationProcessor 'com.github.lany192:app-router:+'
    }

# 自定义参数配置

    kapt {
        arguments {
            arg("AROUTER_MODULE_NAME", project.getName())
            //是否debug模式
            arg("ROUTER_DEBUG", true)
            //是否打印JS路由文档
            arg("JS_ROUTER_DOC", true)
            //Uri Scheme标识
            arg("ROUTER_SCHEME", "gamekipo")
            //JS路由调用方法
            arg("ROUTER_JS_FUN", "window.app.route")
        }
    }

### 调用方法，编译后会自动生成AppRouter类，收集了使用了@Route注解的控件调整方法
    
    //调整到one这个Activity界面
    AppRouter.startOne(66, true, 10.5f, 'w', "哈哈", (byte) 1, "流利")
    
    //获取HelloFragment对象实例
    HelloFragment fragment = AppRouter.getHelloFragment("张无忌");
    
#反馈  如有任何问题或者疑问，欢迎提问 https://github.com/lany192/app-router/issues
