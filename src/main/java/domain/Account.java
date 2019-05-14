package domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Account {
    private String name;
    private AtomicInteger balance;
    private Long id;

    public Account(){

    }

    public Account(Long id,AtomicInteger balance){
        this.id = id;
        this.balance = balance;
    }
    @JsonIgnore
    public ReentrantLock getLock() {
        return reentrantLock;
    }

    @JsonIgnore
    private ReentrantLock reentrantLock = new ReentrantLock();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AtomicInteger getBalance() {
        return balance;
    }

    public void setBalance(AtomicInteger balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Account{" +
                "name='" + name + '\'' +
                ", balance='" + balance + '\'' +
                '}';
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
