package study.java1.week09.hmily.currency.service.impl;

import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import study.java1.week09.hmily.currency.entity.AccountTrade;
import study.java1.week09.hmily.currency.model.CurrencyExchangeRequest;
import study.java1.week09.hmily.currency.model.CurrencyType;
import study.java1.week09.hmily.currency.repository.AccountTradeRepo;
import study.java1.week09.hmily.currency.service.AccountTradeService;
import study.java1.week09.hmily.currency.service.TradeStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class AccountTradeServiceImpl implements AccountTradeService {
    @Autowired
    private MapperFacade beanMapper;

    @Autowired
    private AccountTradeRepo tradeRepo;

    @Override
    public AccountTrade createTrade(CurrencyExchangeRequest request) {
        BigDecimal targetAmount = calcTargetAmount(request);

        AccountTrade trade = beanMapper.map(request, AccountTrade.class);
        trade.setToAccountFreeze(targetAmount);
        trade.setFromAccountFreeze(request.getAmount());
        trade.setStatus(TradeStatus.TRYING.getCode());
        tradeRepo.insert(trade);
        return trade;
    }

    @Override
    public int updateStatus(Long userId, String requestId, int status) {
        return tradeRepo.updateStatus(userId, requestId, status);
    }

    @Override
    public AccountTrade selectByRequestId(Long userId, String requestId) {
        return tradeRepo.selectByRequestId(userId, requestId);
    }

    private BigDecimal calcTargetAmount(CurrencyExchangeRequest request){
        if(CurrencyType.CNY.equals(request.getFromCurrency()) && CurrencyType.USD.equals(request.getToCurrency())){
            return request.getAmount().divide(new BigDecimal(7), 2, RoundingMode.DOWN);
        }
        if(CurrencyType.USD.equals(request.getFromCurrency()) && CurrencyType.CNY.equals(request.getToCurrency())){
            return request.getAmount().multiply(new BigDecimal(7));
        }
        return request.getAmount();
    }

}
