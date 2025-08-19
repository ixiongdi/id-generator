package uno.xifan.id.generator.broid.part;

import uno.xifan.id.generator.broid.BitUtils;
import uno.xifan.id.generator.broid.BroIdPart;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SpatiallyBroIdPart implements BroIdPart {



    private final long value;

    private final int bits;

    @Override
    public List<Boolean> next() {
        return BitUtils.longToList(value, getBits());
    }
}
