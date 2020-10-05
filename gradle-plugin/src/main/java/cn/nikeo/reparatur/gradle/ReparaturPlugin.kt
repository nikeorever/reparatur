package cn.nikeo.reparatur.gradle

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.AndroidBasePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class ReparaturPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val hasApp = project.plugins.hasPlugin(AppPlugin::class.java)
        require(hasApp) {
            "The android app plugin `com.android.application` must be applied before ``"
        }

        val crashWebViews = project.extensions.create("crashWebViews", CrashWebViewsExtension::class.java)

        project.extensions.getByType(BaseExtension::class.java)
            .registerTransform(ReparaturTransform(crashWebViews))
    }
}