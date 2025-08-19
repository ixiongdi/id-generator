package uno.xifan.id.generator.demo.task;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.log.Log;
import uno.xifan.id.base.IdType;
import uno.xifan.id.generator.demo.entity.IdGeneratorDistributedEntity;
import uno.xifan.id.generator.demo.service.IdGeneratorDistributedService;
import uno.xifan.id.generator.distributed.atomiclong.AtomicLongIdGenerator;
import uno.xifan.id.generator.distributed.cosid.CosId;
import uno.xifan.id.generator.distributed.cosid.CosIdGenerator;
import uno.xifan.id.generator.distributed.dtsid.DtsId;
import uno.xifan.id.generator.distributed.dtsid.DtsIdGenerator;
import uno.xifan.id.generator.distributed.mist.MistId;
import uno.xifan.id.generator.distributed.mist.MistIdGenerator;
import uno.xifan.id.generator.distributed.rid.RedissonIdGenerator;
import uno.xifan.id.generator.distributed.snowflake.SnowflakeIdGenerator;
import uno.xifan.id.generator.distributed.ttsid.*;
import uno.xifan.id.generator.distributed.uuid.UUIDv8;
import uno.xifan.id.generator.distributed.uuid.UUIDv8Generator;
import uno.xifan.id.generator.distributed.wxseq.WxSeq;
import uno.xifan.id.generator.distributed.wxseq.WxSeqGenerator;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * ID生成器任务类，负责定期生成各种类型的ID并存储。
 * 该类通过ServiceLoader加载所有可用的ID生成器实现，并按照固定时间间隔生成ID。
 */
@Component
public class IdGeneratorDistributedTask {

    private final Log log = Log.get();

    /**
     * ID服务接口，用于存储生成的ID
     */
    @Resource
    private IdGeneratorDistributedService idGeneratorDistributedService;

    @Resource
    private AtomicLongIdGenerator atomicLongIdGenerator;

    @Resource
    private CosIdGenerator cosIdGenerator;

    @Resource
    private DtsIdGenerator dtsIdGenerator;

    @Resource
    private MistIdGenerator mistIdGenerator;

    @Resource
    private RedissonIdGenerator redissonIdGenerator;

    @Resource
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @Resource
    private TtsIdGenerator ttsIdGenerator;

    @Resource
    private TtsIdPlusGenerator ttsIdPlusGenerator;

    @Resource
    private TtsIdProGenerator ttsIdProGenerator;

    @Resource
    private TtsIdProMaxGenerator ttsIdProMaxGenerator;

    @Resource
    private WxSeqGenerator wxSeqGenerator;

    @Resource
    private UUIDv8Generator uuiDv8Generator;


