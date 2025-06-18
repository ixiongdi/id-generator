package com.ktnb.idgenerator.service.feat.controller;

import icu.congee.id.util.IdUtil;

import tech.smartboot.feat.Feat;
import tech.smartboot.feat.cloud.annotation.Controller;
import tech.smartboot.feat.cloud.annotation.RequestMapping;

@Controller
public class IdController {
    public static void main(String[] args) {
        Feat.httpServer().listen();
    }

    @RequestMapping("/id/generate")
    public String generate() {
        return IdUtil.uuid7();
    }
}
