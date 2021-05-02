package study.java1.week09.hmily.currency.service;

import org.dromara.hmily.annotation.Hmily;
import study.java1.week09.hmily.currency.model.AccountModel;
import study.java1.week09.hmily.currency.model.CurrencyExchangeRequest;

import java.util.List;

public interface AccountService {

    /**
     * 查询所有账户
     * @return
     */
    List<AccountModel> selectAccounts();

    /**
     * 批量兑换，要么都成功，要么都失败
     * @param requests
     */
    @Hmily
    void batchExchange(List<CurrencyExchangeRequest> requests);

    void exchange(CurrencyExchangeRequest request);

}
