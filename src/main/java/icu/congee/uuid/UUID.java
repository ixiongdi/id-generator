package icu.congee.uuid;

import java.util.BitSet;

public class UUID {
    private BitSet value = new BitSet(128);

    private BitSet variant = new BitSet(4);

    private BitSet version = new BitSet(4);
}
