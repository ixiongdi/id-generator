package icu.congee.id.generator.xid;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

public class XidGenerator implements IdGenerator {
    @Override
    public String generate() {
        return Xid.string();
    }

    @Override
    public IdType idType() {
        return IdType.XID;
    }
}
