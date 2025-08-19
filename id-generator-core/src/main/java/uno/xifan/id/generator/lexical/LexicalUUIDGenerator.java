package uno.xifan.id.generator.lexical;

import uno.xifan.id.base.IdGenerator;
import uno.xifan.id.base.IdType;

import java.util.UUID;

/** 一个128位的UUID，由64位时间戳和64位工作节点ID组成 */
public class LexicalUUIDGenerator implements IdGenerator {

    public static String next() {
        LexicalUUID lexicalUUID = new LexicalUUID(MicrosecondEpochClock.getInstance());
        return lexicalUUID.toString();
    }

    @Override
    public Object generate() {
        return next();
    }

    @Override
    public IdType idType() {
        return IdType.LexicalUUID;
    }
}
