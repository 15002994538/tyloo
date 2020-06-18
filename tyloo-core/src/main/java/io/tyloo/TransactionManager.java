package io.tyloo;

import io.tyloo.api.TransactionContext;
import io.tyloo.api.TransactionStatus;
import io.tyloo.common.TransactionType;
import org.apache.log4j.Logger;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;

/*
 *
 * ���������
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 20:25 2019/6/7
 *
 */

public class TransactionManager {

    static final Logger logger = Logger.getLogger(TransactionManager.class.getSimpleName());

    private TransactionRepository transactionRepository;

    /**
     * ��ǰ�߳��������
     */
    private static final ThreadLocal<Deque<Transaction>> CURRENT = new ThreadLocal<Deque<Transaction>>();

    private ExecutorService executorService;

    public void setTransactionRepository(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public TransactionManager() {


    }

    /**
     *
     * ���������
     * ��� begin ����֮����ʵҲ�ʹ�������һ����������ȫ������������������������ʵ����order��������
     * ���Żص� rootMethodProceed ������������ִ��
     *
     * @param uniqueIdentify
     * @return
     */
    public Transaction begin(Object uniqueIdentify) throws CloneNotSupportedException {
        Transaction transaction = new Transaction(uniqueIdentify, TransactionType.ROOT);
        transactionRepository.create(transaction);
        registerTransaction(transaction);
        return transaction;
    }


    /**
     * ���������֧����
     *
     * @param transactionContext ����������
     * @return ��֧����
     */
    public Transaction propagationNewBegin(TransactionContext transactionContext) throws CloneNotSupportedException {

        Transaction transaction = new Transaction(transactionContext);
        transactionRepository.create(transaction);

        registerTransaction(transaction);
        return transaction;
    }

    /**
     * ������ȡ��֧����
     *
     * @param transactionContext ����������
     * @return ��֧����
     * @throws NoExistedTransactionException �����񲻴���ʱ
     */
    public Transaction propagationExistBegin(TransactionContext transactionContext) throws NoExistedTransactionException {
        Transaction transaction = null;
        try {
            transaction = transactionRepository.findByXid(transactionContext.getXid());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        if (transaction != null) {
            transaction.changeStatus(TransactionStatus.valueOf(transactionContext.getStatus()));
            registerTransaction(transaction);
            return transaction;
        } else {
            throw new NoExistedTransactionException();
        }
    }

    public void commit(boolean asyncCommit) {

        final Transaction transaction = getCurrentTransaction();

        transaction.changeStatus(TransactionStatus.CONFIRMING);

        transactionRepository.update(transaction);

        if (asyncCommit) {
            try {
                Long statTime = System.currentTimeMillis();

                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        commitTransaction(transaction);
                    }
                });
                logger.debug("async submit cost time:" + (System.currentTimeMillis() - statTime));
            } catch (Throwable commitException) {
                logger.warn("tyloo transaction async submit confirm failed, recovery job will try to confirm later.", commitException);
                throw new ConfirmingException(commitException);
            }
        } else {
            commitTransaction(transaction);
        }
    }


    public void rollback(boolean asyncRollback) {

        final Transaction transaction = getCurrentTransaction();
        transaction.changeStatus(TransactionStatus.CANCELLING);

        transactionRepository.update(transaction);

        if (asyncRollback) {

            try {
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        rollbackTransaction(transaction);
                    }
                });
            } catch (Throwable rollbackException) {
                logger.warn("tyloo transaction async rollback failed, recovery job will try to rollback later.", rollbackException);
                throw new CancellingException(rollbackException);
            }
        } else {

            rollbackTransaction(transaction);
        }
    }


    private void commitTransaction(Transaction transaction) {
        try {
            transaction.commit();
            transactionRepository.delete(transaction);
        } catch (Throwable commitException) {
            logger.warn("tyloo transaction confirm failed, recovery job will try to confirm later.", commitException);
            throw new ConfirmingException(commitException);
        }
    }

    private void rollbackTransaction(Transaction transaction) {
        try {
            transaction.rollback();
            transactionRepository.delete(transaction);
        } catch (Throwable rollbackException) {
            logger.warn("tyloo transaction rollback failed, recovery job will try to rollback later.", rollbackException);
            throw new CancellingException(rollbackException);
        }
    }

    /**
     * ��ȡ��ǰ�߳������һ��(ͷ��)Ԫ��
     *
     * @return ����
     */
    public Transaction getCurrentTransaction() {
        if (isTransactionActive()) {
            return CURRENT.get().peek();
        }
        return null;
    }

    public boolean isTransactionActive() {
        Deque<Transaction> transactions = CURRENT.get();
        return transactions != null && !transactions.isEmpty();
    }

    /**
     * ע�����񵽵�ǰ�߳��������
     *
     * @param transaction ����
     */
    private void registerTransaction(Transaction transaction) {

        if (CURRENT.get() == null) {
            CURRENT.set(new LinkedList<Transaction>());
        }

        CURRENT.get().push(transaction);
    }

    /**
     * ������ӵ�ǰ�߳���������Ƴ�
     *
     * @param transaction ����
     */
    public void cleanAfterCompletion(Transaction transaction) {
        if (isTransactionActive() && transaction != null) {
            Transaction currentTransaction = getCurrentTransaction();
            if (currentTransaction == transaction) {
                CURRENT.get().pop();
                if (CURRENT.get().size() == 0) {
                    CURRENT.remove();
                }
            } else {
                throw new SystemException("Illegal transaction when clean after completion");
            }
        }
    }

    /**
     * ��Ӳ����ߵ�����
     *
     * @param participant ������
     */
    public void enlistParticipant(Participant participant) {
        Transaction transaction = this.getCurrentTransaction();
        transaction.enlistParticipant(participant);
        transactionRepository.update(transaction);
    }
}
