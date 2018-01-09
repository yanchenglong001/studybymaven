package com.ycl.app.study.remote.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface HelloRMI extends Remote {
    /**  
     * 简单的返回“Hello World！"字样  
     * @return 返回“Hello World！"字样  
     * @throws java.rmi.RemoteException  
     */
    public String helloWorld() throws RemoteException;

    /**  
     * @param someBodyName   
     * @return 返回相应的问候语  
     * @throws java.rmi.RemoteException  
     */
    public String sayHelloToSomeBody(
            String someBodyName) throws RemoteException;
}
