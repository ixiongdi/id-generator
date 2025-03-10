package icu.congee.id.generator.broid;

import java.util.List;

/** BroId组成部分接口 ID组成部分，是一个接口，属性value返回一个List<Boolean>，bit限制字段长度 */
public interface BroIdPart {

    /**
     * 获取该部分的位长度
     *
     * @return 位长度
     */
    int getBits();

    /**
     * 生成下一个值
     *
     * @return 生成的List<Boolean>对象
     */
    List<Boolean> next();
}
