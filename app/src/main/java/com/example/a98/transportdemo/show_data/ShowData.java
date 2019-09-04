package com.example.a98.transportdemo.show_data;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ShowData {
    public String create_time = "";
    public String title = "";
    public String desc = "";
    public String address = "";
    public String location = "";
    public String photo = "";
    public String altitude = "";
    public String angle = "";
    public String radius = "";
    public String satellite = "";
    public String id = "";
    public String distance = "";
    public String duration = "";
    public String pathline = "";
    public String stratpoint = "";
    public String endpoint = "";

    ShowData() {
        Date now = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.create_time = f.format(now);
    }

    public Map<String, String> toMap() {

        Map<String, String> map = new HashMap<String, String>();
        try {
            Field[] declaredFields = this.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                if (field.get(this) != "")
                    map.put(field.getName(), field.get(this).toString());
            }
        } catch (Exception ignored) {
        }
        return map;
    }
}
