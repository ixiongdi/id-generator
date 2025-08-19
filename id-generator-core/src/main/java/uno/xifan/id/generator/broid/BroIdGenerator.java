package uno.xifan.id.generator.broid;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * BroId生成器 用于生成BroId实例
 * 
 * @param <T> BroId的具体实现类型
 */
@Data
public class BroIdGenerator<T extends BroId> {

    private final BroIdLayout layout;
    private final Function<List<Boolean>, T> constructor;

    /**
     * 构造函数
     *
     * @param layout      BroId结构
     * @param constructor T类型的构造器引用
     */
    public BroIdGenerator(BroIdLayout layout, Function<List<Boolean>, T> constructor) {
        this.layout = layout;
        this.constructor = constructor;
    }

    /**
     * 生成下一个BroId
     *
     * @return 生成的BroId
     */
    public T next() {
        List<Boolean> result = new ArrayList<>(layout.getTotalBits());
        layout.getParts()
                .forEach(
                        part -> {
                            result.addAll(part.next());
                        });
        return constructor.apply(result);
    }
}
