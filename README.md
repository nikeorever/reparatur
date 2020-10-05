reparatur
===========
A plugin that helps you fix the crash of webview in Android Lollipop(API 22).
```
Caused by: android.content.res.Resources$NotFoundException: String resource ID #0x2040003
```

**Support Incremental Builds**
wnload
--------

#### Error: 
Due to the configuration problem of the gradle plugin, please **do not** use version **1.0.0**.

#### Top-level build file
```groovy
buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath "cn.nikeo.reparatur:gradle-plugin:1.1.0"
    }
}
```

#### App-module build file
```groovy
apply plugin: 'com.android.application'
apply plugin: "cn.nikeo.reparatur"

crashWebViews {
    qualifiedNames = ["cn.nikeo.reparaturapplication.CustomWebView"]
}
```

License
-------

Apache License, Version 2.0, ([LICENSE](https://github.com/nikeorever/reparatur/blob/trunk/LICENSE) or [https://www.apache.org/licenses/LICENSE-2.0](https://www.apache.org/licenses/LICENSE-2.0))

