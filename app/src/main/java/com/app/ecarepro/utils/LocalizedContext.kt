package com.app.ecarepro.utils


import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import android.view.LayoutInflater

/**
 * Custom Context that provides localized resources
 */
class LocalizedContext(base: Context) : ContextWrapper(base) {
    private var localizedResources: Resources? = null

    override fun getResources(): Resources {
        if (localizedResources == null) {
            localizedResources = LocalizedResources(this, super.getResources())
        }
        return localizedResources!!
    }

    override fun createConfigurationContext(overrideConfiguration: Configuration): Context {
        val context = super.createConfigurationContext(overrideConfiguration)
        return LocalizedContext(context)
    }

    override fun getSystemService(name: String): Any? {
        if (LAYOUT_INFLATER_SERVICE == name) {
            // Return an inflater that uses this context
            val inflater = super.getSystemService(name) as LayoutInflater
            return inflater.cloneInContext(this)
        }
        return super.getSystemService(name)
    }
}