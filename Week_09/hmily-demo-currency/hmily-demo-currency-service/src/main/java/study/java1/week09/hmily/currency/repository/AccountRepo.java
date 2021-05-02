package study.java1.week09.hmily.currency.repository;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import study.java1.week09.hmily.currency.entity.Account;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AccountRepo {

    @Select("select * from account")
    List<Account> selectList();

    @Select("select * from account where user_id = #{userId}")
    List<Account> selectByUserId(@Param("userId") Long userId);

    @Update("update account set balance = balance - #{amount} " +
            "where user_id = #{userId} and currency = #{currency} and balance >= #{amount}")
    int decreaseBalance(Long userId, String currency, BigDecimal amount);

    @Update("update account set balance = balance + #{amount} " +
            "where user_id = #{userId} and currency = #{currency}")
    int increaseBalance(Long userId, String currency, BigDecimal amount);

    @Update("update account set balance = balance + #{amount} " +
            "where user_id = #{userId} and currency = #{currency}")
    int cancelDecrease(Long userId, String currency, BigDecimal amount);

    @Update("update account set balance = balance - #{amount} " +
            "where user_id = #{userId} and currency = #{currency}")
    int cancelIncrease(Long userId, String currency, BigDecimal amount);
}
