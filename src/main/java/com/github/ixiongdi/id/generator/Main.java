package com.github.ixiongdi.id.generator;

public class Main {

    public static void main(String[] args) {

        /** 业务ID，适用于单机环境，和业务强关联的场景 */
        for (int i = 0; i < 10; i++) {
            System.out.println("business id: " + IdUtil.businessId());
        }

        /** 随机ID，短小精悍，适用于单机和小微规模的分布式场景 */
        for (int i = 0; i < 10; i++) {
            System.out.println("random id: " + IdUtil.randomId());
        }

        /** UUID v7，适用于大规模、高并发场景，单机每秒上亿次ID生成 */
        for (int i = 0; i < 10; i++) {
            System.out.println("uuid v7: " + IdUtil.unixTimeBasedUUID());
        }

        /** UUID v8，和v7类似，牺牲一点随机性换来业务相关性，万金油ID生成方案，个人推荐 */
        for (int i = 0; i < 10; i++) {
            System.out.println("uuid v8: " + IdUtil.customUUID());
        }
    }
}
