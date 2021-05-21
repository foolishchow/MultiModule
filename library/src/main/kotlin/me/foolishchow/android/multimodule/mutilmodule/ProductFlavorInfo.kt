package me.foolishchow.android.multimodule.mutilmodule

import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import java.util.*

/**
 * Description:
 * Author: foolishchow
 * Date: 2021/05/21 4:22 PM
 */
internal class ProductFlavorInfo(project: Project) {
    var flavorDimensions: List<String>?
    var productFlavors: MutableList<String>
    var buildTypes: MutableList<String>
    var combinedProductFlavors: MutableList<String> = mutableListOf()
    var combinedProductFlavorsMap: MutableMap<String, List<String>> = mutableMapOf()
    var singleDimension = false
    private var flavorGroups: MutableList<MutableList<String>> = mutableListOf()
    private fun calculateFlavorCombination() {
        combinedProductFlavors = ArrayList()
        combinedProductFlavorsMap = HashMap()
        if (flavorGroups.size == 0) {
            return
        }
        val combination: MutableList<Int> = ArrayList()
        val n = flavorGroups.size
        for (i in 0 until n) {
            combination.add(0)
        }
        var i = 0
        var isContinue = true
        while (isContinue) {
            val items: MutableList<String> = ArrayList()
            var item = flavorGroups[0][combination[0]]
            items.add(item)
            var combined = item
            for (j in 1 until n) {
                item = flavorGroups[j][combination[j]]
                combined += item.capitalize()//Utils.upperCase(item)
                items.add(item)
            }
            combinedProductFlavors.add(combined)
            combinedProductFlavorsMap[combined] = items
            i++
            combination[n - 1] = i
            for (j in n - 1 downTo 0) {
                if (combination[j] >= flavorGroups[j].size) {
                    combination[j] = 0
                    i = 0
                    if (j - 1 >= 0) {
                        combination[j - 1] = combination[j - 1] + 1
                    }
                }
            }
            isContinue = false
            for (integer in combination) {
                if (integer != 0) {
                    isContinue = true
                }
            }
        }
    }

    init {
        val extension: BaseExtension = project.extensions.getByName("android") as BaseExtension
        buildTypes = ArrayList()
        if (extension.buildTypes != null) {
            extension.buildTypes.forEach{
                buildTypes.add(it.name)
            }
        }
        flavorDimensions = extension.flavorDimensionList
        if (flavorDimensions == null) {
            flavorDimensions = ArrayList()
        }
        productFlavors = ArrayList()
        flavorGroups = ArrayList()
        for (i in flavorDimensions!!.indices) {
            flavorGroups.add(ArrayList())
        }
        extension.productFlavors.forEach{
            productFlavors.add(it.name)
            val position = flavorDimensions!!.indexOf(it.dimension)
            flavorGroups[position].add(it.name)
        }
        val flavorGroupTemp: MutableList<MutableList<String>> = ArrayList()
        flavorGroups.forEach{
            if (it.size !== 0) {
                flavorGroupTemp.add(it)
            }
        }
        flavorGroups = flavorGroupTemp
        calculateFlavorCombination()
        if (combinedProductFlavors!!.size == extension.productFlavors.size) {
            singleDimension = true
        }
    }
}