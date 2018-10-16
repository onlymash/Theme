package xyz.aprildown.theme.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import xyz.aprildown.theme.Theme.Companion.get
import xyz.aprildown.theme.internal.AttrWizard
import xyz.aprildown.theme.utils.changeTextColor
import xyz.aprildown.theme.utils.colorForAttrName

internal class ThemeTextView(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    private val wizard = AttrWizard(context, attrs)
    private val textColorValue = wizard.getRawValue(android.R.attr.textColor)

    init {
        get().colorForAttrName(textColorValue)?.let {
            changeTextColor(it)
        }
    }
}