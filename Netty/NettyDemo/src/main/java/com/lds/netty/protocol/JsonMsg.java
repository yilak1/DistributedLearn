package com.lds.netty.protocol;


import com.lds.util.JsonUtil;

public class JsonMsg {

    private int id;
    private String content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "JsonMsg{" +
                "id=" + id +
                ", content='" + content + '\'' +
                '}';
    }

    //fastjson json->Object
    public static JsonMsg parseFromJson(String json){
        return JsonUtil.jsonToPojo(json, JsonMsg.class);
    }
    //gson Object -> json
    public String convertToJson(){
        return JsonUtil.pojoToJson(this);
    }
}
