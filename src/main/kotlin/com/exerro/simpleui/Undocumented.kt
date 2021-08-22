package com.exerro.simpleui

import kotlin.annotation.AnnotationTarget.*

/** Annotates elements that do not yet have any documentation (but should). */
@Retention(AnnotationRetention.SOURCE)
@Target(CLASS, ANNOTATION_CLASS, TYPE_PARAMETER, PROPERTY, FIELD, LOCAL_VARIABLE, VALUE_PARAMETER, CONSTRUCTOR, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER, TYPE, EXPRESSION, FILE, TYPEALIAS)
annotation class Undocumented

/** Annotates elements that do not yet have any documentation, that are part of
 *  the internal API. */
annotation class UndocumentedInternal
