# BorId

这是一个完全自定义的Id生成器。

代码结构定义：

- BorId：value为ByteArray，重写toString方法返回Base62编码值
- BorIdGenerator：Id生成器，构造函数参数为BorIdLayout，函数next返回BorId
- BorIdLayout：Id结构，其构造函数为List<BorIdPart>
- BorIdPart: Id组成部分，是一个接口，属性value返回一个ByteArray，bit限制字段长度
方法next的参数是一个函数式接口，支持自定义，返回value
方法encode的参数是一个函数接口，支持自定义编码方式。默认为Base62Encoder
- InstantBorIdPart extends BorIdPart：value是ByteArray，next的默认实现为当前时间戳
- CounterBorIdPart extends BorIdPart：value是ByteArray，next的默认实现为全局自增的值，支持传入时间参数
- EigenvalueBorIdPart extends BorIdPart：value是ByteArray，next的默认实现为获取本机48位Mac地址+16位Pid
- RandomBorIdPart extends BorIdPart：value是ByteArray，next的默认实现为Random.nextLong
- BorIdPartEncoder：一个接口，函数default encode参数为ByteArray，返回Object，默认实现为Base62Encoder