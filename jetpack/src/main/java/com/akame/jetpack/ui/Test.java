package com.akame.jetpack.ui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import okio.BufferedSource;
import okio.Okio;

public class Test {

    public static void main(String[] args) {
//        socketTest();
        okio();
    }

    private static void socketTest() {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            Socket socket = serverSocket.accept();
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String data;
            while ((data = reader.readLine()) != null) {
                writer.write(data);
                writer.write("\n");
                writer.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void okio() {
        //今天非常焦虑  没有找到工作 自己的面试感觉还是没有准备好
        File file = new File("./jetpack/test.txt");
        try {
            //读
            BufferedSource source = Okio.buffer(Okio.source(file));
            System.out.print("------"+source.readUtf8());
            source.close();

            //写
//            BufferedSink sink = Okio.buffer(Okio.sink(file));
//            sink.writeString("Nihao", Charset.defaultCharset());
//            sink.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
