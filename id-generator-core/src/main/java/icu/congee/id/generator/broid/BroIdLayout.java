package icu.congee.id.generator.broid;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/** BroId结构定义 定义ID的结构，由多个BroIdPart组成 */
@Data
public class BroIdLayout {

    private final List<BroIdPart> parts;
    private final int totalBits;

    /**
     * 构造函数
     *
     * @param parts BroIdPart列表
     */
    public BroIdLayout(List<BroIdPart> parts) {
        this.parts = new ArrayList<>(parts);
        this.totalBits = this.parts.stream().mapToInt(BroIdPart::getBits).sum();
    }
}
