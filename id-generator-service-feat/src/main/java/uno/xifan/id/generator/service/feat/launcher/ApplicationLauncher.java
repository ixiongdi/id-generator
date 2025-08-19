/**
 * 应用程序启动类
 * <p>
 * 该类用于启动基于 FeatCloud 框架的服务端应用。
 */
package uno.xifan.id.generator.service.feat.launcher;

import tech.smartboot.feat.cloud.FeatCloud;

/**
 * 主启动类
 * <p>
 * 包含程序入口main方法，用于启动服务监听。
 */
public class ApplicationLauncher {
    /**
     * 程序主入口
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        FeatCloud.cloudServer().listen();
    }
}