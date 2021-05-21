package me.foolishchow.android.multimodule.dependence

import org.gradle.api.Project

/**
 * Description:
 * Author: foolishchow
 * Date: 2021/05/21 4:16 PM
 */

interface IDependenceExtension{
    /**
     * junit junit:junit 配置
     * @param version
     */
    fun junit( version:String);

    /**
     * androidx.test:runner 版本
     * @param version
     */
    fun androidxTestRunner( version:String)
}
class DefaultDependenceExtension(var project: Project) : IDependenceExtension {
    override fun junit(version: String) {}
    override fun androidxTestRunner(version: String) {}
}

interface IDependenceVersionManager{

}