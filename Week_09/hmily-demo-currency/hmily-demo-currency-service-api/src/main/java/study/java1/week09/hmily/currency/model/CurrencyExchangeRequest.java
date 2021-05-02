package study.java1.week09.hmily.currency.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * 货币兑换请求
 */
public class CurrencyExchangeRequest implements Serializable {
    private String requestId = UUID.randomUUID().toString();
    private Long userId;
    private BigDecimal amount;
    private String fromCurrency;
    private String toCurrency;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
