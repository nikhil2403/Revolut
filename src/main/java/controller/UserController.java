package controller;
import domain.TransferRequest;
import io.javalin.Context;
import services.MoneytransferService;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserController {

    private MoneytransferService moneytransferService;

    @Inject
    public UserController( MoneytransferService moneytransferService) {
        this.moneytransferService = moneytransferService;
    }


    public void postHandle(Context ctx) {

        TransferRequest request = ctx.bodyAsClass(TransferRequest.class);

        moneytransferService.doTransfer(request.getFrom(),request.getTo(),request.getAmount(),request.getType());
    }

}
