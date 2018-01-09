package com.ycl.app.study.remote.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**   
* User: staratsky   
* Date: 2008-8-7 21:56:47   
* 远程的接口的实现   
*/
public class HelloRMIImpl extends UnicastRemoteObject implements HelloRMI {
    private static final long serialVersionUID = -5464145481720553926L;

    /**   
     * 因为UnicastRemoteObject的构造方法抛出了RemoteException异常，因此这里默认的构造方法必须写，必须声明抛出RemoteException 
    
    异常   
     *   
     * @throws RemoteException   
     */
    public HelloRMIImpl() throws RemoteException {

    }

    /**   
     * 简单的返回“Hello World！"字样   
     *   
     * @return 返回“Hello World！"字样   
     * @throws java.rmi.RemoteException   
     */
    public String helloWorld() throws RemoteException {
        return "Hello World!";
    }

    /**   
     * @param someBodyName  
     * @return 返回问候语   
     * @throws java.rmi.RemoteException   
     */
    public String sayHelloToSomeBody(
            String someBodyName) throws RemoteException {
        return "你好，" + someBodyName + "!";
    }
}
