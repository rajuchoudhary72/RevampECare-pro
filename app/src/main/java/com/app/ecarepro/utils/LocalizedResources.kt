package com.app.ecarepro.utils


import android.content.Context
import android.content.res.Resources
import com.app.ecarepro.ui.language.LocalizationManager

class LocalizedResources(private val context: Context, private val resources: Resources) : Resources(
    resources.assets,
    resources.displayMetrics,
    resources.configuration
) {
    override fun getString(id: Int): String {
        // Get the resource name from the ID
        val resourceName = try {
            context.resources.getResourceEntryName(id)
        } catch (e: Exception) {
            return super.getString(id)
        }

        // Try to get the translation from our manager
        val translation = LocalizationManager.getString(resourceName)

        // If we get back just the key, it means no translation was found, so use the default
        return if (translation == resourceName) {
            super.getString(id)
        } else {
            translation
        }
    }

    override fun getString(id: Int, vararg formatArgs: Any): String {
        val baseString = getString(id)
        return String.format(baseString, *formatArgs)
    }
}