package services;

import domain.OperationType;

import java.util.concurrent.CompletableFuture;

public interface MoneytransferService {
    CompletableFuture<Void> doTransfer(Long from, Long to, Integer amount, OperationType type) ;
}
