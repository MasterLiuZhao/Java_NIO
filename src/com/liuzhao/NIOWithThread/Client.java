package com.liuzhao.NIOWithThread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException, InterruptedException {
        Charset charset = Charset.forName("utf-8");
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 8888);

        Selector selector = Selector.open();

        SocketChannel socketChannel = SocketChannel.open(inetSocketAddress);
        //socketChannel.bind(inetSocketAddress);
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);

        ClientSocketChannelHandlerThread readThread = new ClientSocketChannelHandlerThread(charset, selector);
        Thread thread = new Thread(readThread, "ClientSocketThread");
        thread.start();

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String inputString = scanner.nextLine();
            ByteBuffer byteBuffer = charset.encode(inputString);
            socketChannel.write(byteBuffer);
        }

    }

}
