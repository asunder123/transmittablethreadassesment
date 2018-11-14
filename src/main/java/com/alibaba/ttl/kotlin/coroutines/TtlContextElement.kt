/*
 * Copyright 2016-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package com.alibaba.ttl.kotlin.coroutines

import kotlin.coroutines.CoroutineContext


public interface TtlContextElement<S> : CoroutineContext.Element {
    /**
     * Updates context of the current thread.
     * This function is invoked before the coroutine in the specified [context] is resumed in the current thread
     * when the context of the coroutine this element.
     * The result of this function is the old value of the thread-local state that will be passed to [restoreThreadContext].
     * This method should handle its own exceptions and do not rethrow it. Thrown exceptions will leave coroutine which
     * context is updated in an undefined state.
     *
     * @param context the coroutine context.
     */
    public fun updateThreadContext(context: CoroutineContext): S

    /**
     * Restores context of the current thread.
     * This function is invoked after the coroutine in the specified [context] is suspended in the current thread
     * if [updateThreadContext] was previously invoked on resume of this coroutine.
     * The value of [oldState] is the result of the previous invocation of [updateThreadContext] and it should
     * be restored in the thread-local state by this function.
     * This method should handle its own exceptions and do not rethrow it. Thrown exceptions will leave coroutine which
     * context is updated in an undefined state.
     *
     * @param context the coroutine context.
     * @param oldState the value returned by the previous invocation of [updateThreadContext].
     */
    public fun restoreThreadContext(context: CoroutineContext, oldState: S)
}

public fun <T> ThreadLocal<T>.asContextElement(value: T = get()): TtlContextElement<T> =
    ThreadLocalElement(value, this)
