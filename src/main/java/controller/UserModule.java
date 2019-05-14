package controller;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import repositories.UserRepositoryModule;
import services.UserServiceModule;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class UserModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(UserController.class);
        install(new UserServiceModule());
        install(new UserRepositoryModule());
    }

    @Provides
    @Singleton
    ExecutorService getExecutor(){
        //since the time taken to do transfer by each thread is relatively low ,
        // but there can be high number of concurrent requests.
        // So it makes sense to exceed the thread pool count from default number of cpu cores
        return Executors.newFixedThreadPool(100,
                (Runnable r) -> {
                    Thread t = new Thread(r);
                    //set daemon so that executor tasks can be killed if application crashes
                    t.setDaemon(true);
                    return t;
                });
    }
}
