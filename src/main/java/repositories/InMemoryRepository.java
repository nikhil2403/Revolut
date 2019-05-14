package repositories;

import domain.Account;

import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class InMemoryRepository implements UserRepository  {

    private Map<Long, Account> users ;

   public InMemoryRepository(){
        users = new ConcurrentHashMap<>();
        users.put(1l, new Account(1l,new AtomicInteger(100)));
        users.put(2l, new Account(2l,new AtomicInteger(100)));
        users.put(3l, new Account(3l,new AtomicInteger(100)));
        users.put(4l, new Account(4l,new AtomicInteger(100)));
    }

    public InMemoryRepository(Map<Long, Account> users){
       this.users = users;
    }

    @Override
    public AtomicInteger getAccountBalance(long id) {
        return  users.get(id).getBalance();
    }

    @Override
    public void updateBalance(long id, AtomicInteger balance) {
       users.computeIfPresent(id,(idx,account)->{
           account.setBalance(balance);
           return account;
       });
    }

    @Override
    public Account getAccount(long id) {
        return users.get(id);
    }
}
