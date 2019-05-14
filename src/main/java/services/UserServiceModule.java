package services;

import com.google.inject.AbstractModule;
public class UserServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(UserService.class).to(UserServiceImpl.class);
        bind(MoneytransferService.class).to(MoneyTransferServiceImpl.class);
    }


}
