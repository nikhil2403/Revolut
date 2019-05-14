package repositories;

import domain.Account;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public interface UserRepository {
    AtomicInteger getAccountBalance(long id);
    void updateBalance(long id , AtomicInteger balance);
    Account getAccount(long id);
}
