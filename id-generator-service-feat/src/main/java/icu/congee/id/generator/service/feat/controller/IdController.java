package icu.congee.id.generator.service.feat.controller;

import icu.congee.id.util.IdUtil;
import tech.smartboot.feat.cloud.annotation.Controller;
import tech.smartboot.feat.cloud.annotation.RequestMapping;

@Controller
public class IdController {
    @RequestMapping("/id/generate")
    public String generate() {
        return IdUtil.uuid7();
    }
}
