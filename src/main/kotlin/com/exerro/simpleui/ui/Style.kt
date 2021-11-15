package com.exerro.simpleui.ui

import com.exerro.simpleui.Pixels
import com.exerro.simpleui.UndocumentedExperimental
import com.exerro.simpleui.UndocumentedInternal
import com.exerro.simpleui.colour.Colour
import com.exerro.simpleui.colour.Colours
import com.exerro.simpleui.colour.RGBA
import com.exerro.simpleui.px

@UndocumentedExperimental
interface Style {
    @UndocumentedExperimental
    val attributes: Attributes

    @UndocumentedExperimental
    fun <T> getOrNull(key: Key<T>): T?

    @UndocumentedExperimental
    sealed interface Key<T> {
        @UndocumentedExperimental
        abstract class WithDefault<T>(val defaultValue: T): Key<T>

        @UndocumentedExperimental
        abstract class FallsBackOn<T>(val fallsBackOn: Key<T>): Key<T>
    }

    @UndocumentedExperimental
    data class Attributes(
        val isDarkTheme: Boolean = false,
        val isLightTheme: Boolean = false,
        val isAccessibleTheme: Boolean = false,
    )

    @UndocumentedExperimental
    data class KeyValuePair<T> internal constructor(
        val key: Key<T>,
        val value: T,
    )

    ////////////////////////////////////////////////////////////////////////////

    @UndocumentedExperimental
    object BackgroundColour: Key.WithDefault<Colour>(
        RGBA(0xe8, 0xe9, 0xe9)
    )

    @UndocumentedExperimental
    object AlternateBackgroundColour: Key.FallsBackOn<Colour>(
        BackgroundColour
    )

    @UndocumentedExperimental
    object ElementBackgroundColour: Key.FallsBackOn<Colour>(
        BackgroundColour
    )

    @UndocumentedExperimental
    object PrimaryBackgroundColour: Key.WithDefault<Colour>(
        Colours.teal
    )

    @UndocumentedExperimental
    object ErrorBackgroundColour: Key.WithDefault<Colour>(
        Colours.red
    )

    @UndocumentedExperimental
    object DisabledBackgroundColour: Key.WithDefault<Colour>(
        RGBA(0x7b, 0x7c, 0x7c)
    )

    @UndocumentedExperimental
    object ForegroundColour: Key.WithDefault<Colour>(
        Colours.black
    )

    @UndocumentedExperimental
    object AlternateForegroundColour: Key.FallsBackOn<Colour>(
        ForegroundColour
    )

    @UndocumentedExperimental
    object ElementForegroundColour: Key.FallsBackOn<Colour>(
        PrimaryForegroundColour,
    )

    @UndocumentedExperimental
    object PrimaryForegroundColour: Key.WithDefault<Colour>(
        Colours.white
    )

    @UndocumentedExperimental
    object ErrorForegroundColour: Key.FallsBackOn<Colour>(
        PrimaryForegroundColour,
    )

    @UndocumentedExperimental
    object DisabledForegroundColour: Key.FallsBackOn<Colour>(
        PrimaryForegroundColour,
    )

    @UndocumentedExperimental
    object HeaderForegroundColour: Key.FallsBackOn<Colour>(
        ForegroundColour
    )

    @UndocumentedExperimental
    object ShadowColour: Key.WithDefault<Colour>(
        Colours.lightGrey.withAlpha(0.8f)
    )

    @UndocumentedExperimental
    object AlternateShadowColour: Key.WithDefault<Colour>(
        Colours.lightGrey.withAlpha(0.8f)
    )

    @UndocumentedExperimental
    object ShadowRadius: Key.WithDefault<Float>(
        10f,
    )

    @UndocumentedExperimental
    object ShadowOffset: Key.WithDefault<Float>(
        2f,
    )

    @UndocumentedExperimental
    object SeparatorColour: Key.WithDefault<Colour>(
        RGBA(0x9b, 0x9c, 0x9c)
    )

    @UndocumentedExperimental
    object SeparatorThickness: Key.WithDefault<Int>(
        1
    )

    @UndocumentedExperimental
    object FocusUnderlineThickness: Key.WithDefault<Int>(
        2,
    )

    @UndocumentedExperimental
    object ButtonCornerRadius: Key.WithDefault<Pixels>(
        6.px
    )

