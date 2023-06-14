package com.app.ecarepro

import com.airbnb.epoxy.EpoxyDataBindingPattern

/**
 * @Details Used to specify a naming pattern for the databinding layouts that you want models generated for.
 * Use this instead of {@link EpoxyDataBindingLayouts} to avoid having to explicitly list every
 * databinding layout.
 */
@EpoxyDataBindingPattern(rClass = R::class, layoutPrefix = "item")
object EpoxyDataBindingPatterns
