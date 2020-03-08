package com.duyin.bookshelf.todo.web;

import com.duyin.bookshelf.todo.web.controller.SourceDebugWebSocket;

import fi.iki.elonen.NanoWSD;

public class WebSocketServer extends NanoWSD {

    public WebSocketServer(int port) {
        super(port);
    }

    @Override
    protected WebSocket openWebSocket(IHTTPSession handshake) {
        if (handshake.getUri().equals("/sourceDebug")) {
            return new SourceDebugWebSocket(handshake);
        }
        return null;
    }
}
