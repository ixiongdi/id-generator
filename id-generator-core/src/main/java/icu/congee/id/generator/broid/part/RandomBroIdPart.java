package icu.congee.id.generator.broid.part;

import icu.congee.id.generator.broid.BitUtils;
import icu.congee.id.generator.broid.BroIdPart;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Random;

@Data
@AllArgsConstructor
public class RandomBroIdPart implements BroIdPart {


    private final Random random;
    private final int bits;


    @Override
    public List<Boolean> next() {
        return BitUtils.longToList(random.nextLong(), getBits());
    }
}
