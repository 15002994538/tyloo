package io.tyloo.api;

/*
 *
 * ����״̬ö��
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 12:05 2019/4/5
 *
 */
public enum TransactionStatus {

    /**
     * try�׶�
     */
    TRYING(1),

    /**
     * confirm�׶�
     */
    CONFIRMING(2),

    /**
     * cancel�׶�
     */
    CANCELLING(3);

    private int id;

     TransactionStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static TransactionStatus valueOf(int id) {

        switch (id) {
            case 1:
                return TRYING;
            case 2:
                return CONFIRMING;
            default:
                return CANCELLING;
        }
    }

}
