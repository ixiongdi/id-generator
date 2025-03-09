package icu.congee.id.generator.service;


import icu.congee.id.base.IdType;

/** ID生成器服务接口 */
public interface IdGeneratorService {
    Object generate(IdType idType) throws InterruptedException;

    Object[] generate(IdType idType, int count) throws InterruptedException;
}
