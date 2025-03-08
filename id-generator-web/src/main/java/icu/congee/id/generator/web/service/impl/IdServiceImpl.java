package icu.congee.id.generator.web.service.impl;

import com.mybatisflex.core.row.DbChain;

import icu.congee.id.generator.web.service.IdService;

import org.springframework.stereotype.Service;

@Service
public class IdServiceImpl implements IdService {

    @Override
    public void insert(String table, Object id) {
        DbChain.table(table).set("id", id).save();
    }
}
