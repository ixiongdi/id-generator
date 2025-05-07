package icu.congee.id.generator.web.service.impl;


import com.mybatisflex.spring.service.impl.ServiceImpl;
import icu.congee.id.generator.web.entity.IdGeneratorEntity;
import icu.congee.id.generator.web.mapper.IdGeneratorMapper;
import icu.congee.id.generator.web.service.IdGeneratorService;
import org.springframework.stereotype.Service;

@Service
public class IdGeneratorServiceImpl extends ServiceImpl<IdGeneratorMapper, IdGeneratorEntity> implements IdGeneratorService {

}