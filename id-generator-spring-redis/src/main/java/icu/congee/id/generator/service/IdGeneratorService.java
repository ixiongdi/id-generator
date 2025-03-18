/*
 * MIT License
 *
 * Copyright (c) 2024 ixiongdi
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

package icu.congee.id.generator.service;

import icu.congee.id.base.IdType;

/** ID生成器服务接口 */
public interface IdGeneratorService {
    /**
     * 生成单个分布式ID
     * 
     * @param idType ID类型枚举
     * @return 生成的分布式ID对象
     * @throws InterruptedException 当获取分布式锁时被中断抛出
     */
    Object generate(IdType idType) throws InterruptedException;

    /**
     * 批量生成分布式ID
     * 
     * @param idType ID类型枚举
     * @param count  需要生成的ID数量
     * @return 包含生成ID的数组
     * @throws InterruptedException 当获取分布式锁时被中断抛出
     */
    Object[] generate(IdType idType, int count) throws InterruptedException;
}
