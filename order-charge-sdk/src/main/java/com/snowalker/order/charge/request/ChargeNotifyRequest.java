package com.snowalker.order.charge.request;

import com.google.common.base.Preconditions;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/11 10:57
 * @className ChargeNotifyDto
 * @desc 订单结果通知入参
 */
public class ChargeNotifyRequest implements Serializable {

    private static final long serialVersionUID = -2278245695213335505L;

    /**订单状态*/
    private String order_status;
    /**渠道订单id*/
    private String channel_orderid;
    /**平台订单id*/
    private String plat_orderid;
    /**订单结束时间*/
    private String finish_time;
    /**签名*/
    private String sign;

    public String getSign() {
        return sign;
    }

    public ChargeNotifyRequest setSign(String sign) {
        this.sign = sign;
        return this;
    }

    public String getOrder_status() {
        return order_status;
    }

    public ChargeNotifyRequest setOrder_status(String order_status) {
        this.order_status = order_status;
        return this;
    }

    public String getChannel_orderid() {
        return channel_orderid;
    }

    public ChargeNotifyRequest setChannel_orderid(String channel_orderid) {
        this.channel_orderid = channel_orderid;
        return this;
    }

    public String getPlat_orderid() {
        return plat_orderid;
    }

    public ChargeNotifyRequest setPlat_orderid(String plat_orderid) {
        this.plat_orderid = plat_orderid;
        return this;
    }

    public String getFinish_time() {
        return finish_time;
    }

    public ChargeNotifyRequest setFinish_time(String finish_time) {
        this.finish_time = finish_time;
        return this;
    }

    /**
     * MD5签名
     */
    public String sign(String privateKey) {
        Preconditions.checkNotNull(this.getChannel_orderid());
        Preconditions.checkNotNull(this.getFinish_time());
        Preconditions.checkNotNull(this.getOrder_status());
        Preconditions.checkNotNull(this.getPlat_orderid());
        Preconditions.checkNotNull(privateKey);
        // 参数排序
        Map<String, String> params = new TreeMap<>();
        params.put("order_status", this.getOrder_status());
        params.put("channel_orderid", this.getChannel_orderid());
        params.put("plat_orderid", this.getPlat_orderid());
        params.put("finish_time", this.getFinish_time());
        params.put("privateKey", privateKey);
        // 参数拼装并MD5签名
        StringBuilder signSourceBuilder = new StringBuilder();
        for (String key : params.keySet()) {
            signSourceBuilder.append(key).append("=").append(params.get(key)).append("&");
        }
        // 去除最后一个&
        String signSource = signSourceBuilder.toString();
        String beforeSign = signSource.substring(0, signSource.length() - 1);
        // md5签名
        return DigestUtils.md5Hex(beforeSign);
    }

    @Override
    public String toString() {
        return "ChargeNotifyRequest{" +
                "order_status='" + order_status + '\'' +
                ", channel_orderid='" + channel_orderid + '\'' +
                ", plat_orderid='" + plat_orderid + '\'' +
                ", finish_time='" + finish_time + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
