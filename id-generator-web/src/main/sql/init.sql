-- auto-generated definition
create table id_generator
(
    id         varchar(255)                        not null
        primary key,
    id_type    varchar(255)                        null comment '描述id的类型或用途',
    bytes      blob                                null comment '存储字节数据',
    base64     varchar(255)                        null comment 'Base64编码的数据',
    base62     varchar(255)                        null comment 'Base62编码的数据',
    base36     varchar(255)                        null comment 'Base36编码的数据',
    base32     varchar(255)                        null comment 'Base32编码的数据',
    base16     varchar(255)                        null comment 'Base16(Hex)编码的数据',
    base10     varchar(255)                        null comment 'Base10(十进制)表示的数据',
    created_at timestamp default CURRENT_TIMESTAMP null comment '记录创建时间'
);

create index id_generator_id_type_index
    on id_generator (id_type);

