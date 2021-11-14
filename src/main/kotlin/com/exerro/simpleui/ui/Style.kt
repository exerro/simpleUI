package com.exerro.simpleui.ui

import com.exerro.simpleui.UndocumentedExperimental
import com.exerro.simpleui.UndocumentedInternal
import com.exerro.simpleui.colour.Colour
import com.exerro.simpleui.colour.Colours
import com.exerro.simpleui.colour.RGBA

// TODO: change to keys with a default or a fallback
// TODO: no more themed shit in the styles/keys

@UndocumentedExperimental
interface Style {
    @UndocumentedExperimental
    val attributes: Attributes

    @UndocumentedExperimental
    fun <T> getOrNull(key: Key<T>): T?

    @UndocumentedExperimental
    fun <T> getOrNullInherited(key: Key<T>): T? =
        getOrNull(key) ?: key.fallsBackOn?.let(::getOrNullInherited)

    @UndocumentedExperimental
    operator fun <T> get(key: Key<T>): T {
        getOrNullInherited(key)?.let { return it }

        return when {
            attributes.isAccessibleTheme -> key.defaultAccessible
            attributes.isLightTheme -> key.defaultLight
            else -> key.defaultDark
        }
    }

    @UndocumentedExperimental
    data class Attributes(
        val isDarkTheme: Boolean = false,
        val isLightTheme: Boolean = false,
        val isAccessibleTheme: Boolean = false,
    )

    @UndocumentedExperimental
    abstract class Key<T>(
        /** Default value to use with dark styles. */
        val defaultDark: T,
        /** Default value to use with light styles. */
        val defaultLight: T = defaultDark,
        /** Default value to use for high accessibility styles. */
        val defaultAccessible: T = defaultDark,
        /** Default external key to fall back on. */
        val fallsBackOn: Key<T>? = null,
    )

    @UndocumentedExperimental
    object BackgroundColour: Key<Colour>(
        defaultDark = RGBA(0x18, 0x19, 0x19),
        defaultLight = RGBA(0xe8, 0xe9, 0xe9),
        defaultAccessible = Colours.pureWhite
    )

    @UndocumentedExperimental
    object AlternateBackgroundColour: Key<Colour>(
        defaultDark = RGBA(0x13, 0x14, 0x14),
        defaultLight = RGBA(0xe0, 0xe1, 0xe1),
        defaultAccessible = Colours.lighterGrey,
    )

    @UndocumentedExperimental
    object ElementBackgroundColour: Key<Colour>(
        defaultDark = RGBA(0x1e, 0x1f, 0x1f),
        defaultLight = RGBA(0xf1, 0xf2, 0xf2),
        defaultAccessible = Colours.white,
    )

    @UndocumentedExperimental
    object PrimaryBackgroundColour: Key<Colour>(
        defaultDark = Colours.teal,
        defaultLight = Colours.teal,
        defaultAccessible = Colours.teal,
    )

    @UndocumentedExperimental
    object ErrorBackgroundColour: Key<Colour>(
        defaultDark = Colours.red,
        defaultLight = Colours.red,
        defaultAccessible = Colours.red,
    )

    @UndocumentedExperimental
    object DisabledBackgroundColour: Key<Colour>(
        defaultDark = RGBA(0x4a, 0x4b, 0x4b),
        defaultLight = RGBA(0x7b, 0x7c, 0x7c),
        defaultAccessible = Colours.grey,
    )

    @UndocumentedExperimental
    object ForegroundColour: Key<Colour>(
        defaultDark = Colours.white,
        defaultLight = Colours.black,
        defaultAccessible = Colours.pureBlack,
    )

    @UndocumentedExperimental
    object AlternateForegroundColour: Key<Colour>(
        defaultDark = Colours.lightGrey,
        defaultLight = Colours.grey,
        defaultAccessible = Colours.pureBlack,
    )

    @UndocumentedExperimental
    object ElementForegroundColour: Key<Colour>( // TODO
        defaultDark = Colours.white,
        defaultLight = Colours.white,
        defaultAccessible = Colours.pureWhite,
        fallsBackOn = PrimaryForegroundColour,
    )

    @UndocumentedExperimental
    object PrimaryForegroundColour: Key<Colour>(
        defaultDark = Colours.white,
        defaultLight = Colours.white,
        defaultAccessible = Colours.pureWhite,
    )

    @UndocumentedExperimental
    object ErrorForegroundColour: Key<Colour>( // TODO
        defaultDark = Colours.white,
        defaultLight = Colours.white,
        defaultAccessible = Colours.pureWhite,
        fallsBackOn = PrimaryForegroundColour,
    )

    @UndocumentedExperimental
    object DisabledForegroundColour: Key<Colour>( // TODO
        defaultDark = Colours.white,
        defaultLight = Colours.white,
        defaultAccessible = Colours.pureWhite,
        fallsBackOn = PrimaryForegroundColour,
    )

    @UndocumentedExperimental
    object HeaderForegroundColour: Key<Colour>(
        defaultDark = RGBA(0xa8, 0xa9, 0xa9),
        defaultLight = RGBA(0x3a, 0x3b, 0x3b),
        defaultAccessible = Colours.pureBlack,
    )

    @UndocumentedExperimental
    object ShadowColour: Key<Colour>(
        defaultDark = Colours.pureBlack,
        defaultLight = Colours.lightGrey.withAlpha(0.8f),
        defaultAccessible = Colours.pureBlack.withAlpha(1000f),
    )

    @UndocumentedExperimental
    object ShadowRadius: Key<Float>(
        10f,
        defaultAccessible = 8f,
    )

    @UndocumentedExperimental
    object ShadowOffset: Key<Float>(
        2f,
        defaultAccessible = 0f,
    )

    @UndocumentedExperimental
    object SeparatorColour: Key<Colour>(
        defaultDark = RGBA(0x3a, 0x3b, 0x3b),
        defaultLight = RGBA(0x9b, 0x9c, 0x9c),
        defaultAccessible = Colours.pureBlack,
    )

    @UndocumentedExperimental
    object SeparatorThickness: Key<Int>(
        defaultDark = 1,
        defaultLight = 1,
        defaultAccessible = 2,
    )

    @UndocumentedExperimental
    object FocusUnderlineThickness: Key<Int>(
        2,
        defaultAccessible = 4,
    )

    companion object {
        @UndocumentedExperimental
        val Dark = object: Style {
            override val attributes = Attributes(isDarkTheme = true)
            override fun <T> getOrNull(key: Key<T>): T? = null
        }

        @UndocumentedExperimental
        val Light = object: Style {
            override val attributes = Attributes(isLightTheme = true)
            override fun <T> getOrNull(key: Key<T>): T? = null
        }

        @UndocumentedExperimental
        val Accessible = object: Style {
            override val attributes = Attributes(isAccessibleTheme = true)
            override fun <T> getOrNull(key: Key<T>): T? = null
        }

        @UndocumentedExperimental
        fun fromMap(colourMap: Map<Key<*>, Any?>, attributes: Attributes = Attributes()) = object: Style {
            override val attributes = attributes
            override fun <T> getOrNull(key: Key<T>) = colourMap[key] as T?
        }

        @UndocumentedExperimental
        fun combine(base: Style, vararg extras: Style, attributes: Attributes = getAttributes(base, *extras)) = object:
            Style {
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
            isAccessibleTheme = styles.any { it.attributes.isAccessibleTheme },
        )
    }
}
