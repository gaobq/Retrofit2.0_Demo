package com.heybik.gaobiaoqing.retrofit20demo.model;

/**
 * Created by gaobiaoqing on 16-2-25.
 */
public class JsonData {

    public int id;
    public String name;

    public String addr;

    public JsonData(String addr, int id, String name) {
        this.addr = addr;
        this.id = id;
        this.name = name;
    }
}
