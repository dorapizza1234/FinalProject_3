package com.spring.app.admin.domain;

import lombok.Data;
import java.util.Date;

@Data
public class TransactionAdminDTO {
    private long transactionId;
    private long productNo;
    private String productName;
    private long productPrice;
    private String buyerNickname;
    private String buyerEmail;
    private String sellerNickname;
    private String sellerEmail;
    private long amount;
    private String tradeStatus;
    private Date tradeDate;
    private double hoursElapsed;

    public long getHoursLeft() {
        long elapsed = (long) hoursElapsed;
        return Math.max(0, 72 - elapsed);
    }
}
