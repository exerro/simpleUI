package com.exerro.simpleui.ui

import com.exerro.simpleui.UndocumentedExperimentalUI

@UndocumentedExperimentalUI
sealed interface WhoDefinesMe

@UndocumentedExperimentalUI
object ParentDefinesMe: WhoDefinesMe

@UndocumentedExperimentalUI
object ChildDefinesMe: WhoDefinesMe

@UndocumentedExperimentalUI
@JvmInline
value class SizeForParent<T: WhoDefinesMe> @Deprecated("Use fixForParent* methods to construct this value safely") constructor(
    @Deprecated("Use fixFromChild* methods to access this value safely", level = DeprecationLevel.WARNING)
    val value: Float,
)

@UndocumentedExperimentalUI
@JvmInline
value class SizeForChild<T: WhoDefinesMe> @Deprecated("Use fixForChild* methods to construct this value safely") constructor(
    @Deprecated("Use fixFromParent* methods to access this value safely")
    val value: Float,
)

@UndocumentedExperimentalUI
@Suppress("DEPRECATION", "NOTHING_TO_INLINE")
inline fun <T: WhoDefinesMe> fixForParentAny(value: Float): SizeForParent<T> = SizeForParent(value)

@UndocumentedExperimentalUI
@Suppress("DEPRECATION", "NOTHING_TO_INLINE")
inline fun fixForParent(value: Float): SizeForParent<ChildDefinesMe> = SizeForParent(value)

@UndocumentedExperimentalUI
@Suppress("DEPRECATION", "NOTHING_TO_INLINE")
inline fun nothingForParent(): SizeForParent<ParentDefinesMe> = SizeForParent(0f)

@UndocumentedExperimentalUI
@Suppress("DEPRECATION", "NOTHING_TO_INLINE")
inline fun <T: WhoDefinesMe> fixForChildAny(value: Float): SizeForChild<T> = SizeForChild(value)

@UndocumentedExperimentalUI
@Suppress("DEPRECATION", "NOTHING_TO_INLINE")
inline fun fixForChild(value: Float): SizeForChild<ParentDefinesMe> = SizeForChild(value)

@UndocumentedExperimentalUI
@Suppress("DEPRECATION", "NOTHING_TO_INLINE")
inline fun nothingForChild(): SizeForChild<ChildDefinesMe> = SizeForChild(0f)

@UndocumentedExperimentalUI
@Suppress("DEPRECATION", "NOTHING_TO_INLINE")
inline fun fixFromParentAny(value: SizeForChild<*>): Float = value.value

@UndocumentedExperimentalUI
@Suppress("DEPRECATION", "NOTHING_TO_INLINE")
inline fun <reified T: WhoDefinesMe> fixFromParentAnyOptional(
    value: SizeForChild<T>
): Float? = if (ParentDefinesMe is T) value.value else null

@UndocumentedExperimentalUI
@JvmName("fixFromParentParentDefinesMe")
@Suppress("DEPRECATION", "NOTHING_TO_INLINE")
inline fun fixFromParent(value: SizeForChild<ParentDefinesMe>) = value.value

@UndocumentedExperimentalUI
@JvmName("fixFromParentChildDefinesMe")
@Suppress("NOTHING_TO_INLINE")
inline fun fixFromParent(value: SizeForChild<ChildDefinesMe>): Float? = null

@UndocumentedExperimentalUI
@Suppress("DEPRECATION", "NOTHING_TO_INLINE")
inline fun fixFromChildAny(value: SizeForParent<*>): Float = value.value

@UndocumentedExperimentalUI
@Suppress("DEPRECATION", "NOTHING_TO_INLINE")
inline fun <reified T: WhoDefinesMe> fixFromChildAnyOptional(
    value: SizeForParent<T>
): Float? = if (ChildDefinesMe is T) value.value else null

@UndocumentedExperimentalUI
@JvmName("fixFromChildParentDefinesMe")
@Suppress("NOTHING_TO_INLINE")
inline fun fixFromChild(value: SizeForParent<ParentDefinesMe>): Float? = null

@UndocumentedExperimentalUI
@JvmName("fixFromChildChildDefinesMe")
@Suppress("DEPRECATION", "NOTHING_TO_INLINE")
inline fun fixFromChild(value: SizeForParent<ChildDefinesMe>) = value.value

@UndocumentedExperimentalUI
@Suppress("DEPRECATION")
inline fun <reified T: WhoDefinesMe> invert(
    value: SizeForChild<T>,
    eval: () -> Float
): SizeForParent<T> = SizeForParent(if (ParentDefinesMe is T) 0f else eval())

@UndocumentedExperimentalUI
@Suppress("DEPRECATION")
inline fun <reified T: WhoDefinesMe> map(
    value: SizeForChild<T>,
    map: (Float) -> Float
): SizeForChild<T> = SizeForChild(if (ParentDefinesMe is T) map(value.value) else value.value)

@UndocumentedExperimentalUI
@Suppress("DEPRECATION")
inline fun <reified T: WhoDefinesMe> map(
    value: SizeForParent<T>,
    map: (Float) -> Float
): SizeForParent<T> = SizeForParent(if (ChildDefinesMe is T) map(value.value) else value.value)
