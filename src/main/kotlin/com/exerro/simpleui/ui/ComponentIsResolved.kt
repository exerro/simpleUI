package com.exerro.simpleui.ui

/** A type which exists purely to minimise mistakes where the proper control
 *  flow is not taken. A component must, in its definition/init function, call
 *  either [setResolver()][ComponentContext.setResolver] or
 *  [children()][ComponentContext.children] (including indirectly). This type is
 *  returned by both, ensuring they have been called. */
class ComponentIsResolved private constructor() {
    companion object {
        internal val INSTANCE = ComponentIsResolved()
    }
}
