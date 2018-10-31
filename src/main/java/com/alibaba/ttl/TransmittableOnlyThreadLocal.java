package com.alibaba.ttl;

import java.util.Map;
import java.util.logging.Logger;

/**
 * {@link TransmittableOnlyThreadLocal} can transmit value from the thread of submitting task to the thread of executing task.
 * <p>
 * Note: {@link TransmittableOnlyThreadLocal} extends {@link InheritableThreadLocal},
 * so {@link TransmittableOnlyThreadLocal} first is a {@link InheritableThreadLocal}.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see TtlRunnable
 * @see TtlCallable
 * @since 0.10.0
 */
public class TransmittableOnlyThreadLocal<T> extends InheritableThreadLocal<T> {
    private static final Logger logger = Logger.getLogger(TransmittableOnlyThreadLocal.class.getName());

    /**
     * Computes the value for this transmittable thread-local variable
     * as a function of the source thread's value at the time the task
     * Object is created.  This method is called from {@link TtlRunnable} or
     * {@link TtlCallable} when it create, before the task is started.
     * <p>
     * This method merely returns reference of its source thread value, and should be overridden
     * if a different behavior is desired.
     *
     * @since 1.0.0
     */
    protected T copy(T parentValue) {
        return parentValue;
    }

    /**
     * Callback method before task object({@link TtlRunnable}/{@link TtlCallable}) execute.
     * <p>
     * Default behavior is do nothing, and should be overridden
     * if a different behavior is desired.
     * <p>
     * Do not throw any exception, just ignored.
     *
     * @since 1.2.0
     */
    protected void beforeExecute() {
    }

    /**
     * Callback method after task object({@link TtlRunnable}/{@link TtlCallable}) execute.
     * <p>
     * Default behavior is do nothing, and should be overridden
     * if a different behavior is desired.
     * <p>
     * Do not throw any exception, just ignored.
     *
     * @since 1.2.0
     */
    protected void afterExecute() {
    }

    /**
     * see {@link InheritableThreadLocal#get()}
     */
    @Override
    public final T get() {
        T value = super.get();
        if (null != value) addValue();
        return value;
    }

    /**
     * see {@link InheritableThreadLocal#set}
     */
    @Override
    public final void set(T value) {
        super.set(value);
        // may set null to remove value
        if (null == value) removeValue();
        else addValue();
    }

    /**
     * see {@link InheritableThreadLocal#remove()}
     */
    @Override
    public final void remove() {
        removeValue();
        super.remove();
    }

    void superRemove() {
        super.remove();
    }

    T copyValue() {
        return copy(get());
    }

    private void addValue() {
        final InheritableThreadLocal<Map<Object, ?>> holder = TransmittableThreadLocal.holder;
        if (!holder.get().containsKey(this)) {
            holder.get().put(this, null); // WeakHashMap supports null value.
        }
    }

    private void removeValue() {
        TransmittableThreadLocal.holder.get().remove(this);
    }
}
