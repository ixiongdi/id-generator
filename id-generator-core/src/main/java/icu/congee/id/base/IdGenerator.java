package icu.congee.id.base;

public interface IdGenerator {
    Object generate();

    default Object[] generate(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("count must be positive");
        }
        Object[] ids = new Object[count];
        for (int i = 0; i < count; i++) {
            ids[i] = generate();
        }
        return ids;
    }

    IdType idType();
}
