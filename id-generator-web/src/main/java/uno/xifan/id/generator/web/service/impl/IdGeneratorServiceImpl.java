package uno.xifan.id.generator.web.service.impl;


import com.mybatisflex.spring.service.impl.ServiceImpl;
import uno.xifan.id.generator.web.entity.IdGeneratorEntity;
import uno.xifan.id.generator.web.mapper.IdGeneratorMapper;
import uno.xifan.id.generator.web.service.IdGeneratorService;
import org.springframework.stereotype.Service;

@Service
public class IdGeneratorServiceImpl extends ServiceImpl<IdGeneratorMapper, IdGeneratorEntity> implements IdGeneratorService {

}