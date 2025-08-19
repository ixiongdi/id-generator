/*
 * MIT License
 *
 * Copyright (c) 2025 ixiongdi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 */

package uno.xifan.id.generator.objectid;


/**
 * <p>
 * Design by contract assertions.
 * </p>
 * <p>
 * This class is not part of the public API and may be removed or changed at any
 * time.
 * </p>
 */
public final class Assertions {
    /**
     * Throw IllegalArgumentException if the value is null.
     *
     * @param name  the parameter name
     * @param value the value that should not be null
     * @param <T>   the value type
     * @return the value
     * @throws IllegalArgumentException if value is null
     */
    public static <T> T notNull(final String name, final T value) {
        if (value == null) {
            throw new IllegalArgumentException(name + " can not be null");
        }
        return value;
    }

    /**
     * Throw IllegalStateException if the condition if false.
     *
     * @param name      the name of the state that is being checked
     * @param condition the condition about the parameter to check
     * @throws IllegalStateException if the condition is false
     */
    public static void isTrue(final String name, final boolean condition) {
        if (!condition) {
            throw new IllegalStateException("state should be: " + name);
        }
    }

    /**
     * Throw IllegalArgumentException if the condition if false.
     *
     * @param name      the name of the state that is being checked
     * @param condition the condition about the parameter to check
     * @throws IllegalArgumentException if the condition is false
     */
    public static void isTrueArgument(final String name, final boolean condition) {
        if (!condition) {
            throw new IllegalArgumentException("state should be: " + name);
        }
    }

    /**
     * Throw IllegalArgumentException if the condition if false, otherwise return
     * the value. This is useful when arguments must be checked
     * within an expression, as when using {@code this} to call another constructor,
     * which must be the first line of the calling
     * constructor.
     *
     * @param <T>       the value type
     * @param name      the name of the state that is being checked
     * @param value     the value of the argument
     * @param condition the condition about the parameter to check
     * @return the value
     * @throws java.lang.IllegalArgumentException if the condition is false
     */
    public static <T> T isTrueArgument(final String name, final T value, final boolean condition) {
        if (!condition) {
            throw new IllegalArgumentException("state should be: " + name);
        }
        return value;
    }

    /**
     * @return Never completes normally. The return type is {@link AssertionError}
     *         to allow writing {@code throw fail()}.
     *         This may be helpful in non-{@code void} methods.
     * @throws AssertionError Always
     */
    public static AssertionError fail() throws AssertionError {
        throw new AssertionError();
    }

    /**
     * @param msg The failure message.
     * @return Never completes normally. The return type is {@link AssertionError}
     *         to allow writing {@code throw fail("failure message")}.
     *         This may be helpful in non-{@code void} methods.
     * @throws AssertionError Always
     */
    public static AssertionError fail(final String msg) throws AssertionError {
        throw new AssertionError(assertNotNull(msg));
    }

    /**
     * @param value A value to check.
     * @param <T>   The type of {@code value}.
     * @return {@code value}
     * @throws AssertionError If {@code value} is {@code null}.
     */
    public static <T> T assertNotNull(final T value) throws AssertionError {
        if (value == null) {
            throw new AssertionError();
        }
        return value;
    }

    /**
     * Throw AssertionError if the condition if false.
     *
     * @param name      the name of the state that is being checked
     * @param condition the condition about the parameter to check
     * @throws AssertionError if the condition is false
     */
    public static void assertTrue(final String name, final boolean condition) {
        if (!condition) {
            throw new AssertionError("state should be: " + assertNotNull(name));
        }
    }

    /**
     * Cast an object to the given class and return it, or throw
     * IllegalArgumentException if it's not assignable to that class.
     *
     * @param clazz        the class to cast to
     * @param value        the value to cast
     * @param errorMessage the error message to include in the exception
     * @param <T>          the Class type
     * @return value cast to clazz
     * @throws IllegalArgumentException if value is not assignable to clazz
     */
    @SuppressWarnings("unchecked")
    public static <T> T convertToType(final Class<T> clazz, final Object value, final String errorMessage) {
        if (!clazz.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException(errorMessage);
        }
        return (T) value;
    }

    private Assertions() {
    }
}