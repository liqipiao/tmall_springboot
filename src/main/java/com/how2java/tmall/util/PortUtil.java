package com.how2java.tmall.util;

import javax.swing.*;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;

/**
 * Redis启动工具类
 * 用于判断某个端口是否启动。
 */
public class PortUtil {

    public static boolean testPort(int port){
        try {
            ServerSocket serverSocket=new ServerSocket(port);
            serverSocket.close();
            return false;
        }catch (BindException e){
            return true;
        } catch (IOException e){
            return true;
        }
    }
    public static void chekPort(int port,String server,boolean shutdown){
        if (!testPort(port)){
            String message=String.format("在端口 %d 未检查得到 %s 启动%n",port,server);
            JOptionPane.showMessageDialog(null,message);
            System.exit(1);
        }else {
            String message =String.format("在端口 %d 未检查得到 %s 启动%n,是否继续?",port,server);
            if(JOptionPane.OK_OPTION !=JOptionPane.showConfirmDialog(null, message)) {
                System.exit(1);
            }
        }
    }
}
