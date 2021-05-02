package study.java1.week09.hmily.currency.service;

import org.apache.ibatis.annotations.Param;
import study.java1.week09.hmily.currency.entity.AccountTrade;
import study.java1.week09.hmily.currency.model.CurrencyExchangeRequest;

public interface AccountTradeService {

    AccountTrade createTrade(CurrencyExchangeRequest request);

    int updateStatus(Long userId, String requestId, int status);

    AccountTrade selectByRequestId(@Param("userId") Long userId, @Param("requestId") String requestId);
}
