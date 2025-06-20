package icu.congee.id.generator.service.feat.controller;

import icu.congee.id.util.IdUtil;

import tech.smartboot.feat.cloud.FeatCloud;
import tech.smartboot.feat.cloud.annotation.Controller;
import tech.smartboot.feat.cloud.annotation.RequestMapping;

@Controller
public class IdController {
    public static void main(String[] args) {
        FeatCloud.cloudServer().listen();
    }

    @RequestMapping("/id/generate")
    public String generate() {
        return IdUtil.uuid7();
    }
}
