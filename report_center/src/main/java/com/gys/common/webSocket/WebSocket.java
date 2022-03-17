//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.gys.common.webSocket;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ServerEndpoint("/websocket/{name}")
public class WebSocket {
    private static final Logger log = LoggerFactory.getLogger(WebSocket.class);
    private Session session;
    private String name;
    private static ConcurrentHashMap<String, WebSocket> webSocketSet = new ConcurrentHashMap();

    public WebSocket() {
    }

    @OnOpen
    public void OnOpen(Session session, @PathParam("name") String name) {
        this.session = session;
        this.name = name;
        webSocketSet.put(name, this);
        log.info("[WebSocket] 连接成功，当前连接人数为：={}", webSocketSet.size());
    }

    @OnClose
    public void OnClose() {
        webSocketSet.remove(this.name);
        log.info("[WebSocket] 退出成功，当前连接人数为：={}", webSocketSet.size());
    }

    @OnMessage
    public void OnMessage(String message) {
        log.info("[WebSocket] 收到消息：{}", message);
        if (message.indexOf("TOUSER") == 0) {
            String name = message.substring(message.indexOf("TOUSER") + 6, message.indexOf(";"));
            this.AppointSending(name, message.substring(message.indexOf(";") + 1, message.length()));
        } else {
            this.GroupSending(message);
        }

    }

    public void GroupSending(String message) {
        Iterator var2 = webSocketSet.keySet().iterator();

        while(var2.hasNext()) {
            String name = (String)var2.next();

            try {
                ((WebSocket)webSocketSet.get(name)).session.getBasicRemote().sendText(message);
            } catch (Exception var5) {
                var5.printStackTrace();
            }
        }

    }

    public void AppointSending(String name, String message) {
        try {
            ((WebSocket)webSocketSet.get(name)).session.getBasicRemote().sendText(message);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }
}
