package study.java1.week09.hmily.currency.service;

public enum TradeStatus {
    TRYING(1),

    TRIED(2),

    CONFIRMED(3),

    CANCELED(4);

    private final int code;

    TradeStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
