package icu.congee.id.generator.custom.part;

/**
 * ID部分信息类
 * <p>
 * 该类用于存储ID部分的信息，包括部分对象和部分名称。
 * 主要用于支持自定义ID部分的顺序和可选性。
 *
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01
 */
public class IdPartInfo {
    
    // ID部分对象
    private final IdPart part;
    
    // ID部分名称
    private final String name;
    
    /**
     * 构造函数
     *
     * @param part ID部分对象
     * @param name ID部分名称
     */
    public IdPartInfo(IdPart part, String name) {
        this.part = part;
        this.name = name;
    }
    
    /**
     * 获取ID部分对象
     *
     * @return ID部分对象
     */
    public IdPart getPart() {
        return part;
    }
    
    /**
     * 获取ID部分名称
     *
     * @return ID部分名称
     */
    public String getName() {
        return name;
    }
}