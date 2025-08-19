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
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package uno.xifan.id.base;

/**
 * ID生成器接口
 * <p>
 * 该接口定义了生成唯一标识符的基本行为。所有具体的ID生成器实现类都应该实现此接口。
 * 接口提供了单个ID生成和批量ID生成的能力，同时要求实现类指定其生成的ID类型。
 * </p>
 * 
 * @author ixiongdi
 * @since 1.0
 */
public interface IdGenerator {
    /**
     * 生成一个唯一标识符。
     * 每个实现类都应该根据其特定的生成策略来实现此方法。
     *
     * @return 生成的唯一标识符，具体类型由实现类决定
     */
    Object generate();

    /**
     * 批量生成指定数量的唯一标识符
     * <p>
     * 此方法提供了一个默认实现，通过多次调用{@link #generate()}方法来生成多个ID。
     * 实现类可以根据需要重写此方法以提供更高效的批量生成策略。
     * </p>
     *
     * @param count 要生成的ID数量，必须大于0
     * @return 包含生成的唯一标识符的数组
     * @throws IllegalArgumentException 当count参数小于或等于0时抛出
     */
    default Object[] generate(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("count must be positive");
        }
        Object[] ids = new Object[count];
        for (int i = 0; i < count; i++) {
            ids[i] = generate();
        }
        return ids;
    }

    /**
     * 获取当前生成器的标识符类型。
     * 此方法用于标识生成器使用的具体ID生成策略。
     *
     * @return 当前生成器的标识符类型
     */
    /**
     * 获取当前生成器的标识符类型
     * <p>
     * 此方法用于标识生成器使用的具体ID生成策略。每个实现类都应该
     * 返回一个对应其生成策略的IdType枚举值。
     * </p>
     *
     * @return 当前生成器的标识符类型
     */
    IdType idType();
}
