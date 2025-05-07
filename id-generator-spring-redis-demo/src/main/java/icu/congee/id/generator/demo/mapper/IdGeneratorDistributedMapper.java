package icu.congee.id.generator.demo.mapper;


import com.mybatisflex.core.BaseMapper;
import icu.congee.id.generator.demo.entity.IdGeneratorDistributedEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IdGeneratorDistributedMapper extends BaseMapper<IdGeneratorDistributedEntity> {
}