package com.wiqer.coordina.tm.aspect;


import com.wiqer.coordina.tm.menum.TransactionType;
import com.wiqer.coordina.tm.transactional.EFTransaction;
import com.wiqer.coordina.tm.transactional.GlobalTransactionManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;


/**
 * @author laowu
 * @date 2020/8/22 11:20
 * @desc 自定义切面
 */
@Aspect
@Component
public class GlobalTransactionAspect implements Ordered {

//    public  static ProceedingJoinPoint localPoint;
//    public  static GlobalTransactionAspect localGTA;
    public int getOrder() {
        return 10000; //需要在Spring事务前面执行，Spring事务默认是最大值哈
    }

    @Around("@annotation(com.wiqer.coordina.tm.annotation.GlobalTransaction)")
    public void invoke(ProceedingJoinPoint point) {
        // before
        String xid = GlobalTransactionManager.getOrCreateGroup();//获取XID
        // 分支事务
        EFTransaction EFTransaction = GlobalTransactionManager.createEFTransaction(xid);
        try {
            point.proceed();
 //point测试
//            String s= point.getKind();
//            String s2=  point.getTarget().toString();
//            String s3=  point.toLongString();
//            String s5=  point.toShortString();
//            String s4=  point.getStaticPart().toLongString();
//            if(localPoint!=null){
//               if(localPoint==point) {
//                   System.out.println("没毛病奥");
//               }else {
//                   System.out.println("啥玩意");
//               }
//
//            }else {
//                localPoint=point;
//            }
//            if(localGTA!=null){
//                if(localGTA==this) {
//                    System.out.println("localGTA没毛病奥");
//                }else {
//                    System.out.println("localGTA啥玩意");
//                }
//
//            }else {
//                localGTA=this;
//            }
            EFTransaction.setTransactionType(TransactionType.commit);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            EFTransaction.setTransactionType(TransactionType.rollback);
            throw new RuntimeException(throwable.getMessage());
        } finally {
            // 注册(提交/回滚)
            GlobalTransactionManager.addEFTransaction(EFTransaction);
        }
    }
}
