package controller;
import domain.Account;
import domain.TransferRequest;
import io.javalin.Context;
import services.MoneytransferService;
import services.UserService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Singleton
public class UserController {

    private MoneytransferService moneytransferService;
    private UserService  userService;

    @Inject
    public UserController( MoneytransferService moneytransferService,UserService userService) {
        this.moneytransferService = moneytransferService;
        this.userService = userService;
    }


    public void postHandle(Context ctx) {

        TransferRequest request = ctx.bodyAsClass(TransferRequest.class);

        CompletableFuture<Void> future = moneytransferService.doTransfer(request.getFrom(), request.getTo(), request.getAmount(), request.getType());
        future.exceptionally(e-> {
            //log the error. In production -> logger
            System.out.println(e);
            ctx.status(500);
            ctx.result(e.getLocalizedMessage());
            return null;
        }).join();
    }

    public void getAll(Context ctx) {
        ctx.json(userService.getAll());
    }

    public void getOne(Context ctx){
        ctx.json( userService.getAccount(Long.valueOf(ctx.req.getParameter("id"))));
    }
}
