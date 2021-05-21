package me.foolishchow.android.plugin.multimodule

import com.android.build.gradle.api.AndroidSourceSet
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.android.ide.common.repository.main
import org.gradle.api.Project

/**
 * Description:
 * Author: foolishchow
 * Date: 2021/05/21 3:20 PM
 */
interface OnModuleIncludeListener {
    fun addIncludeModule(moduleName: String)
}

interface IMultiModulePluginExtension {
    fun include(name: String)
    fun getProjects(): Set<String>
    fun addModuleIncludeListener(listener: OnModuleIncludeListener)
}


open class MultiModulePluginExtension(
        private var project: Project
) : IMultiModulePluginExtension {

    val android: BaseAppModuleExtension = project.extensions.getByName("android") as BaseAppModuleExtension
    val mModules = mutableSetOf<String>()

    override fun include(name: String) {
        println("include $name")
        mModules.add(name)
    }

    override fun getProjects(): Set<String> {
        return mutableSetOf()
    }

    override fun addModuleIncludeListener(listener: OnModuleIncludeListener) {
        listener.addIncludeModule("")
    }


    private fun getVariantName(): List<String> {
        if (android.productFlavors.isEmpty()) return mutableListOf<String>()
        val flavorList = mutableMapOf<String, MutableSet<String>>()
        android.productFlavors.forEach { flavor ->
            val dimension = flavor.dimension
            if (!flavorList.containsKey(dimension)) {
                flavorList[dimension] = mutableSetOf<String>()
            }
            flavorList[dimension]!!.add(flavor.name)
        }
        val dimension = android.flavorDimensionList

        val suffixList = arrayOfNulls<Array<String>>(dimension.size)
        dimension.forEachIndexed { index, name ->
            val dimens = flavorList[name]
            if (dimens != null) {
                suffixList[index] = dimens.toTypedArray()
            } else {
                suffixList[index] = arrayOf()
            }
        }


        val joined = mutableSetOf<String>()
        suffixList.forEach { now ->
            val current = now ?: arrayOf()
            val result = mutableListOf<String>()
            if (joined.isNotEmpty() && current.isNotEmpty()) {
                for (before in joined) {
                    for (after in current) {
                        result.add("$before${after.capitalize()}")
                    }
                }
            } else if (joined.isNotEmpty()) {
                result.addAll(joined)
            } else if (current.isNotEmpty()) {
                result.addAll(current)
            }
            joined.clear()
            joined.addAll(result.toTypedArray())
        }

        suffixList.forEachIndexed { index, values ->
            values?.forEach { value ->
                joined.add(value)
            }
        }
        return joined.toList()
    }

    init {

        android.applicationVariants.configureEach {
            println("configureEach $name")
            mModules.forEach { module ->
                //addSourceSet(module, this)
            }
        }

        project.afterEvaluate {
            val variantName = getVariantName()
            variantName.forEachIndexed { index, values ->
                println(values)
            }




            println("")
            println("")


            mModules.forEach { module ->
                addSourceSet(module, variantName)
                //sourceSet.java.srcDir("$module/src/main/java")
                //sourceSet.res.srcDir("$module/src/main/res")
            }

            android.sourceSets.forEach {
                println(it.name)
                println(it.java.srcDirs)
            }
            println("")
            println("")
        }
    }

    /**
     * ( `` | `test` ) **variantName** ( `Debug` | `Release` )
     */
    private fun addSourceSet(
            module: String,
            variants: List<String>
    ) {
        //val sourceSet = android.sourceSets.maybeCreate(variant)

        addSrc(android.sourceSets.maybeCreate("main"), module, "main")
        addSrc(android.sourceSets.maybeCreate("debug"), module, "debug")
        addSrc(android.sourceSets.maybeCreate("release"), module, "release")

        variants.forEach { variant ->
            addSrc(android.sourceSets.findByName(
                    "androidTest${variant.capitalize()}"),
                    module,
                    "androidTest${variant.capitalize()}"
            )
            addSrc(android.sourceSets.findByName(
                    "androidTest${variant.capitalize()}Debug"),
                    module,
                    "androidTest${variant.capitalize()}Debug")

            addSrc(android.sourceSets.findByName(variant), module, variant)
            addSrc(android.sourceSets.findByName("${variant}Debug"), module, "${variant}Debug")
            addSrc(android.sourceSets.findByName("${variant}Release"), module, "${variant}Release")


            addSrc(android.sourceSets.findByName("test${variant.capitalize()}"),
                    module,
                    "test${variant.capitalize()}")

            addSrc(android.sourceSets.findByName("test${variant.capitalize()}Debug"),
                    module,
                    "test${variant.capitalize()}Debug")

            addSrc(android.sourceSets.findByName("test${variant.capitalize()}Release"),
                    module,
                    "test${variant.capitalize()}Release")
        }
    }

    private fun addSrc(sourceSet: AndroidSourceSet?, module: String, type: String) {
        if (sourceSet == null) return
        sourceSet.java.srcDir("$module/src/$type/java")
        sourceSet.res.srcDir("$module/src/$type/res")
        sourceSet.assets.srcDir("$module/src/$type/assets")
        sourceSet.jni.srcDir("$module/src/$type/jni")
        sourceSet.jniLibs.srcDir("$module/src/$type/jniLibs")
        sourceSet.aidl.srcDir("$module/src/$type/aidl")
    }

}

class Plugin : org.gradle.api.Plugin<Project> {

    override fun apply(project: Project) {

        project.extensions.create(
                IMultiModulePluginExtension::class.java, "pins",
                MultiModulePluginExtension::class.java, project
        )


    }
}