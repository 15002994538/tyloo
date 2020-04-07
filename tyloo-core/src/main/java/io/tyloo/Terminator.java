package io.tyloo;

import io.tyloo.api.TransactionContext;
import io.tyloo.api.TransactionContextEditor;
import io.tyloo.support.FactoryBuilder;
import io.tyloo.utils.StringUtils;

import java.lang.reflect.Method;

/*
 *
 * ִ����
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 20:01 2019/6/6
 *
 */

public final class Terminator {

    public Terminator() {

    }

    /**
     * ���ݵ��������ģ���ȡĿ�귽����ִ�з�������.
     *
     * @param invocationContext
     */
    public static void invoke(TransactionContext transactionContext, InvocationContext invocationContext, Class<? extends TransactionContextEditor> transactionContextEditorClass) {


        if (StringUtils.isNotEmpty(invocationContext.getMethodName())) {

            try {

                Object target = FactoryBuilder.factoryOf(invocationContext.getTargetClass()).getInstance();

                Method method = null;

                //ע������������
                method = target.getClass().getMethod(invocationContext.getMethodName(), invocationContext.getParameterTypes());

                FactoryBuilder.factoryOf(transactionContextEditorClass).getInstance().set(transactionContext, target, method, invocationContext.getArgs());

                // ���÷��񷽷������ٴα�TylooAspect��TylooCoordinatorAspect���أ�����Ϊ����״̬�Ѿ�������TRYING�ˣ�����ֱ��ִ��Զ�̷���
                method.invoke(target, invocationContext.getArgs());

            } catch (Exception e) {
                throw new SystemException(e);
            }
        }
    }
}
