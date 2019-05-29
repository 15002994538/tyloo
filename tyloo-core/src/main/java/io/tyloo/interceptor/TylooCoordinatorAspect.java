package io.tyloo.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/*
 * ��ԴЭ����������Ӧ������
 * ͨ��@Pointcut + @Around ע�⣬���ö� @Tyloo ע��ķ�����������
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 18:48 2019/4/22
 *
 */

@Aspect
public abstract class TylooCoordinatorAspect {

    private TylooCoordinatorInterceptor tylooCoordinatorInterceptor;

    @Pointcut("@annotation(io.tyloo.api.Tyloo)")
    public void transactionContextCall() {

    }

    @Around("transactionContextCall()")
    public Object interceptTransactionContextMethod(ProceedingJoinPoint pjp) throws Throwable {
        return tylooCoordinatorInterceptor.interceptTransactionContextMethod(pjp);
    }

    public void setTylooCoordinatorInterceptor(TylooCoordinatorInterceptor tylooCoordinatorInterceptor) {
        this.tylooCoordinatorInterceptor = tylooCoordinatorInterceptor;
    }

    public abstract int getOrder();
}
