package com.exerro.simpleui

/** Marks rendering related DSLs and prevents nested calls within those. Used
 *  to prevent accidental calls to a parent method. */
@DslMarker
annotation class DrawContextDSL
