package com.liuzhao.NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Server {

    public static void main(String[] args) throws IOException {
        Charset charset = Charset.forName("utf-8");
        InetSocketAddress inetSocketAddress = new InetSocketAddress(8888);

        Selector selector = Selector.open();

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(inetSocketAddress);
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (selector.select() > 0) {

            //System.out.println("Execute selector.select()");

            for (SelectionKey selectionKey : selector.selectedKeys()) {

                //System.out.println("Execute SelectionKey selectionKey : selector.selectedKeys()");
                //System.out.println(selectionKey.toString());

                selector.selectedKeys().remove(selectionKey);

                if (selectionKey.isAcceptable()) {
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);

                    selectionKey.interestOps(SelectionKey.OP_ACCEPT);
                }

                if (selectionKey.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    String content = "";

                    try {
                        while (socketChannel.read(buffer) > 0) {

                            System.out.println("Waiting for socketChannel.read(buffer)");

                            buffer.flip();
                            content = content + charset.decode(buffer);

                            System.out.println(content);
                        }

                        //selectionKey.interestOps(SelectionKey.OP_READ);
                    } catch (IOException e) {
                        selectionKey.cancel();
                        if (selectionKey.channel() != null) {
                            selectionKey.channel().close();
                        }
                    }

                    // TODO write method.
                }
            }
        }

    }

}
