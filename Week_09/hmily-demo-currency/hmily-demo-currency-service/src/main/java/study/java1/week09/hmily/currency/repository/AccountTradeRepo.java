package study.java1.week09.hmily.currency.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import study.java1.week09.hmily.currency.entity.Account;
import study.java1.week09.hmily.currency.entity.AccountTrade;

import java.util.List;

@Repository
public interface AccountTradeRepo {

    @Insert("insert account_trade(request_id,user_id,from_account_freeze,from_currency,to_account_freeze,to_currency, status) " +
            "values( #{requestId}, #{userId},#{fromAccountFreeze}, #{fromCurrency}, #{toAccountFreeze}, #{toCurrency}, #{status})")
    int insert(AccountTrade trade);

    @Update("update account_trade set status = #{status} where user_id = #{userId} and request_id = #{requestId}")
    int updateStatus(@Param("userId") Long userId, @Param("requestId") String requestId, int status);

    @Select("select * from account_trade where user_id = #{userId} and request_id = #{requestId}")
    AccountTrade selectByRequestId(@Param("userId") Long userId, @Param("requestId") String requestId);
}
