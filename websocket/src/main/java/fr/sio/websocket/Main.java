package fr.sio.websocket;


public class Main {

    public static void main(String[] args) {
        new WebsocketServer(8080,"localhost").start();
    }
}
