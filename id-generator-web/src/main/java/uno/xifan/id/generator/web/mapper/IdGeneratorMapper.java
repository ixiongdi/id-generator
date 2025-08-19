package uno.xifan.id.generator.web.mapper;


import com.mybatisflex.core.BaseMapper;
import uno.xifan.id.generator.web.entity.IdGeneratorEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IdGeneratorMapper extends BaseMapper<IdGeneratorEntity> {
}