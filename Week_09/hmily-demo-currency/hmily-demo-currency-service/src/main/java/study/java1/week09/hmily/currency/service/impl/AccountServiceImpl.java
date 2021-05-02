package study.java1.week09.hmily.currency.service.impl;

import ma.glasnost.orika.MapperFacade;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.hmily.annotation.HmilyTCC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import study.java1.week09.hmily.currency.entity.Account;
import study.java1.week09.hmily.currency.entity.AccountTrade;
import study.java1.week09.hmily.currency.model.AccountModel;
import study.java1.week09.hmily.currency.model.CurrencyExchangeRequest;
import study.java1.week09.hmily.currency.repository.AccountRepo;
import study.java1.week09.hmily.currency.service.AccountService;
import study.java1.week09.hmily.currency.service.AccountTradeService;
import study.java1.week09.hmily.currency.service.TradeStatus;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;

@DubboService(version = "1.0.0")
public class AccountServiceImpl implements AccountService {
    private Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private MapperFacade beanMapper;
    @Autowired
    private AccountTradeService tradeService;

    @Override
    public List<AccountModel> selectAccounts() {
        List<Account> accounts = accountRepo.selectList();
        return beanMapper.mapAsList(accounts, AccountModel.class);
    }

    @Override
    @HmilyTCC(confirmMethod = "confirm", cancelMethod = "cancel")
    public void batchExchange(List<CurrencyExchangeRequest> requests) {
        logger.info("try batch exchange...");
        AccountService self = (AccountService) AopContext.currentProxy();
        try{
            for (CurrencyExchangeRequest request : requests) {
                self.exchange(request);
            }
            logger.info("try batch exchange... done");
        }catch (Exception e){
            logger.error("try batch exchange error", e);
            throw e;
        }
    }

    @Transactional
    @Override
    public void exchange(CurrencyExchangeRequest request) {
        AccountTrade trade = tradeService.createTrade(request);

        //目标账户增加
        accountRepo.increaseBalance(request.getUserId(), request.getToCurrency(), trade.getToAccountFreeze());

        //来源账户减少
        int ret = accountRepo.decreaseBalance(request.getUserId(), request.getFromCurrency(), request.getAmount());
        if(ret == 0){
            throw new RuntimeException(MessageFormat.format("账户余额不足，userId:{0}, currency:{1},amount:{2}",
                    request.getUserId(),request.getFromCurrency(), request.getAmount()));
        }
        tradeService.updateStatus(request.getUserId(), request.getRequestId(), TradeStatus.TRIED.getCode());
    }

    @Transactional
    public void confirm(List<CurrencyExchangeRequest> requests) {
        logger.error("confirm request");
        for (CurrencyExchangeRequest request : requests) {
            tradeService.updateStatus(request.getUserId(), request.getRequestId(), TradeStatus.CONFIRMED.getCode());
        }
    }

    @Transactional
    public void cancel(List<CurrencyExchangeRequest> requests) {
        logger.error("cancel request");
        for (CurrencyExchangeRequest request : requests) {
            AccountTrade trade = tradeService.selectByRequestId(request.getUserId(), request.getRequestId());
            if(trade != null && TradeStatus.TRIED.getCode() == trade.getStatus()){
                accountRepo.cancelIncrease(trade.getUserId(), trade.getToCurrency(), trade.getToAccountFreeze());
                accountRepo.cancelDecrease(trade.getUserId(), trade.getFromCurrency(), trade.getFromAccountFreeze());
                tradeService.updateStatus(trade.getUserId(), trade.getRequestId(), TradeStatus.CANCELED.getCode());
            }
        }
    }
}
