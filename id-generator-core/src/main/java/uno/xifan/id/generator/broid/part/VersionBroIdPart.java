package uno.xifan.id.generator.broid.part;

import uno.xifan.id.generator.broid.BitUtils;
import uno.xifan.id.generator.broid.BroIdPart;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class VersionBroIdPart implements BroIdPart {

    private final int version;

    @Override
    public int getBits() {
        return 4;
    }

    @Override
    public List<Boolean> next() {
        return BitUtils.longToList(version, getBits());
    }
}
