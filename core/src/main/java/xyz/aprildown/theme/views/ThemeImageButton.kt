package xyz.aprildown.theme.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import xyz.aprildown.theme.R
import xyz.aprildown.theme.Theme.Companion.get
import xyz.aprildown.theme.internal.AttrWizard
import xyz.aprildown.theme.utils.changeBackgroundColor
import xyz.aprildown.theme.utils.changeImageTint
import xyz.aprildown.theme.utils.colorForAttrName

internal class ThemeImageButton(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatImageButton(context, attrs) {

    init {
        val wizard = AttrWizard(context, attrs)
        val backgroundColorValue = wizard.getRawValue(android.R.attr.background)
        val tintColorValue = wizard.getRawValue(R.attr.tint)

        get().colorForAttrName(backgroundColorValue)?.let {
            changeBackgroundColor(it)
        }

        get().colorForAttrName(tintColorValue)?.let {
            changeImageTint(it)
        }
    }
}