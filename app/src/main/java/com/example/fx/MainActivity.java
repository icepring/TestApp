package com.example.fx;

import android.graphics.Color;
import android.os.Bundle;

import com.example.fx.button.CFDSpeedButton;
import com.example.fx.button.CFDSpeedShadowButton;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 主な活動
 *
 * @author 小天
 * @date 2022/05/15
 */
public class MainActivity extends AppCompatActivity {

    /**
     * 作成時
     *
     * @param savedInstanceState 保存されたインスタンスの状態
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CFDSpeedButton sellSpeedButton = findViewById(R.id.speed_sell);
        CFDSpeedButton buySpeedButton = findViewById(R.id.speed_buy);

        CFDSpeedShadowButton cfdSpeedShadowButton = findViewById(R.id.speed_sell1);
        sellSpeedButton.setRate(28.4d);
        buySpeedButton.setRate(123.458886d);

        CFDSpeedShadowButton.Builder builder = new CFDSpeedShadowButton.Builder()
                .buildPrice(28.4d)
                .buildBgColor(Color.YELLOW)
                .buildBuySellMarkBGColor(Color.BLUE)
                .buildBuySellMarkTextColor(Color.WHITE)
                .buildIndicator(CFDSpeedShadowButton.TickIndicator.UP)
                .buildOrderLocked(false)
                .buildPriceColor(Color.RED)
                .buildShadowColor(Color.GREEN)
                .buildShadowRadios(20)
                .buildShowShadow(true);

        final boolean[] flag = {false};
        cfdSpeedShadowButton.setOnClickListener((CFDSpeedShadowButton.CFDSpeedButtonClickListener)
                isBuy -> {
                    builder.buildShowShadow(flag[0])
                            .buildPrice(27.4d)
                            .updateData(cfdSpeedShadowButton);
                    flag[0] = !flag[0];
                }


        );

        builder.updateData(cfdSpeedShadowButton);
    }
}