package com.liuzhao.NIOWithThread;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Set;

public class ServerSocketChannelHandlerThread implements Runnable {
    private Charset charset;
    private SelectionKey selectedKey;
    private Set<SelectionKey> registeredSelectionKeys;

    public ServerSocketChannelHandlerThread(Charset charset, SelectionKey selectedKey, Set<SelectionKey> registeredSelectionKeys) {
        this.charset = charset;
        this.selectedKey = selectedKey;
        this.registeredSelectionKeys = registeredSelectionKeys;
    }

    @Override
    public void run() {
        System.out.println("Create a new Thread.....");

        SocketChannel socketChannel = (SocketChannel) selectedKey.channel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        String content = "";

        try {
            while (socketChannel.read(buffer) > 0) {
                buffer.flip();
                content = content + charset.decode(buffer);
            }

            System.out.println(content);

            selectedKey.interestOps(SelectionKey.OP_READ);
        } catch (IOException e) {
            selectedKey.cancel();
            if (selectedKey.channel() != null) {
                try {
                    selectedKey.channel().close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        if (content != null && content.length() > 0) {
            for (SelectionKey registeredSelectionKey : registeredSelectionKeys) {
                Channel registeredChannel = registeredSelectionKey.channel();
                if (registeredChannel instanceof SocketChannel) {

                    SocketChannel clientSocketChannel = (SocketChannel) registeredChannel;

                    try {
                        clientSocketChannel.write(charset.encode(content));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        System.out.println("Finish this thread.");
    }
}
