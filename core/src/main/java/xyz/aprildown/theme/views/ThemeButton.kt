package xyz.aprildown.theme.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import xyz.aprildown.theme.ColorIsDarkState
import xyz.aprildown.theme.Theme.Companion.get
import xyz.aprildown.theme.internal.AttrWizard
import xyz.aprildown.theme.utils.ColorUtils
import xyz.aprildown.theme.utils.colorForAttrName
import xyz.aprildown.theme.utils.setTintAuto

internal class ThemeButton(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatButton(context, attrs) {

    init {
        val wizard = AttrWizard(context, attrs)
        val backgroundColorValue = wizard.getRawValue(android.R.attr.background)

        val theme = get(context)
        theme.colorForAttrName(backgroundColorValue, theme.colorAccent)?.let {
            invalidateColors(ColorIsDarkState(it, theme.isDark))
        }
    }

    private fun invalidateColors(state: ColorIsDarkState) {
        setTintAuto(state.color, true, state.isDark)
        val textColorSl = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_enabled),
                intArrayOf(-android.R.attr.state_enabled)
            ),
            intArrayOf(
                if (ColorUtils.isLightColor(state.color)) BLACK else WHITE,
                if (state.isDark) WHITE else BLACK
            )
        )
        setTextColor(textColorSl)

        // Hack around button color not updating
        isEnabled = !isEnabled
        isEnabled = !isEnabled
    }
}
