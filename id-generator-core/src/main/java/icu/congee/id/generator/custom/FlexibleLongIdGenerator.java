package icu.congee.id.generator.custom;

import icu.congee.id.base.IdType;
import icu.congee.id.generator.custom.part.IdPart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 灵活的长整型ID生成器实现类
 * <p>
 * 该类实现了LongIdGenerator接口，提供完全自定义的64位长整型ID生成功能。
 * 用户可以通过提供IdPart列表来自定义ID的组成部分和顺序。
 *
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01
 */
public class FlexibleLongIdGenerator implements LongIdGenerator {
    
    // ID部分列表，包含了顺序和有无信息
    private final List<IdPart> idParts;
    
    // ID部分名称映射，用于格式化和获取各部分值
    private final Map<String, IdPart> partMap;
    
    /**
     * 使用指定的ID部分列表创建灵活ID生成器
     *
     * @param idParts ID部分列表
     */
    public FlexibleLongIdGenerator(List<IdPart> idParts) {
        if (idParts == null || idParts.isEmpty()) {
            throw new IllegalArgumentException("ID部分列表不能为空");
        }
        
        this.idParts = new ArrayList<>(idParts);
        this.partMap = new HashMap<>();
        
        // 初始化部分名称映射
        for (int i = 0; i < idParts.size(); i++) {
            String partName = "part" + i;
            partMap.put(partName, idParts.get(i));
        }
    }
    
    /**
     * 使用指定的ID部分列表和名称映射创建灵活ID生成器
     *
     * @param idParts ID部分列表
     * @param partNames ID部分名称列表，与idParts一一对应
     */
    public FlexibleLongIdGenerator(List<IdPart> idParts, List<String> partNames) {
        if (idParts == null || idParts.isEmpty()) {
            throw new IllegalArgumentException("ID部分列表不能为空");
        }
        
        if (partNames == null || partNames.size() != idParts.size()) {
            throw new IllegalArgumentException("ID部分名称列表必须与ID部分列表大小相同");
        }
        
        this.idParts = new ArrayList<>(idParts);
        this.partMap = new HashMap<>();
        
        // 初始化部分名称映射
        for (int i = 0; i < idParts.size(); i++) {
            partMap.put(partNames.get(i), idParts.get(i));
        }
    }
    
    /**
     * 使用指定的ID部分映射创建灵活ID生成器
     *
     * @param partMap ID部分名称到ID部分的映射
     */
    public FlexibleLongIdGenerator(Map<String, IdPart> partMap) {
        if (partMap == null || partMap.isEmpty()) {
            throw new IllegalArgumentException("ID部分映射不能为空");
        }
        
        this.partMap = new HashMap<>(partMap);
        this.idParts = new ArrayList<>(partMap.values());
    }
    
    /**
     * 刷新ID各部分的值
     */
    private void refreshParts() {
        for (IdPart part : idParts) {
            part.refreshAndGet();
        }
    }
    
    /**
     * 生成一个64位的长整型ID
     *
     * @return 生成的长整型ID
     */
    @Override
    public Long generate() {
        refreshParts();
        
        // 计算各部分的位移并组合生成最终的ID
        long id = 0L;
        int currentShift = 0;
        
        // 从列表末尾开始，这样第一个部分会在最低位
        for (int i = idParts.size() - 1; i >= 0; i--) {
            IdPart part = idParts.get(i);
            id |= (part.getValue() << currentShift);
            currentShift += part.getBits();
        }
        
        return id;
    }
    
    /**
     * 使用指定的格式生成ID
     * <p>
     * 格式字符串中可以包含以下占位符：
     * <ul>
     *   <li>{partName} - 对应partMap中的部分名称</li>
     * </ul>
     * 如果使用标准名称，则可以包含：
     * <ul>
     *   <li>{ts} - 时间戳部分</li>
     *   <li>{wid} - 工作节点ID部分</li>
     *   <li>{seq} - 序列号部分</li>
     *   <li>{rnd} - 随机数部分</li>
     * </ul>
     *
     * @param format 格式字符串，定义ID各部分的组合方式
     * @return 按照指定格式生成的ID
     */
    @Override
    public Long generateWithFormat(String format) {
        refreshParts();
        
        // 解析格式字符串，替换占位符
        String result = format;
        
        // 替换所有部分的占位符
        for (Map.Entry<String, IdPart> entry : partMap.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            result = result.replace(placeholder, String.valueOf(entry.getValue().getValue()));
        }
        
        // 兼容标准名称
        if (partMap.containsKey("timestamp")) {
            result = result.replace("{ts}", String.valueOf(partMap.get("timestamp").getValue()));
        }
        
        if (partMap.containsKey("workerId")) {
            result = result.replace("{wid}", String.valueOf(partMap.get("workerId").getValue()));
        }
        
        if (partMap.containsKey("sequence")) {
            result = result.replace("{seq}", String.valueOf(partMap.get("sequence").getValue()));
        }
        
        if (partMap.containsKey("random")) {
            result = result.replace("{rnd}", String.valueOf(partMap.get("random").getValue()));
        }
        
        try {
            return Long.parseLong(result);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("格式化后的ID不是有效的长整型: " + result, e);
        }
    }
    
    /**
     * 获取当前生成器的时间戳部分
     *
     * @return 时间戳部分的值，如果不存在则返回0
     */
    @Override
    public long getTimestampPart() {
        IdPart part = partMap.get("timestamp");
        return part != null ? part.getValue() : 0;
    }
    
    /**
     * 获取当前生成器的工作节点ID部分
     *
     * @return 工作节点ID部分的值，如果不存在则返回0
     */
    @Override
    public long getWorkerIdPart() {
        IdPart part = partMap.get("workerId");
        return part != null ? part.getValue() : 0;
    }
    
    /**
     * 获取当前生成器的序列号部分
     *
     * @return 序列号部分的值，如果不存在则返回0
     */
    @Override
    public long getSequencePart() {
        IdPart part = partMap.get("sequence");
        return part != null ? part.getValue() : 0;
    }
    
    /**
     * 获取当前生成器的随机数部分
     *
     * @return 随机数部分的值，如果不存在则返回0
     */
    @Override
    public long getRandomPart() {
        IdPart part = partMap.get("random");
        return part != null ? part.getValue() : 0;
    }
    
    /**
     * 获取指定名称的ID部分值
     *
     * @param partName ID部分名称
     * @return ID部分的值，如果不存在则返回0
     */
    public long getPartValue(String partName) {
        IdPart part = partMap.get(partName);
        return part != null ? part.getValue() : 0;
    }
    
    /**
     * 获取指定名称的ID部分字节数组
     *
     * @param partName ID部分名称
     * @return ID部分的字节数组，如果不存在则返回空数组
     */
    public byte[] getPartBytes(String partName) {
        IdPart part = partMap.get(partName);
        return part != null ? part.getBytes() : new byte[0];
    }
    
    /**
     * 获取ID类型
     *
     * @return ID类型
     */
    @Override
    public IdType idType() {
        return IdType.CustomTimeBasedRandomId; // 使用现有的类型
    }
    
    /**
     * 获取ID部分列表
     *
     * @return ID部分列表的副本
     */
    public List<IdPart> getIdParts() {
        return new ArrayList<>(idParts);
    }
    
    /**
     * 获取ID部分映射
     *
     * @return ID部分映射的副本
     */
    public Map<String, IdPart> getPartMap() {
        return new HashMap<>(partMap);
    }
}