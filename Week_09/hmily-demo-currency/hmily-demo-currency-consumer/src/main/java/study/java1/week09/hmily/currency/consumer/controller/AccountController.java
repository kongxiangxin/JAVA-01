package study.java1.week09.hmily.currency.consumer.controller;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import study.java1.week09.hmily.currency.model.AccountModel;
import study.java1.week09.hmily.currency.model.CurrencyExchangeRequest;
import study.java1.week09.hmily.currency.model.CurrencyType;
import study.java1.week09.hmily.currency.service.AccountService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

//import study.java1.week09.hmily.currency.service.AccountTradeService;

@RequestMapping("/account")
@RestController
public class AccountController {

    @DubboReference(version = "1.0.0")
    private AccountService accountService;

//    @DubboReference(version = "1.0.0")
//    private AccountTradeService tradeService;

    @RequestMapping("/list")
    public List<AccountModel> list(){

        return accountService.selectAccounts();
    }
    @RequestMapping("/trade")
    public void performTrade(){

//        List<AccountTradeModel> trades = new ArrayList<>();

        CurrencyExchangeRequest request = new CurrencyExchangeRequest();
        request.setUserId(10L);
        request.setFromCurrency(CurrencyType.USD);
        request.setToCurrency(CurrencyType.CNY);
        request.setAmount(BigDecimal.valueOf(1));


        CurrencyExchangeRequest request2 = new CurrencyExchangeRequest();
        request2.setUserId(11L);
        request2.setFromCurrency(CurrencyType.CNY);
        request2.setToCurrency(CurrencyType.USD);
        request2.setAmount(BigDecimal.valueOf(700));

//        AccountTradeModel trade = tradeService.createTrade(request);
//        trades.add(trade);
//
//        request = new CurrencyExchangeRequest();
//        request.setUserId(11L);
//        request.setFromCurrency(CurrencyType.CNY);
//        request.setToCurrency(CurrencyType.USD);
//        request.setAmount(BigDecimal.valueOf(7));
//        trade = tradeService.createTrade(request);
//        trades.add(trade);

//        tradeService.performTrade(trades);

        accountService.batchExchange(Arrays.asList(request, request2));
    }
}
