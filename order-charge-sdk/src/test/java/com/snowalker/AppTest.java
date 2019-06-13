package com.snowalker;

import static org.junit.Assert.assertTrue;

import com.alibaba.fastjson.JSON;
import com.snowalker.order.charge.request.ChargeOrderRequest;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue( true );
    }

    @Test
    public void generateChargeOrderRequestJson() {
        ChargeOrderRequest chargeOrderRequest = new ChargeOrderRequest();
        chargeOrderRequest.setChannelOrderId(UUID.randomUUID().toString())
                .setChargePrice("123.14")
                .setMerchantName("SNOWALKER")
                .setPurseId("NO00000001")
                .setUserPhoneNum("13912312343")
                .setTimestamp(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis())));
        chargeOrderRequest.setSign(chargeOrderRequest.sign("asdaseasdaddada"));
        String json = JSON.toJSONString(chargeOrderRequest);
        System.out.println(json);
    }
}
