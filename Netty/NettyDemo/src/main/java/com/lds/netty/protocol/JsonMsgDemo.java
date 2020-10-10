package com.lds.netty.protocol;

import org.junit.Test;

public class JsonMsgDemo {

    //构建Json对象
    public JsonMsg build(){
        JsonMsg user = new JsonMsg();
        user.setId(1000);
        user.setContent("hello我是大厦子");
        return user;
    }

    //序列化 serialization & 反序列化 Deserialization
    @Test
    public void test(){
        JsonMsg message = build();
        String json = message.convertToJson();
        System.out.println("json:"+json);
        JsonMsg msg = JsonMsg.parseFromJson(json);
        System.out.println(msg);
    }
}
