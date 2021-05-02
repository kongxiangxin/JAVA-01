package study.java1.week09.hmily.currency.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class AccountModel implements Serializable {
    private Long userId;
    private BigDecimal balance;
    private String currency;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

}
