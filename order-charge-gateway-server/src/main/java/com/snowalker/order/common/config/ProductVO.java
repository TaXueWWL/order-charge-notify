package com.snowalker.order.common.config;

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
    /**商品库存*/
    private Integer productStock;

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

    public Integer getProductStock() {
        return productStock;
    }

    public ProductVO setProductStock(Integer productStock) {
        this.productStock = productStock;
        return this;
    }

    @Override
    public String toString() {
        return "ProductVO{" +
                "productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", productStock=" + productStock +
                '}';
    }
}
