package icu.congee.id.generator.distributed.cosid;


import icu.congee.id.base.Id;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.ByteBuffer;

@Data
@AllArgsConstructor
public class CosId implements Id {

    // 44bit
    private long timestamp;
    // 20bit
    private long machineId;
    // 16bit
    private long sequence;

    @Override
    public byte[] toBytes() {
        // 分配10字节的缓冲区（80位）
        ByteBuffer buffer = ByteBuffer.allocate(10);

        // 写入时间戳（44位，占用5.5字节）
        buffer.put((byte) (timestamp >>> 36)); // 时间戳的高8位
        buffer.put((byte) (timestamp >>> 28 & 0xFF)); // 时间戳的次高8位
        buffer.put((byte) (timestamp >>> 20 & 0xFF)); // 时间戳的中间8位
        buffer.put((byte) (timestamp >>> 12 & 0xFF)); // 时间戳的次低8位
        buffer.put((byte) (timestamp >>> 4 & 0xFF)); // 时间戳的低8位

        // 处理第6字节（时间戳最后4位 + 机器ID高4位）
        byte sixthByte = (byte) ((timestamp & 0x0F) << 4); // 时间戳最后4位左移4位
        sixthByte |= (byte) (machineId >>> 16 & 0x0F); // 合并机器ID的高4位
        buffer.put(sixthByte);

        // 写入机器ID的剩余16位（占用2字节）
        buffer.putShort((short) (machineId & 0xFFFF));

        // 写入序列号（16位，占用2字节）
        buffer.putShort((short) sequence);

        // 返回完整的字节数组
        return buffer.array();
    }

    @Override
    public long toLong() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
