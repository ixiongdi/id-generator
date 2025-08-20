package uno.xifan.id.generator.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uno.xifan.id.base.Base62;
import uno.xifan.id.util.IdUtil;
import uno.xifan.id.generator.snowflake.SnowflakeIdGenerator;
import uno.xifan.id.generator.cosid.CosIdGenerator;
// import kept deliberately minimal; specific IdUtil covers most UUID variants
import uno.xifan.id.generator.ksuid.KsuidGenerator;
import uno.xifan.id.generator.objectid.ObjectIdGenerator;
import uno.xifan.id.generator.xid.XidGenerator;
import uno.xifan.id.generator.flake.FlakeIdGenerator;
import uno.xifan.id.generator.sonyflake.SonyflakeIdGenerator;
import uno.xifan.id.generator.shardingid.ShardingIdGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 提供算法枚举与在线生成接口
 */
@RestController
@RequestMapping("/api")
public class IdGenerateController {

    /**
     * 返回可用算法及其参数定义
     */
    @GetMapping("/algorithms")
    public List<Map<String, Object>> listAlgorithms() {
        List<Map<String, Object>> list = new ArrayList<>();

        // 无参数算法
        list.add(alg("uuid_v1", "UUID v1", "基于时间戳与节点信息的UUID", null));
        list.add(alg("uuid_v2", "UUID v2", "DCE安全版本UUID", null));
        list.add(alg("uuid_v4", "UUID v4", "随机数生成的UUID", null));
        list.add(alg("uuid_v6", "UUID v6", "时间可排序的UUID v6", null));
        list.add(alg("uuid_v7", "UUID v7", "基于Unix时间的时间可排序UUID", null));
        list.add(alg("ulid", "ULID", "按时间可排序、Base32编码", null));
        list.add(alg("ksuid", "KSUID", "按时间可排序、Base62编码", null));
        list.add(alg("xid", "XID", "紧凑的全局唯一ID", null));
        list.add(alg("object_id", "ObjectId", "MongoDB风格ObjectId", null));
        list.add(alg("ordered_uuid", "Ordered UUID", "时间前缀的有序UUID", null));
        list.add(alg("lexical_uuid", "Lexical UUID", "字典序可排序UUID", null));
        list.add(alg("nano_id", "NanoId", "短小URL安全ID", null));
        list.add(alg("push_id", "Push ID", "Firebase风格Push ID", null));
        list.add(alg("sid", "SID", "时间戳+随机数Base64", null));
        list.add(alg("combguid", "COMB GUID", "时间有序的GUID", null));
        list.add(alg("elastic_flake", "Elastic Flake", "适配ES的雪花ID", null));
        list.add(alg("business_id", "业务时间ID", "yyMMddHHmmss+序号", null));
        list.add(alg("entropy", "时间熵ID", "时间+熵值的长整型", null));
        list.add(alg("js_safety_id", "JS安全ID", "不会超出JS安全整数范围", null));
        list.add(alg("flake", "Flake", "MAC派生节点ID的变体雪花", null));
        list.add(alg("sonyflake", "Sonyflake", "10ms粒度、8位节点、8位序列", null));
        list.add(alg("sharding_id", "Sharding ID", "Instagram风格分片ID(示例)", null));

        // 带参数算法
        List<Map<String, Object>> snowflakeParams = new ArrayList<>();
        snowflakeParams.add(param("workerId", "工作节点ID", "number", 0, 1023, null));
        list.add(alg("snowflake", "Snowflake", "64位时间有序分布式ID", snowflakeParams));

        List<Map<String, Object>> cosidParams = new ArrayList<>();
        cosidParams.add(param("machineId", "机器ID", "number", 0, 1_048_575, null));
        cosidParams.add(param("epoch", "纪元毫秒(可选)", "number", 0, null, 0));
        list.add(alg("cos_id", "CosId", "80位(44位时间+20位机器+16位序列)", cosidParams));

        return list;
    }

