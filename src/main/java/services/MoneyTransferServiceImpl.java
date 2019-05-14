package services;

import domain.Account;
import domain.OperationType;
import exception.MoneyTransferException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;

@Singleton
public class MoneyTransferServiceImpl implements MoneytransferService {

    private UserService userService;
    private ExecutorService executor;

    @Inject
    public MoneyTransferServiceImpl(UserService userService, ExecutorService executor){
        this.userService = userService;
        this.executor = executor;
    }

    @Override
    public CompletableFuture<Void> doTransfer(Long from, Long to, Integer amount, OperationType type) {
        Runnable r = ()->{} ;

        if(type!=null ){
            if(type.equals(OperationType.SEND)){
             r = ()->  doTransfer(from,to,amount);
            }
            else if(type.equals(OperationType.RECEIVE)){
                r= ()->  doTransfer(to,from,amount);
            }
            else
                throw new MoneyTransferException("INVALID OPERATION TYPE");
        }
            //submit incoming request to custom threadpool executor
        return CompletableFuture.runAsync(r,executor).exceptionally(e-> {
            //log the error. In production -> logger
             System.out.println(e);
             return null;
        });

    }

    private void doTransfer(Long fromId, Long toId, Integer amount)  {

      Account from =  userService.getAccount(fromId);
      Account to   =  userService.getAccount(toId);

        ReentrantLock lock = from.getReentrantLock();

        while(true) {
            try {
                //induce artificial delay to simulate race condition
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.lock();

            boolean successful = false;
            try {
                validateAccounts(from, to);
                from.setBalance(userService.getAccountBalance(from.getId()));
                to.setBalance(userService.getAccountBalance(to.getId()));
                if(!isSufficientBalance(from, amount)){
                    break;
                }
                ReentrantLock toLock = to.getReentrantLock();
                boolean isLocked = toLock.tryLock();
                if(!isLocked) {
                    continue;
                }
                try {

                    to.setBalance(userService.getAccountBalance(to.getId()));
                    from.getBalance().addAndGet(-amount);
                    to.getBalance().addAndGet(amount);
                    userService.updateBalance(from.getId(),from.getBalance());
                    userService.updateBalance(to.getId(),to.getBalance());
                    successful = true;
                } finally {
                    toLock.unlock();
                }
            }  finally {
                lock.unlock();
                if (successful){
                    break;
                }
            }
        }
    }

    private void validateAccounts(Account from, Account to) {
        if (from==null || to==null)
            throw  new MoneyTransferException("Either Account does not exist");
    }

    private boolean isSufficientBalance(Account from, Integer amount) {
        if (from.getBalance().get()<amount){
            return false;
        }
        return true;
    }

}

