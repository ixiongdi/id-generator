package icu.congee.id.generator.service;


public interface DistributedIdGenerator {

    Object generate();

    Object[] generate(int count);
}
