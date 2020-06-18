package io.tyloo.dubbo.proxy.javassist;

import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.proxy.InvokerInvocationHandler;
import org.apache.dubbo.rpc.proxy.javassist.JavassistProxyFactory;

/*
 *
 *
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 9:33 2019/8/16
 *
 */
public class TylooJavassistProxyFactory extends JavassistProxyFactory {

    /**
     * ��Ŀ����ʱ������getProxy(...)` ���������� Dubbo Service ���� Proxy��
     * com.alibaba.dubbo.rpc.proxy.InvokerInvocationHandler`��Dubbo ���ô�����
     *
     * @param invoker
     * @param interfaces
     * @param <T>
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Invoker<T> invoker, Class<?>[] interfaces) {
        return (T) TylooProxy.getProxy(interfaces).newInstance(new InvokerInvocationHandler(invoker));
    }
}
