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
    int getMaxRetryCount();

    /**
     * ��ȡ��Ҫִ������ָ��ĳ���ʱ��.
     *
     * @return
     */
    int getRecoverDuration();

    /**
     * ��ȡ��ʱ���������ʽ.
     *
     * @return
     */
    String getCronExpression();

    /**
     * �ӳ�ȡ���쳣����
     *
     * @return
     */
    Set<Class<? extends Exception>> getDelayCancelExceptions();

    void setDelayCancelExceptions(Set<Class<? extends Exception>> delayRecoverExceptions);

    int getAsyncTerminateThreadCorePoolSize();

    int getAsyncTerminateThreadMaxPoolSize();

    int getAsyncTerminateThreadWorkQueueSize();
}
