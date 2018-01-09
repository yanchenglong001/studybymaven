package com.ycl.app.study.remote.rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**   
* User: staratsky 
* Date: 2008-8-7 22:21:07   
* 客户端测试，在客户端调用远程对象上的远程方法，并返回结果。   
*/
public class HelloRMIClient {
    public static void main(String args[]) {
        try {
            //在RMI服务注册表中查找名称为RHello的对象，并调用其上的方法     
            HelloRMI rhello = (HelloRMI) Naming
                    .lookup("rmi://localhost:8888/RHello");
            System.out.println(rhello.helloWorld());
            System.out.println(rhello.sayHelloToSomeBody("staratsky"));
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
