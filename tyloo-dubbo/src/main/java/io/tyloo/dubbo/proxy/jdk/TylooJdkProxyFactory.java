package io.tyloo.dubbo.proxy.jdk;

import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.proxy.InvokerInvocationHandler;
import org.apache.dubbo.rpc.proxy.jdk.JdkProxyFactory;

import java.lang.reflect.Proxy;


/*
 *
 * TCC JDK ������
 * ���� JDK ��̬�������
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 10:52 2019/9/29
 *
 */
public class TylooJdkProxyFactory extends JdkProxyFactory {
    /**
     * - ��Ŀ����ʱ������ `TccJavassistProxyFactory#getProxy(...)` ���������� Dubbo Service ���� Proxy��
     * - ��һ�ε��� `Proxy#newProxyInstance(...)` �������������� Dubbo Service ����� Proxy��
     * - �ڶ��ε��� `Proxy#newProxyInstance(...)` �����������Ե��� Dubbo Service �� Proxy �� Proxy��
     *
     * @param invoker
     * @param interfaces
     * @param <T>
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Invoker<T> invoker, Class<?>[] interfaces) {

        T proxy = (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), interfaces, new InvokerInvocationHandler(invoker));

        T tccProxy = (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), interfaces, new TylooInvokerInvocationHandler(proxy, invoker));

        return tccProxy;
    }
}