    /**
     * 定期执行的ID生成任务。
     * 每秒执行一次，遍历所有可用的ID生成器，生成ID并存储。
     */
    @Scheduled(cron = "0 * * * * *")
    public void generate() {
        {
            Long id = atomicLongIdGenerator.generate();
            log.info("{}", id);
            IdGeneratorDistributedEntity idGeneratorDistributedEntity = new IdGeneratorDistributedEntity();
            idGeneratorDistributedEntity.setIdType(IdType.RAtomicLong.getName());
            idGeneratorDistributedEntity.setBase10(id.toString());
            idGeneratorDistributedService.save(idGeneratorDistributedEntity);
        }
        {
            CosId id = cosIdGenerator.generate();
            log.info("{}", id);
            IdGeneratorDistributedEntity idGeneratorDistributedEntity = new IdGeneratorDistributedEntity();
            idGeneratorDistributedEntity.setIdType(IdType.CosId.getName());
            idGeneratorDistributedEntity.setBase64(id.toBase62());
            idGeneratorDistributedEntity.setBase64(id.toBase36());
            idGeneratorDistributedService.save(idGeneratorDistributedEntity);
        }
        {
            DtsId id = dtsIdGenerator.generate();
            log.info("{}", id);
            IdGeneratorDistributedEntity idGeneratorDistributedEntity = new IdGeneratorDistributedEntity();
            idGeneratorDistributedEntity.setIdType(IdType.DtsId.getName());
            idGeneratorDistributedEntity.setBase10(String.valueOf(id.toLong()));
            idGeneratorDistributedService.save(idGeneratorDistributedEntity);
        }
        {
            MistId id = mistIdGenerator.generate();
            log.info("{}", id);
            IdGeneratorDistributedEntity idGeneratorDistributedEntity = new IdGeneratorDistributedEntity();
            idGeneratorDistributedEntity.setIdType(IdType.MIST_ID.getName());
            idGeneratorDistributedEntity.setBase10(String.valueOf(id));
            idGeneratorDistributedService.save(idGeneratorDistributedEntity);
        }
        {
            Long id = redissonIdGenerator.generate();
            log.info("{}", id);
            IdGeneratorDistributedEntity idGeneratorDistributedEntity = new IdGeneratorDistributedEntity();
            idGeneratorDistributedEntity.setIdType(IdType.RID.getName());
            idGeneratorDistributedEntity.setBase10(String.valueOf(id));
            idGeneratorDistributedService.save(idGeneratorDistributedEntity);
        }
        {
            Long id = snowflakeIdGenerator.generate();
            log.info("{}", id);
            IdGeneratorDistributedEntity idGeneratorDistributedEntity = new IdGeneratorDistributedEntity();
            idGeneratorDistributedEntity.setIdType(IdType.Snowflake.getName());
            idGeneratorDistributedEntity.setBase10(String.valueOf(id));
            idGeneratorDistributedService.save(idGeneratorDistributedEntity);
        }
        {
            Long id = snowflakeIdGenerator.generate();
            log.info("{}", id);
            IdGeneratorDistributedEntity idGeneratorDistributedEntity = new IdGeneratorDistributedEntity();
            idGeneratorDistributedEntity.setIdType(IdType.Snowflake.getName());
            idGeneratorDistributedEntity.setBase10(String.valueOf(id));
            idGeneratorDistributedService.save(idGeneratorDistributedEntity);
        }
        {
            TtsId id = ttsIdGenerator.generate();
            log.info("{}", id);
            IdGeneratorDistributedEntity idGeneratorDistributedEntity = new IdGeneratorDistributedEntity();
            idGeneratorDistributedEntity.setIdType(IdType.TtsId.getName());
            idGeneratorDistributedEntity.setBytes(id.toBytes());
            idGeneratorDistributedEntity.setBase64(id.toBase64());
            idGeneratorDistributedEntity.setBase62(id.toBase62());
            idGeneratorDistributedEntity.setBase36(id.toBase36());
            idGeneratorDistributedEntity.setBase32(id.toBase32());
            idGeneratorDistributedEntity.setBase16(id.toBase16());
            idGeneratorDistributedEntity.setBase10(id.toBase10());
            idGeneratorDistributedService.save(idGeneratorDistributedEntity);
        }
        {
            TtsIdPlus id = ttsIdPlusGenerator.generate();
            log.info("{}", id);
            IdGeneratorDistributedEntity idGeneratorDistributedEntity = new IdGeneratorDistributedEntity();
            idGeneratorDistributedEntity.setIdType(IdType.TtsId.getName());
            idGeneratorDistributedEntity.setBytes(id.toBytes());
            idGeneratorDistributedEntity.setBase64(id.toBase64());
            idGeneratorDistributedEntity.setBase62(id.toBase62());
            idGeneratorDistributedEntity.setBase36(id.toBase36());
            idGeneratorDistributedEntity.setBase32(id.toBase32());
            idGeneratorDistributedEntity.setBase16(id.toBase16());
            idGeneratorDistributedEntity.setBase10(id.toBase10());
            idGeneratorDistributedService.save(idGeneratorDistributedEntity);
        }
        {
            TtsIdPro id = ttsIdProGenerator.generate();
            log.info("{}", id);
            IdGeneratorDistributedEntity idGeneratorDistributedEntity = new IdGeneratorDistributedEntity();
            idGeneratorDistributedEntity.setIdType(IdType.TtsId.getName());
            idGeneratorDistributedEntity.setBytes(id.toBytes());
            idGeneratorDistributedEntity.setBase64(id.toBase64());
            idGeneratorDistributedEntity.setBase62(id.toBase62());
            idGeneratorDistributedEntity.setBase36(id.toBase36());
            idGeneratorDistributedEntity.setBase32(id.toBase32());
            idGeneratorDistributedEntity.setBase16(id.toBase16());
            idGeneratorDistributedEntity.setBase10(id.toBase10());
            idGeneratorDistributedService.save(idGeneratorDistributedEntity);
        }
        {
            TtsIdProMax id = ttsIdProMaxGenerator.generate();
            log.info("{}", id);
            IdGeneratorDistributedEntity idGeneratorDistributedEntity = new IdGeneratorDistributedEntity();
            idGeneratorDistributedEntity.setIdType(IdType.TtsId.getName());
            idGeneratorDistributedEntity.setBytes(id.toBytes());
            idGeneratorDistributedEntity.setBase64(id.toBase64());
            idGeneratorDistributedEntity.setBase62(id.toBase62());
            idGeneratorDistributedEntity.setBase36(id.toBase36());
            idGeneratorDistributedEntity.setBase32(id.toBase32());
            idGeneratorDistributedEntity.setBase16(id.toBase16());
            idGeneratorDistributedEntity.setBase10(id.toBase10());
            idGeneratorDistributedService.save(idGeneratorDistributedEntity);
        }
        {
            WxSeq id = wxSeqGenerator.generate(RandomUtil.randomLong(1, 10));
            log.info("{}", id);
            IdGeneratorDistributedEntity idGeneratorDistributedEntity = new IdGeneratorDistributedEntity();
            idGeneratorDistributedEntity.setIdType(IdType.WxSeq.getName());
            idGeneratorDistributedEntity.setBytes(id.toBytes());
            idGeneratorDistributedEntity.setBase64(id.toBase64());
            idGeneratorDistributedEntity.setBase62(id.toBase62());
            idGeneratorDistributedEntity.setBase36(id.toBase36());
            idGeneratorDistributedEntity.setBase32(id.toBase32());
            idGeneratorDistributedEntity.setBase16(id.toBase16());
            idGeneratorDistributedEntity.setBase10(id.toBase10());
            idGeneratorDistributedService.save(idGeneratorDistributedEntity);
        }
        {
            UUIDv8 id = uuiDv8Generator.generate();
            log.info("{}", id);
            IdGeneratorDistributedEntity idGeneratorDistributedEntity = new IdGeneratorDistributedEntity();
            idGeneratorDistributedEntity.setIdType(IdType.UUIDv8.getName());
            idGeneratorDistributedEntity.setBytes(id.toBytes());
            idGeneratorDistributedService.save(idGeneratorDistributedEntity);
        }
    }
}
