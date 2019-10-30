package com.hpu.dao;

import com.hpu.bean.Sku;

import java.util.List;

public interface SkuDao {
    Sku findById(String id);
    List<Sku> findAll();
}
