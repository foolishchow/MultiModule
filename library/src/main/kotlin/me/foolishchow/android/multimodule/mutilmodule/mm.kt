package me.foolishchow.android.multimodule.mutilmodule

import me.foolishchow.android.plugin.multimodule.OnModuleIncludeListener
import org.gradle.api.Project
import java.util.HashSet

/**
 * Description:
 * Author: foolishchow
 * Date: 2021/05/21 4:19 PM
 */
interface IMultiModulePluginExtension {
    fun include(projectName: String)
    fun getProjects(): Set<String>
    fun addModuleIncludeListener(listener: OnModuleIncludeListener)
}

class MultiModulePluginExtension(var project: Project) : IMultiModulePluginExtension {
    var projectSet: MutableSet<String> = HashSet()
    var mListener: OnModuleIncludeListener? = null
    override fun include(projectName: String) {
        projectSet.add(projectName)
        if (mListener != null) {
            mListener!!.addIncludeModule(projectName)
        }
    }

    override fun getProjects(): Set<String> {
        return projectSet
    }

    override fun addModuleIncludeListener(listener: OnModuleIncludeListener) {
        mListener = listener
    }
}