package xyz.aprildown.theme.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.PorterDuff.Mode.SRC_ATOP
import android.graphics.PorterDuff.Mode.SRC_IN
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.*
import android.view.View
import android.widget.*
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.TintableBackgroundView
import androidx.core.view.ViewCompat
import xyz.aprildown.theme.R

@SuppressLint("PrivateResource")
internal fun View.setTintAuto(
    @ColorInt color: Int,
    requestBackground: Boolean,
    isDark: Boolean
) {
    var background = requestBackground
    if (!background) {
        when (this) {
            is RadioButton -> setTint(color, isDark)
            is SeekBar -> setTint(color, isDark)
            is ProgressBar -> setTint(color)
            is EditText -> setTint(color, isDark)
            is CheckBox -> setTint(color, isDark)
            is ImageView -> setTint(color)
            is Switch -> setTint(color, isDark)
            is SwitchCompat -> setTint(color, isDark)
            is AppCompatCheckedTextView -> setTint(color, isDark)
            else -> background = true
        }

        if (SDK_INT >= LOLLIPOP && !background && this.background is RippleDrawable) {
            // Ripples for the above views (e.g. when you tap and hold a switch or checkbox)
            val rd = this.background as RippleDrawable
            val unchecked = context.color(
                if (isDark) R.color.ripple_material_dark
                else R.color.ripple_material_light
            )
            val checked = ColorUtils.adjustAlpha(color, 0.4f)
            val sl = ColorStateList(
                arrayOf(
                    intArrayOf(
                        -android.R.attr.state_activated,
                        -android.R.attr.state_checked
                    ),
                    intArrayOf(android.R.attr.state_activated),
                    intArrayOf(android.R.attr.state_checked)
                ),
                intArrayOf(unchecked, checked, checked)
            )
            rd.setColor(sl)
        }
    }
    if (background) {
        // Need to tint the background of a view
        if (this is Button) {
            setTintSelector(color, false, isDark)
        } else if (this.background != null) {
            val drawable: Drawable? = this.background
            if (drawable != null) {
                ViewCompat.setBackground(this, drawable.tint(color))
            }
        }
    }
}

internal fun View.setTintSelector(
    @ColorInt color: Int,
    darker: Boolean,
    useDarkTheme: Boolean
) {
    val isColorLight = ColorUtils.isLightColor(color)
    val disabled = context.color(
        if (useDarkTheme) R.color.theme_button_disabled_dark
        else R.color.theme_button_disabled_light
    )
    val pressed = ColorUtils.shiftColor(color, if (darker) 0.9f else 1.1f)
    val activated = ColorUtils.shiftColor(color, if (darker) 1.1f else 0.9f)
    val rippleColor = defaultRippleColor(context, isColorLight)
    val textColor = context.color(
        if (isColorLight) R.color.theme_primary_text_light
        else R.color.theme_primary_text_dark
    )

    val sl: ColorStateList
    when (this) {
        is Button -> {
            sl = disabledColorStateList(color, disabled)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && this.background is RippleDrawable) {
                val rd = this.background as RippleDrawable
                rd.setColor(ColorStateList.valueOf(rippleColor))
            }

            // Disabled text color state for buttons, may get overridden later by ATE tags
            setTextColor(
                disabledColorStateList(
                    textColor,
                    context.color(
                        if (useDarkTheme) R.color.theme_button_text_disabled_dark
                        else R.color.theme_button_text_disabled_light
                    )
                )
            )
        }
        else -> sl = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_enabled), intArrayOf(android.R.attr.state_enabled),
                intArrayOf(android.R.attr.state_enabled, android.R.attr.state_pressed),
                intArrayOf(android.R.attr.state_enabled, android.R.attr.state_activated),
                intArrayOf(android.R.attr.state_enabled, android.R.attr.state_checked)
            ),
            intArrayOf(disabled, color, pressed, activated, activated)
        )
    }

    val drawable: Drawable? = this.background
    if (drawable != null) {
        ViewCompat.setBackground(this, drawable.tint(sl))
    }

    if (this is TextView && this !is Button) {
        setTextColor(
            disabledColorStateList(
                textColor,
                context.color(
                    if (isColorLight) R.color.theme_text_disabled_light
                    else R.color.theme_text_disabled_dark
                )
            )
        )
    }
}

