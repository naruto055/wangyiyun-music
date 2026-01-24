package com.naruto.wangyiyunmusic;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;

import java.util.Collections;

/**
 * MyBatis-Plus 代码生成器
 *
 * 使用说明：
 * 1. 确保数据库已创建并执行了建表脚本（docs/sql/music.sql）
 * 2. 修改数据库连接信息（url, username, password）
 * 3. 运行 main 方法生成代码
 * 4. 生成的代码会自动覆盖到对应目录
 *
 * @Author: naruto
 * @CreateTime: 2026-01-24
 */
public class CodeGenerator {

    public static void main(String[] args) {
        // 数据库连接信息
        String url = "jdbc:mysql://localhost:3306/wangyiyun-music?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "root123.";

        FastAutoGenerator.create(url, username, password)
            // 全局配置
            .globalConfig(builder -> {
                builder.author("naruto") // 作者
                    .outputDir(System.getProperty("user.dir") + "/src/main/java") // 输出目录
                    .commentDate("yyyy-MM-dd"); // 注释日期格式
            })
            // 包配置
            .packageConfig(builder -> {
                builder.parent("com.naruto.wangyiyunmusic") // 父包名
                    .entity("model.entity") // Entity 包名
                    .mapper("mapper") // Mapper 包名
                    .service("service") // Service 包名
                    .serviceImpl("service.impl") // ServiceImpl 包名
                    .controller("controller") // Controller 包名
                    .pathInfo(Collections.singletonMap(OutputFile.xml,
                        System.getProperty("user.dir") + "/src/main/resources/mapper")); // Mapper XML 位置
            })
            // 策略配置
            .strategyConfig(builder -> {
                builder.addInclude("music", "artist", "album", "category", "tag",
                                 "music_artist", "music_tag", "play_record", "favorite") // 表名
                    .entityBuilder()
                    .enableLombok() // 启用 Lombok
                    .enableTableFieldAnnotation() // 启用字段注解
                    .logicDeleteColumnName("is_deleted") // 逻辑删除字段
                    .controllerBuilder()
                    .enableRestStyle() // 启用 REST 风格
                    .serviceBuilder()
                    .formatServiceFileName("%sService") // Service 接口命名
                    .formatServiceImplFileName("%sServiceImpl"); // ServiceImpl 命名
            })
            // 模板引擎
            .templateEngine(new VelocityTemplateEngine())
            .execute();

        System.out.println("代码生成完成！");
    }
}
