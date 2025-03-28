/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// 声明包路径，用于组织和管理代码结构
package icu.congee.id.generator.combguid;

// 导入所需的基础接口和类型
import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

// 导入Java标准库中的UUID类和线程安全的随机数生成器
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * CombGuid生成器
 *
 * <p>
 * 基于RT.Comb的实现，将时间信息编码到UUID中，使其可按时间排序。
 * CombGuid通过重新排列标准UUID的字节，将时间戳信息放在开头，从而实现按时间排序的功能。
 *
 * <p>
 * CombGuid结构：
 *
 * <ul>
 * <li>前6字节：Unix时间戳（精确到毫秒）
 * <li>后10字节：随机UUID数据
 * </ul>
 *
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01
 */
// 实现IdGenerator接口的CombGuid生成器类
public class CombGuidGenerator implements IdGenerator {

    /**
     * 生成一个新的CombGuid
     *
     * <p>
     * 该方法首先生成一个随机UUID，然后将当前时间戳编码到其中， 确保生成的ID既保持唯一性又具有时间顺序性。
     *
     * @return 新生成的CombGuid字符串
     */
    public static UUID next() {
        // 使用ThreadLocalRandom生成随机的最高有效位和最低有效位
        long msb = ThreadLocalRandom.current().nextLong();
        long lsb = ThreadLocalRandom.current().nextLong();

        // 设置版本号为4（第12-15位为0100）
        msb &= 0xffffffffffff0fffL; // 清除版本位（将第12-15位置为0）
        msb |= 0x0000000000004000L; // 设置版本号4（将第12-15位置为0100）

        // 设置变体位为RFC 4122（最高两位为10）
        lsb &= 0x3fffffffffffffffL; // 清除高两位（将最高两位置为0）
        lsb |= 0x8000000000000000L; // 设置高两位为10（符合RFC 4122规范）

        // 获取当前时间戳（毫秒级）
        long timestamp = System.currentTimeMillis();

        // 将时间戳编码到UUID的前6字节中
        // 通过位运算将时间戳左移16位，并保留原msb的低16位
        // 这样确保时间信息位于UUID的开头，便于排序
        long newMsb = ((timestamp & 0x0000FFFFFFFFFFFFL) << 16) | (msb & 0x000000000000FFFFL);

        // 创建并返回新的UUID实例
        return new UUID(newMsb, lsb);
    }

    // 实现IdGenerator接口的generate方法
    @Override
    public String generate() {
        // 调用next()方法生成UUID并转换为字符串
        return next().toString();
    }

    // 实现IdGenerator接口的idType方法
    @Override
    public IdType idType() {
        // 返回当前生成器的类型为COMBGUID
        return IdType.COMBGUID;
    }
}