    companion object {
        @UndocumentedExperimental
        operator fun invoke(vararg values: KeyValuePair<*>, attributes: Attributes = Attributes()) =
            fromMap(values.associate { it.key to it.value }, attributes = attributes)

        @UndocumentedExperimental
        fun fromMap(colourMap: Map<Key<*>, Any?>, attributes: Attributes = Attributes()) = object: Style {
            override val attributes = attributes
            override fun <T> getOrNull(key: Key<T>) = colourMap[key] as T?
        }

        @UndocumentedExperimental
        fun combine(base: Style, vararg extras: Style, attributes: Attributes = getAttributes(base, *extras)) = object: Style {
            override val attributes = attributes
            override fun <T> getOrNull(key: Key<T>): T? {
                base.getOrNull(key)?.let { return it }

                for (theme in extras) {
                    theme.getOrNull(key)?.let { return it }
                }

                return null
            }
        }

        @UndocumentedInternal
        private fun getAttributes(vararg styles: Style) = Attributes(
            isDarkTheme = styles.any { it.attributes.isDarkTheme },
            isLightTheme = styles.any { it.attributes.isLightTheme },
            isAccessibleTheme = styles.all { it.attributes.isAccessibleTheme },
        )

        @UndocumentedExperimental
        val Light = Style(
            BackgroundColour to RGBA(0xe8, 0xe9, 0xe9),
            AlternateBackgroundColour to RGBA(0xe0, 0xe1, 0xe1),
            ElementBackgroundColour to RGBA(0xf1, 0xf2, 0xf2),
            PrimaryBackgroundColour to Colours.teal,
            ErrorBackgroundColour to Colours.red,
            DisabledBackgroundColour to RGBA(0x7b, 0x7c, 0x7c),
            ForegroundColour to Colours.black,
            AlternateForegroundColour to Colours.black,
            ElementForegroundColour to Colours.white,
            PrimaryForegroundColour to Colours.white,
            ErrorForegroundColour to Colours.white,
            DisabledForegroundColour to Colours.white,
            HeaderForegroundColour to RGBA(0x3a, 0x3b, 0x3b),
            ShadowColour to Colours.lightGrey.withAlpha(0.8f),
            AlternateShadowColour to Colours.lightGrey.withAlpha(0.8f),
            SeparatorColour to RGBA(0x9b, 0x9c, 0x9c),
            attributes = Attributes(isLightTheme = true)
        )

        @UndocumentedExperimental
        val Dark = Style(
            BackgroundColour to RGBA(0x18, 0x19, 0x19),
            AlternateBackgroundColour to RGBA(0x13, 0x14, 0x14),
            ElementBackgroundColour to RGBA(0x1e, 0x1f, 0x1f),
            PrimaryBackgroundColour to Colours.teal,
            ErrorBackgroundColour to Colours.red,
            DisabledBackgroundColour to RGBA(0x4a, 0x4b, 0x4b),
            ForegroundColour to Colours.white,
            AlternateForegroundColour to Colours.white,
            ElementForegroundColour to Colours.white,
            PrimaryForegroundColour to Colours.white,
            ErrorForegroundColour to Colours.white,
            DisabledForegroundColour to Colours.white,
            HeaderForegroundColour to RGBA(0xa8, 0xa9, 0xa9),
            ShadowColour to Colours.pureBlack,
            AlternateShadowColour to Colours.pureBlack,
            SeparatorColour to RGBA(0x3a, 0x3b, 0x3b),
            attributes = Attributes(isDarkTheme = true)
        )

        @UndocumentedExperimental
        val Accessible = Style(
            BackgroundColour to Colours.pureWhite,
            AlternateBackgroundColour to Colours.lighterGrey,
            ElementBackgroundColour to Colours.white,
            PrimaryBackgroundColour to Colours.teal,
            ErrorBackgroundColour to Colours.red,
            DisabledBackgroundColour to Colours.grey,
            ForegroundColour to Colours.pureBlack,
            AlternateForegroundColour to Colours.pureBlack,
            ElementForegroundColour to Colours.pureWhite,
            PrimaryForegroundColour to Colours.pureWhite,
            ErrorForegroundColour to Colours.pureWhite,
            DisabledForegroundColour to Colours.pureWhite,
            HeaderForegroundColour to Colours.pureBlack,
            ShadowColour to Colours.pureBlack,
            AlternateShadowColour to Colours.pureBlack,
            SeparatorColour to Colours.pureBlack,
            attributes = Attributes(isAccessibleTheme = true)
        )
    }
}

@UndocumentedExperimental
infix fun <T> Style.Key<T>.to(value: T) =
    Style.KeyValuePair(this, value)

@UndocumentedExperimental
tailrec operator fun <T> Style.get(key: Style.Key<T>): T {
    getOrNull(key) ?.let { return it }

    return when (key) {
        is Style.Key.FallsBackOn -> get(key.fallsBackOn)
        is Style.Key.WithDefault -> key.defaultValue
    }
}
