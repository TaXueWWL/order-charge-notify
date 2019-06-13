package com.snowalker.order;

import com.snowalker.order.common.dao.dataobject.OrderInfoDO;
import com.snowalker.order.common.dao.dataobject.OrderInfoDobj;
import com.snowalker.order.common.service.OrderChargeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderChargeGatewayApplicationTests {

    @Autowired
    OrderChargeService orderChargeService;

    @Test
    public void contextLoads() {
        OrderInfoDO orderInfoDO = new OrderInfoDO().setOrderId("00001");
        OrderInfoDobj orderInfoDobj = orderChargeService.queryOrderInfo(orderInfoDO);
        System.out.println(orderInfoDobj.toString());
    }

}
