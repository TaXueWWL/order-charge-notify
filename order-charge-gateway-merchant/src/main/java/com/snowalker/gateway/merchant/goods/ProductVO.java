package com.snowalker.gateway.merchant.goods;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/6/10 9:45
 * @className ProductBean
 * @desc 商品配置领域
 */
public class ProductVO {

    /**商品id*/
    private String productId;
    /**商品名*/
    private String productName;
    /**供应商商品id*/
    private String productOutId;
    /**供应商商品名*/
    private String productOutName;
    /**商品库存*/
    private Integer productStock;
    /**供应商渠道id*/
    private String productChannelId;

    public String getProductId() {
        return productId;
    }

    public ProductVO setProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public String getProductName() {
        return productName;
    }

    public ProductVO setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public String getProductOutId() {
        return productOutId;
    }

    public ProductVO setProductOutId(String productOutId) {
        this.productOutId = productOutId;
        return this;
    }

    public String getProductOutName() {
        return productOutName;
    }

    public ProductVO setProductOutName(String productOutName) {
        this.productOutName = productOutName;
        return this;
    }

    public Integer getProductStock() {
        return productStock;
    }

    public ProductVO setProductStock(Integer productStock) {
        this.productStock = productStock;
        return this;
    }

    public String getProductChannelId() {
        return productChannelId;
    }

    public ProductVO setProductChannelId(String productChannelId) {
        this.productChannelId = productChannelId;
        return this;
    }

    @Override
    public String toString() {
        return "ProductVO{" +
                "productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", productOutId='" + productOutId + '\'' +
                ", productOutName='" + productOutName + '\'' +
                ", productStock=" + productStock +
                ", productChannelId='" + productChannelId + '\'' +
                '}';
    }
}
