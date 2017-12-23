package com.liuzhao.NIOWithThread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static void main(String[] args) throws IOException {
        Charset charset = Charset.forName("utf-8");
        ExecutorService threadPool = Executors.newFixedThreadPool(100);
        InetSocketAddress inetSocketAddress = new InetSocketAddress(8888);

        Selector selector = Selector.open();

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(inetSocketAddress);
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (selector.select() > 0) {

            //System.out.println("Execute selector.select()");

            for (SelectionKey selectedKey : selector.selectedKeys()) {

                // Selector 不会删除上次处理过的被选择的SelectionKey，如果我们不自己删除，下次 Selector.select() 的时候还会继续处理上次留下的SelectionKey。
                selector.selectedKeys().remove(selectedKey);

                if (selectedKey.isAcceptable()) {
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);

                    selectedKey.interestOps(SelectionKey.OP_ACCEPT);
                }

                if (selectedKey.isReadable()) {
                    Set<SelectionKey> registeredSelectionKeys = selector.keys();

                    ServerSocketChannelHandlerThread readAndWriteThread = new ServerSocketChannelHandlerThread(charset, selectedKey, registeredSelectionKeys);
                    threadPool.submit(readAndWriteThread);
                }
            }
        }

    }

}
