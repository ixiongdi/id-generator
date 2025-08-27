/*
 * MIT License
 *
 * Copyright (c) 2024 Andy Xiong
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package uno.xifan.id.generator.springai.mcp.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import uno.xifan.id.generator.springai.mcp.server.service.IdGeneratorMcpService;

/**
 * Spring AI MCP Server Application using modern Spring AI 1.0.1 features
 *
 * @author Andy Xiong (ixiongdi@gmail.com)
 */
@SpringBootApplication
public class IdGeneratorSpringAiMcpServerApplication {

    private static final Logger logger = LoggerFactory.getLogger(IdGeneratorSpringAiMcpServerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(IdGeneratorSpringAiMcpServerApplication.class, args);
    }

    @Bean
    public ToolCallbackProvider weatherTools(IdGeneratorMcpService idGeneratorMcpService) {
        return MethodToolCallbackProvider.builder().toolObjects(idGeneratorMcpService).build();
    }

}
