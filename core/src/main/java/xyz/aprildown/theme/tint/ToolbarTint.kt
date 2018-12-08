package xyz.aprildown.theme.tint

import android.R.attr
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.Menu
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import xyz.aprildown.theme.utils.*
import java.lang.reflect.Field

fun Toolbar.setOverflowButtonColor(@ColorInt color: Int) {
    val overflowDrawable = overflowIcon
    if (overflowDrawable != null) {
        overflowIcon = overflowDrawable.tint(color)
    }
}

fun Toolbar.tintMenu(
    menu: Menu,
    activeColor: Int,
    inactiveColor: Int
) {
    val colors = ColorStateList(
        arrayOf(
            intArrayOf(attr.state_enabled), intArrayOf(-attr.state_enabled)
        ),
        intArrayOf(activeColor, inactiveColor)
    )

    // The collapse icon displays when action views are expanded (e.g. SearchView)
    try {
        Reflection.getField(this, "mCollapseIcon")?.let { field ->
            val collapseIcon = field.get(this) as? Drawable
            if (collapseIcon != null) {
                field.set(this, collapseIcon.tint(colors))
            }
        }
    } catch (e: Exception) {
    }

    // Theme menu action views
    for (i in 0 until menu.size()) {
        val menuItem = menu.getItem(i)
        val actionView = menuItem.actionView
        if (actionView is SearchView) {
            actionView.setColors(activeColor, inactiveColor)
        }
        if (menu.getItem(i).icon != null) {
            menuItem.icon = menuItem.icon.tint(colors)
        }
    }
}

private fun SearchView.setColors(
    activeColor: Int,
    inactiveColor: Int
) {
    val tintColors = ColorStateList(
        arrayOf(
            intArrayOf(attr.state_enabled), intArrayOf(-attr.state_enabled)
        ),
        intArrayOf(activeColor, inactiveColor)
    )

    try {
        Reflection.getField(this, "mSearchSrcTextView")?.let { mSearchSrcTextViewField ->
            val mSearchSrcTextView = mSearchSrcTextViewField.get(this) as EditText
            mSearchSrcTextView.setTextColor(activeColor)
            mSearchSrcTextView.setHintTextColor(inactiveColor)
            mSearchSrcTextView.setCursorTint(activeColor)
        }

        @Throws(Exception::class)
        fun tintImageView(
            target: Any,
            field: Field,
            colors: ColorStateList
        ) {
            field.isAccessible = true
            val imageView = field.get(target) as ImageView
            if (imageView.drawable != null) {
                imageView.setImageDrawable(imageView.drawable.tint(colors))
            }
        }

        Reflection.getField(this, "mSearchButton")?.let { field ->
            tintImageView(this, field, tintColors)
        }
        Reflection.getField(this, "mGoButton")?.let { field ->
            tintImageView(this, field, tintColors)
        }
        Reflection.getField(this, "mCloseButton")?.let { field ->
            tintImageView(this, field, tintColors)
        }
        Reflection.getField(this, "mVoiceButton")?.let { field ->
            tintImageView(this, field, tintColors)
        }

        Reflection.getField(this, "mSearchPlate")?.let { field ->
            (field.get(this) as View).apply {
                setTintAuto(
                    color = activeColor,
                    requestBackground = true,
                    isDark = ColorUtils.isDarkColor(activeColor)
                )
            }
        }

        Reflection.getField(this, "mSearchHintIcon")?.let { field ->
            (field.get(this) as Drawable).apply {
                field.set(this@setColors, this@apply.tint(tintColors))
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}