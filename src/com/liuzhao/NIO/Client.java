package com.liuzhao.NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client {

    public static void main(String[] args) throws IOException, InterruptedException {
        Charset charset = Charset.forName("utf-8");
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 8888);

        Selector selector = Selector.open();

        SocketChannel socketChannel = SocketChannel.open(inetSocketAddress);
        //socketChannel.bind(inetSocketAddress);
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);

        ByteBuffer byteBuffer = charset.encode("大哥，我连上你了吗？");
        socketChannel.write(byteBuffer);

        Thread.sleep(8000);

        byteBuffer = charset.encode("连上了给我说声！！！！");
        socketChannel.write(byteBuffer);

        socketChannel.close();

    }

}
