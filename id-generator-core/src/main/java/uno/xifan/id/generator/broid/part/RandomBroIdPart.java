package uno.xifan.id.generator.broid.part;

import uno.xifan.id.generator.broid.BitUtils;
import uno.xifan.id.generator.broid.BroIdPart;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Random;

/**
 * BroId生成器的随机部分实现
 *
 * <p>
 * 该类负责生成BroId中的随机位序列。通过指定的随机数生成器和位数，
 * 生成指定长度的随机布尔序列。
 *
 * @author 稀饭科技
 */
@Data
@AllArgsConstructor
public class RandomBroIdPart implements BroIdPart {

    /**
     * 用于生成随机数的随机数生成器
     */
    private final Random random;

    /**
     * 需要生成的随机位数
     */
    private final int bits;

    @Override
    public List<Boolean> next() {
        return BitUtils.longToList(random.nextLong(), getBits());
    }
}
