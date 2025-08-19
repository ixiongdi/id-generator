package uno.xifan.id.generator.broid.part;

import uno.xifan.id.generator.broid.BitUtils;
import uno.xifan.id.generator.broid.BroIdPart;

import lombok.Data;

import java.util.List;

@Data
public class VariantBroIdPart implements BroIdPart {

    private final int bits = 2;

    @Override
    public List<Boolean> next() {
        return BitUtils.longToList(2, getBits());
    }
}
