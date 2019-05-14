package exception;

public class MoneyTransferException extends RuntimeException {
   public MoneyTransferException(String message){
        super(message);
    }

    public MoneyTransferException(){

    }

}
