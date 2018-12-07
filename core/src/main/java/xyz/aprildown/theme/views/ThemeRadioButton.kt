package xyz.aprildown.theme.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton
import xyz.aprildown.theme.Theme.Companion.get
import xyz.aprildown.theme.internal.AttrWizard
import xyz.aprildown.theme.internal.ColorIsDarkState
import xyz.aprildown.theme.utils.colorForAttrName
import xyz.aprildown.theme.utils.setTint

internal class ThemeRadioButton(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatRadioButton(context, attrs) {

    init {
        val wizard = AttrWizard(context, attrs)
        val backgroundColorValue = wizard.getRawValue(android.R.attr.background)

        val theme = get(context)
        theme.colorForAttrName(backgroundColorValue, theme.colorAccent)?.let {
            invalidateColors(ColorIsDarkState(it, theme.isDark))
        }
    }

    private fun invalidateColors(state: ColorIsDarkState) = setTint(state.color, state.isDark)
}
