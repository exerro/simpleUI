package com.exerro.simpleui

import kotlin.annotation.AnnotationTarget.*

/** Annotates elements that do not yet have any documentation (but should). */
@Suppress("unused")
@Retention(AnnotationRetention.SOURCE)
@Target(CLASS, ANNOTATION_CLASS, TYPE_PARAMETER, PROPERTY, FIELD, LOCAL_VARIABLE, VALUE_PARAMETER, CONSTRUCTOR, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER, TYPE, EXPRESSION, FILE, TYPEALIAS)
annotation class Undocumented

/** Annotates elements that do not yet have any documentation, that are part of
 *  the internal API. */
@Suppress("unused")
@Retention(AnnotationRetention.SOURCE)
@Target(CLASS, ANNOTATION_CLASS, TYPE_PARAMETER, PROPERTY, FIELD, LOCAL_VARIABLE, VALUE_PARAMETER, CONSTRUCTOR, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER, TYPE, EXPRESSION, FILE, TYPEALIAS)
annotation class UndocumentedInternal

/** Annotates elements that do not yet have any documentation, that are in an
 *  experimental state. */
@Suppress("unused")
@Retention(AnnotationRetention.SOURCE)
@Target(CLASS, ANNOTATION_CLASS, TYPE_PARAMETER, PROPERTY, FIELD, LOCAL_VARIABLE, VALUE_PARAMETER, CONSTRUCTOR, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER, TYPE, EXPRESSION, FILE, TYPEALIAS)
annotation class UndocumentedExperimental

/** Annotates UI elements that do not yet have any documentation, that are in an
 *  experimental state. */
@Suppress("unused")
@Retention(AnnotationRetention.SOURCE)
@Target(CLASS, ANNOTATION_CLASS, TYPE_PARAMETER, PROPERTY, FIELD, LOCAL_VARIABLE, VALUE_PARAMETER, CONSTRUCTOR, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER, TYPE, EXPRESSION, FILE, TYPEALIAS)
annotation class UndocumentedExperimentalUI
