package com.blockchain.riskengine.inventory.event;

import com.blockchain.riskengine.inventory.model.WithdrawEntity;
import lombok.Data;

@Data
public class WithdrawalChecked {
    public WithdrawEntity withdraw;

    public WithdrawalChecked(WithdrawEntity withdraw) {
        this.withdraw = withdraw;
    }
}
