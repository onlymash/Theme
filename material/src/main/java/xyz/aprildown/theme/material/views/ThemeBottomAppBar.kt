package xyz.aprildown.theme.material.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import com.google.android.material.bottomappbar.BottomAppBar
import xyz.aprildown.theme.Theme.Companion.get
import xyz.aprildown.theme.internal.AttrWizard
import xyz.aprildown.theme.material.utils.darkenColor
import xyz.aprildown.theme.material.utils.tint
import xyz.aprildown.theme.utils.*

class ThemeBottomAppBar(
    context: Context,
    attrs: AttributeSet? = null
) : BottomAppBar(context, attrs) {

    private var menuIconColor: Int? = null

    init {
        val wizard = AttrWizard(context, attrs)
        val backgroundColorValue = wizard.getRawValue(android.R.attr.background)

        get().colorForAttrName(backgroundColorValue, get().colorPrimary)?.let {
            backgroundTint = ColorStateList.valueOf(it)
        }

        invalidateColors(get().toolbarIconColor)

        setTitleTextColor(get().toolbarTitleColor)

        setSubtitleTextColor(get().toolbarSubtitleColor)
    }

    override fun setNavigationIcon(icon: Drawable?) {
        if (menuIconColor == null) {
            super.setNavigationIcon(icon)
            return
        }
        super.setNavigationIcon(icon.tint(menuIconColor!!))
    }

    fun setNavigationIcon(icon: Drawable?, @ColorInt color: Int) {
        if (menuIconColor == null) {
            super.setNavigationIcon(icon)
            return
        }
        super.setNavigationIcon(icon.tint(color))
    }

    private fun invalidateColors(color: Int) {
        this.menuIconColor = color
        setOverflowButtonColor(color)
        tintMenu(menu, color, color.darkenColor())
        if (navigationIcon != null) {
            this.navigationIcon = navigationIcon
        }
    }
}