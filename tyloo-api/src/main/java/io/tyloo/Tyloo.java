package io.tyloo.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/*
 *
 * ����ע��
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 11:32 2019/4/1
 *
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Tyloo {

    /**
     * ��������
     */
    Propagation propagation() default Propagation.REQUIRED;

    /**
     * ȷ��ִ��ҵ�񷽷�
     */
    String confirmMethod() default "";

    /**
     * ȡ��ִ��ҵ�񷽷�
     */
    String cancelMethod() default "";

    /**
     * ���������ı༭
     */
    public Class<? extends TransactionContextEditor> transactionContextEditor() default DefaultTransactionContextEditor.class;

    /**
     * ��ʱ�쳣
     * delayCancelExceptions()��ʾϵͳ���������õ��쳣ʱ������������rollbac�������ɻָ�job��ִ������ָ���k
     * ͨ����Ҫ����ʱ�쳣����ΪdelayCancelExceptions���������Ա�����Ϊ�������ʱ�����˳�ʱ�쳣���������������rollback, ���Ǵ�����ûִ���꣬�Ӷ����������rollbackʧ��
     *
     * @return
     */
    public Class<? extends Exception>[] delayCancelExceptions() default {};

    public boolean asyncConfirm() default false;

    public boolean asyncCancel() default false;

    /**
     * Ĭ�����������ı༭��ʵ��
     */
    class DefaultTransactionContextEditor implements TransactionContextEditor {

        @Override
        public TransactionContext get(Object target, Method method, Object[] args) {
            int position = getTransactionContextParamPosition(method.getParameterTypes());

            if (position >= 0) {
                return (TransactionContext) args[position];
            }

            return null;
        }

        @Override
        public void set(TransactionContext transactionContext, Object target, Method method, Object[] args) {

            int position = getTransactionContextParamPosition(method.getParameterTypes());
            if (position >= 0) {
                args[position] = transactionContext;
            }
        }

        /**
         * ��������������ڷ����������λ��
         *
         * @param parameterTypes �������ͼ���
         * @return λ��
         */
        static int getTransactionContextParamPosition(Class<?>[] parameterTypes) {

            int position = -1;

            for (int i = 0; i < parameterTypes.length; i++) {
                if (parameterTypes[i].equals(TransactionContext.class)) {
                    position = i;
                    break;
                }
            }
            return position;
        }

        /**
         * @param args �����б�
         * @return ��ȡTransactionContext����
         */
        public static TransactionContext getTransactionContextFromArgs(Object[] args) {

            TransactionContext transactionContext = null;

            for (Object arg : args) {
                if (arg != null && TransactionContext.class.isAssignableFrom(arg.getClass())) {

                    transactionContext = (TransactionContext) arg;
                }
            }

            return transactionContext;
        }
    }
}