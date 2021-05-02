package study.java1.week09.hmily.currency.entity;

import java.math.BigDecimal;
import java.util.Date;

public class AccountTrade {
    private Long id;
    private String requestId;
    private Long userId;
    private BigDecimal fromAccountFreeze;
    private String fromCurrency;
    private BigDecimal toAccountFreeze;
    private String toCurrency;
    private Integer status;

    private Date createTime;
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getFromAccountFreeze() {
        return fromAccountFreeze;
    }

    public void setFromAccountFreeze(BigDecimal fromAccountFreeze) {
        this.fromAccountFreeze = fromAccountFreeze;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public BigDecimal getToAccountFreeze() {
        return toAccountFreeze;
    }

    public void setToAccountFreeze(BigDecimal toAccountFreeze) {
        this.toAccountFreeze = toAccountFreeze;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
