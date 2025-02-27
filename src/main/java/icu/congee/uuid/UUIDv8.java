package icu.congee.uuid;

import cn.hutool.log.Log;

import java.util.BitSet;
import java.util.UUID;

public class UUIDv8 {
    static private final Log log = Log.get();



    UUID generate() {
        BitSet value = new BitSet(128);
        // 1. 写入48位时间戳（高位的前48位）
        long timestamp = System.currentTimeMillis() & 0xFFFFFFFFFFFFL;
        for (int i = 0; i < 48; i++) {
            if ((timestamp & (1L << i)) != 0) {
                value.set(i);
            }
        }

        // 2. 设置版本号（第49-52位，值为8）
        int version = 0x8;  // UUIDv8版本号
        for (int i = 0; i < 4; i++) {
            value.set(48 + i, ((version >> (3 - i)) & 1) == 1);
        }

        // 3. 设置变体号（低64位的最高两位，值为10b）
        value.set(64, true);  // 第65位
        value.set(65, false); // 第66位

        // 4. 转换为UUID
        long[] longArray = value.toLongArray();
        long msb = longArray.length > 0 ? longArray[0] : 0;
        long lsb = longArray.length > 1 ? longArray[1] : 0;
        return new UUID(msb, lsb);
    }

    public static void main(String[] args){
        UUIDv8 uuidv8 = new UUIDv8();
        log.info(uuidv8.generate().toString());
    }
}
