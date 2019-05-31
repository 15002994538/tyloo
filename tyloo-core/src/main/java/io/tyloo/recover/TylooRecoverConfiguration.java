package io.tyloo.recover;

import java.util.Set;

/*
 * ����ָ����ýӿ�.
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 18:50 2019/5/1
 *
 */

public interface TylooRecoverConfiguration {

    /**
     * ��ȡ������Դ���
     *
     * @return
     */
    public int getMaxRetryCount();

    /**
     * ��ȡ��Ҫִ������ָ��ĳ���ʱ��.
     *
     * @return
     */
    public int getRecoverDuration();

    /**
     * ��ȡ��ʱ���������ʽ.
     *
     * @return
     */
    public String getCronExpression();

    /**
     * �ӳ�ȡ���쳣����
     *
     * @return
     */
    public Set<Class<? extends Exception>> getDelayCancelExceptions();

    public void setDelayCancelExceptions(Set<Class<? extends Exception>> delayRecoverExceptions);

    public int getAsyncTerminateThreadCorePoolSize();

    public int getAsyncTerminateThreadMaxPoolSize();

    public int getAsyncTerminateThreadWorkQueueSize();
}
