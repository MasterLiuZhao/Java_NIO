package com.liuzhao.NIOWithThread;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class ClientSocketChannelHandlerThread implements Runnable {
    private Charset charset;
    private Selector selector;

    public ClientSocketChannelHandlerThread(Charset charset, Selector selector) {
        this.charset = charset;
        this.selector = selector;
    }

    @Override
    public void run() {
        try {
            while (this.selector.select() > 0) {
                for (SelectionKey selectedKey : selector.selectedKeys()) {

                    // Selector 不会删除上次处理过的被选择的SelectionKey，如果我们不自己删除，下次 Selector.select() 的时候还会继续处理上次留下的SelectionKey。
                    selector.selectedKeys().remove(selectedKey);

                    if (selectedKey.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) selectedKey.channel();

                        String content = "";
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        while (socketChannel.read(buffer) > 0) {
                            buffer.flip();
                            content = content + charset.decode(buffer);
                        }

                        selectedKey.interestOps(SelectionKey.OP_READ);

                        System.out.println(content);
                    }
                }
            }
            System.out.println("Finish this thread....");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