internal fun RadioButton.setTint(
    @ColorInt tintColor: Int,
    useDarker: Boolean
) {
    val disabledColor = context.color(
        if (useDarker) R.color.theme_control_disabled_dark
        else R.color.theme_control_disabled_light
    )
    val defaultColor = context.color(
        if (useDarker) R.color.theme_control_normal_dark
        else R.color.theme_control_normal_light
    )
    val sl = ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_enabled),
            intArrayOf(android.R.attr.state_enabled, -android.R.attr.state_checked),
            intArrayOf(android.R.attr.state_enabled, android.R.attr.state_checked)
        ),
        intArrayOf(
            // Radio button includes own alpha for disabled state
            ColorUtils.stripAlpha(disabledColor),
            defaultColor,
            tintColor
        )
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        this.buttonTintList = sl
    } else {
        @SuppressLint("PrivateResource")
        this.buttonDrawable = context.drawable(R.drawable.abc_btn_radio_material)
            .tint(sl)
    }
}

internal fun AbsSeekBar.setTint(
    @ColorInt color: Int,
    useDarker: Boolean
) {
    val s1 = disabledColorStateList(
        color,
        context.color(
            if (useDarker) R.color.theme_control_disabled_dark
            else R.color.theme_control_disabled_light
        )
    )
    if (SDK_INT >= LOLLIPOP) {
        this.thumbTintList = s1
        this.progressTintList = s1
        this.secondaryProgressTintList = s1
    } else {
        this.progressDrawable = this.progressDrawable.tint(s1)
        if (SDK_INT >= JELLY_BEAN) {
            this.thumb = this.thumb.tint(s1)
        }
    }
}

internal fun ProgressBar.setTint(@ColorInt color: Int) = setTint(color, false)

internal fun CheckBox.setTint(
    @ColorInt tintColor: Int,
    useDarker: Boolean
) {
    val disabledColor = context.color(
        if (useDarker) R.color.theme_control_disabled_dark
        else R.color.theme_control_disabled_light
    )
    val defaultColor = context.color(
        if (useDarker) R.color.theme_control_normal_dark
        else R.color.theme_control_normal_light
    )
    val sl = ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_enabled),
            intArrayOf(android.R.attr.state_enabled, -android.R.attr.state_checked),
            intArrayOf(android.R.attr.state_enabled, android.R.attr.state_checked)
        ),
        intArrayOf(
            disabledColor,
            defaultColor,
            tintColor
        )
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        this.buttonTintList = sl
    } else {
        @SuppressLint("PrivateResource")
        this.buttonDrawable = context.drawable(R.drawable.abc_btn_check_material)
            .tint(sl)
    }
}

internal fun Switch.setTint(
    @ColorInt tintColor: Int,
    useDarker: Boolean
) {
    if (trackDrawable != null) {
        trackDrawable = trackDrawable.modifySwitchDrawable(
            context = context,
            requestedTint = tintColor,
            thumb = false,
            compatSwitch = false,
            useDarker = useDarker
        )
    }
    if (thumbDrawable != null) {
        thumbDrawable = thumbDrawable.modifySwitchDrawable(
            context = context,
            requestedTint = tintColor,
            thumb = true,
            compatSwitch = false,
            useDarker = useDarker
        )
    }
}

internal fun SwitchCompat.setTint(
    @ColorInt color: Int,
    useDarker: Boolean
) {
    if (trackDrawable != null) {
        trackDrawable = trackDrawable.modifySwitchDrawable(
            context = context,
            requestedTint = color,
            thumb = false,
            compatSwitch = true,
            useDarker = useDarker
        )
    }
    if (thumbDrawable != null) {
        thumbDrawable = thumbDrawable.modifySwitchDrawable(
            context = context,
            requestedTint = color,
            thumb = true,
            compatSwitch = true,
            useDarker = useDarker
        )
    }
}

internal fun AppCompatCheckedTextView.setTint(
    @ColorInt tintColor: Int,
    useDarker: Boolean
) {
    val sl = checkableColorStateList(
        context = context,
        requestedTint = tintColor,
        thumb = false,
        compatSwitch = true,
        useDarker = useDarker
    )
    if (SDK_INT >= M) {
        compoundDrawableTintList = sl
    } else {
        for (compoundDrawable in compoundDrawables) {
            compoundDrawable?.setColorFilter(tintColor, SRC_IN)
        }
    }
}

internal fun EditText.setTint(
    @ColorInt tintColor: Int,
    useDarker: Boolean
) {
    val disabledColor = context.color(
        if (useDarker) R.color.theme_text_disabled_dark
        else R.color.theme_text_disabled_light
    )
    val defaultColor = context.color(
        if (useDarker) R.color.theme_control_normal_dark
        else R.color.theme_control_normal_light
    )
    val editTextColorStateList = ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_enabled),
            intArrayOf(
                android.R.attr.state_enabled,
                -android.R.attr.state_pressed,
                -android.R.attr.state_focused
            ),
            intArrayOf()
        ),
        intArrayOf(
            disabledColor,
            defaultColor,
            tintColor
        )
    )
    if (this is TintableBackgroundView) {
        ViewCompat.setBackgroundTintList(this, editTextColorStateList)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        this.backgroundTintList = editTextColorStateList
    }
    setCursorTint(tintColor)
}

