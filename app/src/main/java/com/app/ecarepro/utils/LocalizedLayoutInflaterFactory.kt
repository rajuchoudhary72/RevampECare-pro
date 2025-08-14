package com.app.ecarepro.utils


import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.LayoutInflaterCompat
import com.app.ecarepro.ui.language.LocalizationManager
import java.lang.reflect.Field

/**
 * Custom LayoutInflater.Factory2 that intercepts view creation to apply translations to TextView attributes
 */
class LocalizedLayoutInflaterFactory(private val delegate: AppCompatDelegate) : LayoutInflater.Factory2 {

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        // Let AppCompatDelegate handle the view creation first
        val view = delegate.createView(parent, name, context, attrs)

        // Process text attributes for TextView and subclasses
        if (view is AppCompatTextView) {
            processTextView(view, attrs, context)
        }

        return view
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return onCreateView(null, name, context, attrs)
    }

    private fun processTextView(textView: AppCompatTextView, attrs: AttributeSet, context: Context) {
        // Look for the android:text attribute
        for (i in 0 until attrs.attributeCount) {
            if (attrs.getAttributeName(i) == "text") {
                val value = attrs.getAttributeValue(i)
                if (value.startsWith("@")) {
                    try {
                        // Extract resource ID from the attribute value
                        val resourceId = value.substring(1).toInt()

                        // Get resource name from ID
                        val resourceName = context.resources.getResourceEntryName(resourceId)

                        // Try to get a translation for this resource name
                        val translatedString = LocalizationManager.getString(resourceName)

                        // Apply translation if available
                        if (translatedString != resourceName) {
                            textView.text = translatedString
                        }
                    } catch (e: Exception) {
                        // Handle any parsing errors silently
                    }
                }
            }
        }
    }
}

/**
 * Factory method to install our custom layout inflater factory
 */
fun installLocalizedLayoutFactory(activity: BaseActivity) {
    val layoutInflater = LayoutInflater.from(activity)

    // Check if the factory is already set
    try {
        val field: Field = LayoutInflater::class.java.getDeclaredField("mFactory2")
        field.isAccessible = true
        val existingFactory = field.get(layoutInflater) as? LayoutInflater.Factory2

        // Only set if not already set to our factory
        if (existingFactory !is LocalizedLayoutInflaterFactory) {
            LayoutInflaterCompat.setFactory2(
                layoutInflater,
                LocalizedLayoutInflaterFactory(activity.delegate)
            )
        }
    } catch (e: Exception) {
        // Fallback if reflection fails
        try {
            LayoutInflaterCompat.setFactory2(
                layoutInflater,
                LocalizedLayoutInflaterFactory(activity.delegate)
            )
        } catch (e: Exception) {
            // Factory already set, can't override
        }
    }
}