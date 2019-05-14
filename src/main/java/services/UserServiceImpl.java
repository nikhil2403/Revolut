package services;

import domain.Account;
import exception.MoneyTransferException;
import repositories.UserRepository;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Inject
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public Account getAccount(long id) {
        return userRepository.getAccount(id);
    }

    @Override
    public AtomicInteger getAccountBalance(long id) {
        AtomicInteger accountBalance = userRepository.getAccountBalance(id);
        if (accountBalance ==null){
            throw  new MoneyTransferException("Account does not exist");
        }
        return accountBalance;
    }

    @Override
    public void updateBalance(long id, AtomicInteger balance) {
        userRepository.updateBalance(id,balance);

    }

    @Override
    public Collection<Account> getAll() {
        return userRepository.getAll();
    }
}