internal fun EditText.setCursorTint(@ColorInt color: Int) {
    try {
        Reflection.getField(this, "mCursorDrawableRes")
            ?.let { fCursorDrawableRes ->
                Reflection.getField(this, "mEditor")?.let { fEditor ->
                    fEditor.get(this)?.let { editor ->
                        Reflection.getField(editor, "mCursorDrawable")?.let { fCursorDrawable ->
                            val cursorDrawableRes = fCursorDrawableRes.getInt(this)
                            context.drawable(cursorDrawableRes)?.let { drawable ->
                                drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                                val drawables = arrayOf(drawable, drawable)
                                fCursorDrawable.set(editor, drawables)
                            }
                        }
                    }
                }
            }
    } catch (e: Exception) {
    }
}

internal fun ProgressBar.setTint(
    @ColorInt color: Int,
    skipIndeterminate: Boolean
) {
    val sl = ColorStateList.valueOf(color)
    if (SDK_INT >= LOLLIPOP) {
        this.progressTintList = sl
        this.secondaryProgressTintList = sl
        if (!skipIndeterminate) {
            this.indeterminateTintList = sl
        }
        return
    }

    if (!skipIndeterminate && this.indeterminateDrawable != null) {
        this.indeterminateDrawable.setColorFilter(color, SRC_IN)
    }
    if (this.progressDrawable != null) {
        this.progressDrawable.setColorFilter(color, SRC_IN)
    }
}

internal fun ImageView.setTint(@ColorInt color: Int) = setColorFilter(color, SRC_ATOP)

@SuppressLint("PrivateResource")
@ColorInt
private fun defaultRippleColor(
    context: Context,
    useDarkRipple: Boolean
): Int {
    // Light ripple is actually translucent black, and vice versa
    return context.color(
        if (useDarkRipple) R.color.ripple_material_light
        else R.color.ripple_material_dark
    )
}

private fun disabledColorStateList(
    @ColorInt normal: Int,
    @ColorInt disabled: Int
): ColorStateList {
    return ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_enabled),
            intArrayOf(android.R.attr.state_enabled)
        ),
        intArrayOf(disabled, normal)
    )
}

private fun Drawable.modifySwitchDrawable(
    context: Context,
    @ColorInt requestedTint: Int,
    thumb: Boolean,
    compatSwitch: Boolean,
    useDarker: Boolean
): Drawable? {
    val sl = checkableColorStateList(
        context = context,
        requestedTint = requestedTint,
        thumb = thumb,
        compatSwitch = compatSwitch,
        useDarker = useDarker
    )
    return this.tint(sl)
}

private fun checkableColorStateList(
    context: Context,
    @ColorInt requestedTint: Int,
    thumb: Boolean,
    compatSwitch: Boolean,
    useDarker: Boolean
): ColorStateList {
    var tint = requestedTint
    if (useDarker) {
        tint = ColorUtils.shiftColor(tint, 1.1f)
    }
    tint = ColorUtils.adjustAlpha(tint, if (compatSwitch && !thumb) 0.5f else 1.0f)

    val disabled: Int
    var normal: Int
    if (thumb) {
        disabled = context.color(
            if (useDarker) R.color.theme_switch_thumb_disabled_dark
            else R.color.theme_switch_thumb_disabled_light
        )
        normal = context.color(
            if (useDarker) R.color.theme_switch_thumb_normal_dark
            else R.color.theme_switch_thumb_normal_light
        )
    } else {
        disabled = context.color(
            if (useDarker) R.color.theme_switch_track_disabled_dark
            else R.color.theme_switch_track_disabled_light
        )
        normal = context.color(
            if (useDarker) R.color.theme_switch_track_normal_dark
            else R.color.theme_switch_track_normal_light
        )
    }

    // Stock switch includes its own alpha
    if (!compatSwitch) {
        normal = ColorUtils.stripAlpha(normal)
    }

    return ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_enabled),
            intArrayOf(
                android.R.attr.state_enabled,
                -android.R.attr.state_activated,
                -android.R.attr.state_checked
            ),
            intArrayOf(
                android.R.attr.state_enabled,
                android.R.attr.state_activated
            ),
            intArrayOf(
                android.R.attr.state_enabled,
                android.R.attr.state_checked
            )
        ),
        intArrayOf(disabled, normal, tint, tint)
    )
}
