import domain.Account;
import domain.OperationType;
import domain.TransferRequest;
import javafx.util.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import repositories.InMemoryRepository;
import repositories.UserRepository;
import services.MoneyTransferServiceImpl;
import services.MoneytransferService;
import services.UserService;
import services.UserServiceImpl;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class MoneyTransferTest {

    private UserRepository userRepository;
    private UserService userService;
    private MoneytransferService moneytransferService;
    private ExecutorService executor;

    @Before
   public void init(){
         Map<Long, Account> users ;
        users = new ConcurrentHashMap<>();
        users.put(1l, new Account(1l,new AtomicInteger(100)));
        users.put(2l, new Account(2l,new AtomicInteger(100)));
        users.put(3l, new Account(3l,new AtomicInteger(100)));
        users.put(4l, new Account(4l,new AtomicInteger(100)));
        users.put(5l, new Account(5l,new AtomicInteger(100)));
        userRepository = new InMemoryRepository(users);
        userService = new UserServiceImpl(userRepository);
        executor = Executors.newFixedThreadPool(100,
                (Runnable r) -> {
                    Thread t = new Thread(r);
                    t.setDaemon(true);
                    return t;
                });
        moneytransferService = new MoneyTransferServiceImpl(userService,executor);

    }


    @Test
    public void testMoneyTransferSingleRunningAdd(){


        CompletableFuture<Void>  future =   moneytransferService.doTransfer(1l,2l,90,OperationType.SEND);
        future.join();
        assertEquals(10,userRepository.getAccountBalance(1l).get());
        assertEquals(190,userRepository.getAccountBalance(2l).get());
    }


    @Test
    public void testMoneyTransferParallelSend(){


        CompletableFuture<Void>  send =    moneytransferService.doTransfer(1l,2l,90,OperationType.SEND);
        CompletableFuture<Void>  receive =   moneytransferService.doTransfer(2l,1l,80,OperationType.SEND);
        Stream.of(send,receive).parallel().map(CompletableFuture::join).collect(Collectors.toList());

        assertEquals(90,userRepository.getAccountBalance(1l).get());
        assertEquals(110,userRepository.getAccountBalance(2l).get());

    }

    @Test
    public void testMoneyTransferParallelSendAndReceive(){

        CompletableFuture<Void>  send =     moneytransferService.doTransfer(1l,2l,90,OperationType.SEND);
        CompletableFuture<Void>  receive =   moneytransferService.doTransfer(1l,2l,80,OperationType.RECEIVE);
        Stream.of(send,receive).parallel().map(CompletableFuture::join).collect(Collectors.toList());

        assertEquals(90,userRepository.getAccountBalance(1l).get());
        assertEquals(110,userRepository.getAccountBalance(2l).get());

    }



    @Test
    public void testMoneyTransferConcurrentSend(){

        Stream.of(1,2,3,4,5).parallel()
                .map(i-> moneytransferService.doTransfer(1l,2l,1,OperationType.SEND))
                .map(CompletableFuture::join).collect(Collectors.toList());

        assertEquals(95,userRepository.getAccountBalance(1l).get());
        assertEquals(105,userRepository.getAccountBalance(2l).get());

    }


    @Test
    public void testMoneyTransferConcurrentSendAndReceive(){

        Stream.of(1,2,3,4,5).parallel()
                .map(i-> moneytransferService.doTransfer(1l,2l,1,OperationType.SEND))
                .map(CompletableFuture::join).collect(Collectors.toList());
        Stream.of(1,2,3,4,5).parallel()
                .map(i-> moneytransferService.doTransfer(1l,2l,2,OperationType.RECEIVE))
                .map(CompletableFuture::join).collect(Collectors.toList());

        assertEquals(105,userRepository.getAccountBalance(1l).get());
        assertEquals(95,userRepository.getAccountBalance(2l).get());

    }


    @Test
    public void testMoneyTransferConcurrentSendMultipleAccounts(){

        List<Pair<Long,Long>> accountsGraph = new ArrayList<>();
        accountsGraph.add(new Pair<>(1l,2l));
        accountsGraph.add(new Pair<>(1l,3l));
        accountsGraph.add(new Pair<>(4l,2l));
        accountsGraph.add(new Pair<>(5l,1l));
        accountsGraph.add(new Pair<>(2l,3l));
        accountsGraph.add(new Pair<>(4l,2l));


        Stream.of(0,1,2,3,4,5).parallel()
                .map(i-> moneytransferService.doTransfer(accountsGraph.get(i).getKey(),accountsGraph.get(i).getValue(),10,OperationType.SEND))
                .map(CompletableFuture::join).collect(Collectors.toList());

        assertEquals(90,userRepository.getAccountBalance(1l).get());
        assertEquals(120,userRepository.getAccountBalance(2l).get());
        assertEquals(120,userRepository.getAccountBalance(3l).get());
        assertEquals(80,userRepository.getAccountBalance(4l).get());
        assertEquals(90,userRepository.getAccountBalance(5l).get());


    }



    @After
    public void stopExecutor(){
        executor.shutdown();
    }
}
