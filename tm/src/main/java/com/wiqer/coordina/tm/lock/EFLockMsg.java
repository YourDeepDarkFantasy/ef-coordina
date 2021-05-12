package com.wiqer.coordina.tm.lock;

public class EFLockMsg {
    private String type="LK";

    public EFLockMsg(String lockId) {
        this.lockId = lockId;
    }

    //事务ID
    private String lockId;
}
