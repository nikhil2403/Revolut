package services;

import domain.Account;
import java.util.concurrent.atomic.AtomicInteger;

public interface UserService {

   Account getAccount(long id);

    AtomicInteger getAccountBalance(long id);
    void updateBalance(long id , AtomicInteger balance);
}
