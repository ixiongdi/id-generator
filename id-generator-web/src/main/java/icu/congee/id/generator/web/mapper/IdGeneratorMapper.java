package icu.congee.id.generator.web.mapper;


import com.mybatisflex.core.BaseMapper;
import icu.congee.id.generator.web.entity.IdGeneratorEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IdGeneratorMapper extends BaseMapper<IdGeneratorEntity> {
}