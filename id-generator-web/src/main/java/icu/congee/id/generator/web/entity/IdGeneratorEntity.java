package icu.congee.id.generator.web.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Table("id_generator")
public class IdGeneratorEntity {
    @Id(keyType = KeyType.None)
    private Object id;
    private String idType;
    private byte[] bytes;
    private String base64;
    private String base62;
    private String base36;
    private String base32;
    private String base16;
    private String base10;
    private LocalDateTime createdAt;

}