package com.ktnb.idgenerator.service.solon.controller;

import icu.congee.id.util.IdUtil;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

@Controller
public class IdGeneratorController {

    @Mapping("/id/generate")
    public String generateId() {
        return IdUtil.uuid7();
    }
}
