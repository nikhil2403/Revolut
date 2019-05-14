package repositories;

import domain.Account;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public interface UserRepository {
    AtomicInteger getAccountBalance(long id);
    void updateBalance(long id , AtomicInteger balance);
    Account getAccount(long id);

    Collection<Account> getAll();
}
