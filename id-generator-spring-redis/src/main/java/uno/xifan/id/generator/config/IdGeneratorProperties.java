package uno.xifan.id.generator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ID生成器配置属性
 */
/**
 * ID生成器配置属性类
 * <p>
 * 用于加载ID生成相关的配置属性
 * </p>
 *
 * @author 稀饭科技
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "id.generator")
/**
 * 默认构造器创建ID生成器配置属性实例
 * <p>
 * 创建一个配置属性实例，用于管理和维护ID生成器的配置信息。
 * 该实例通过Spring的@ConfigurationProperties注解自动绑定配置文件中的属性。
 * </p>
 * 
 * @since 1.0.0
 */
/**
 * 默认构造器创建ID生成器配置属性实例
 * <p>
 * 创建一个配置属性实例，用于管理和维护ID生成器的配置信息。
 * 该实例通过Spring的@ConfigurationProperties注解自动绑定配置文件中的属性。
 * </p>
 * 
 * @since 1.0.0
 */
public class IdGeneratorProperties {

}