    /**
     * 根据算法与参数生成ID，支持批量
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody(required = false) Map<String, Object> body) {
        String algorithm = body == null ? null : (String) body.get("algorithm");
        if (!StringUtils.hasText(algorithm)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error("algorithm 不能为空"));
        }

        int count = 1;
        Object cnt = body != null ? body.get("count") : null;
        if (cnt instanceof Number n) {
            count = Math.max(1, Math.min(1000, n.intValue()));
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> params = body == null ? new HashMap<>() : (Map<String, Object>) body.getOrDefault("params", new HashMap<>());

        Function<Map<String, Object>, String> generator = resolve(algorithm);
        if (generator == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error("不支持的算法: " + algorithm));
        }

        List<String> results = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            results.add(generator.apply(params));
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("algorithm", algorithm);
        resp.put("count", count);
        resp.put("results", results);
        return ResponseEntity.ok(resp);
    }

    private Map<String, Object> alg(String key, String label, String desc, List<Map<String, Object>> params) {
        Map<String, Object> map = new HashMap<>();
        map.put("key", key);
        map.put("label", label);
        map.put("desc", desc);
        map.put("params", params == null ? List.of() : params);
        return map;
    }

    private Map<String, Object> param(String name, String label, String type, Integer min, Integer max, Integer def) {
        Map<String, Object> m = new HashMap<>();
        m.put("name", name);
        m.put("label", label);
        m.put("type", type);
        if (min != null) m.put("min", min);
        if (max != null) m.put("max", max);
        if (def != null) m.put("default", def);
        return m;
    }

    private Map<String, Object> error(String msg) {
        Map<String, Object> m = new HashMap<>();
        m.put("error", msg);
        return m;
    }

    private Function<Map<String, Object>, String> resolve(String algorithm) {
        return switch (algorithm) {
            case "uuid_v1" -> p -> IdUtil.uuid1();
            case "uuid_v2" -> p -> IdUtil.uuid2();
            case "uuid_v4" -> p -> IdUtil.uuid4();
            case "uuid_v6" -> p -> IdUtil.uuid6();
            case "uuid_v7" -> p -> IdUtil.uuid7();
            case "ulid" -> p -> IdUtil.ulid();
            case "ksuid" -> p -> KsuidGenerator.next();
            case "xid" -> p -> XidGenerator.next();
            case "object_id" -> p -> new ObjectIdGenerator().generate();
            case "ordered_uuid" -> p -> IdUtil.orderedUuid();
            case "lexical_uuid" -> p -> IdUtil.lexicalUuid();
            case "nano_id" -> p -> IdUtil.nanoId();
            case "push_id" -> p -> IdUtil.pushId();
            case "sid" -> p -> IdUtil.sid();
            case "combguid" -> p -> IdUtil.combguid();
            case "elastic_flake" -> p -> IdUtil.elasticflake();
            case "business_id" -> p -> String.valueOf(IdUtil.businessId());
            case "entropy" -> p -> String.valueOf(IdUtil.entropy());
            case "js_safety_id" -> p -> String.valueOf(IdUtil.javaScriptSafetyId());
            case "flake" -> p -> String.valueOf(new FlakeIdGenerator().generate());
            case "sonyflake" -> p -> String.valueOf(new SonyflakeIdGenerator().generate());
            case "sharding_id" -> p -> String.valueOf(new ShardingIdGenerator().generate());
            case "snowflake" -> this::genSnowflake;
            case "cos_id" -> this::genCosId;
            default -> null;
        };
    }

    private String genSnowflake(Map<String, Object> params) {
        long workerId = toLong(params.get("workerId"), 0L);
        SnowflakeIdGenerator g = new SnowflakeIdGenerator(workerId);
        return String.valueOf(g.generate());
    }

    private String genCosId(Map<String, Object> params) {
        long machineId = toLong(params.get("machineId"), 0L);
        long epoch = toLong(params.get("epoch"), 0L);
        CosIdGenerator g = new CosIdGenerator(machineId, epoch);
        byte[] bytes = g.generateId();
        return Base62.encode(bytes);
    }

    private long toLong(Object v, long def) {
        if (v instanceof Number n) {
            return n.longValue();
        }
        if (v instanceof String s && StringUtils.hasText(s)) {
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException ignored) {
            }
        }
        return def;
    }
}


