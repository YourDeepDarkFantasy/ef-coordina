package com.wiqer.coordina.tm.transactional;


import com.wiqer.coordina.tm.menum.TransactionType;
import com.wiqer.coordina.tm.util.Task;

/**
 * @desc 分支事务
 */
public class EFTransaction {

    public void setTask(Task task) {
        this.task = task;
    }

    private String type="TS";
    //事务ID
    private String transactionId;
    //事务类型
    private TransactionType transactionType;
    //事务组
    private String xid;
    //
    private Task task;
    public EFTransaction(String xid, String transactionId) {
        this.transactionId = transactionId;
        this.xid = xid;
        this.task = new Task();
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }

    public Task getTask() {
        return task;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
