package cn.nikeo.reparatur.gradle

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty

open class CrashWebViewsExtension(objects: ObjectFactory) {
    val qualifiedNames: ListProperty<String> = objects.listProperty(String::class.java)
}
