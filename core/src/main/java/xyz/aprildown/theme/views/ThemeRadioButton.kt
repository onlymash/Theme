package xyz.aprildown.theme.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton
import xyz.aprildown.theme.ColorIsDarkState
import xyz.aprildown.theme.Theme.Companion.get
import xyz.aprildown.theme.internal.AttrWizard
import xyz.aprildown.theme.utils.colorForAttrName
import xyz.aprildown.theme.utils.setTint

internal class ThemeRadioButton(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatRadioButton(context, attrs) {

    init {
        val wizard = AttrWizard(context, attrs)
        val backgroundColorValue = wizard.getRawValue(android.R.attr.background)

        get().colorForAttrName(backgroundColorValue, get().colorAccent)?.let {
            invalidateColors(ColorIsDarkState(it, get().isDark))
        }
    }

    private fun invalidateColors(state: ColorIsDarkState) = setTint(state.color, state.isDark)
}
