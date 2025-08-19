package uno.xifan.id.generator.xid;

import uno.xifan.id.base.IdGenerator;
import uno.xifan.id.base.IdType;

public class XidGenerator implements IdGenerator {
    @Override
    public String generate() {
        return Xid.string();
    }

    @Override
    public IdType idType() {
        return IdType.XID;
    }

    public static String next() {
        return Xid.string();
    }
}
