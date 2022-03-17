package com.casstime.product.search.store.bean;

import android.os.Bundle;

import com.casstime.uikit.recyclerView.adapter.protocol.ICECDiffData;

import java.io.Serializable;
import java.util.List;

/**
 * 搜索店铺bean
 *
 * @Author wangbin
 * @Date 2019-06-13 19:24
 */
public class CECStoreList implements Serializable {


    /**
     * 是否平台推荐
     */
    private boolean isFromPlatForm;
    /**
     * 店铺列表
     */
    private List<StoreListBean> stores;

    public boolean isFromPlatForm() {
        return isFromPlatForm;
    }

    public void setFromPlatForm(boolean fromPlatForm) {
        isFromPlatForm = fromPlatForm;
    }

    public List<StoreListBean> getStores() {
        return stores;
    }

    public void setStores(List<StoreListBean> stores) {
        this.stores = stores;
    }

    public static class StoreListBean implements Serializable, ICECDiffData {
        /**
         * 店铺id
         */
        private String storeId;
        /**
         * 店铺名称
         */
        private String storeName;

        /**
         * 店铺logo url
         */
        private String storyImageUri;
        /**
         * 发货地id
         */
        private String facilityId;
        /**
         * 发货地
         */
        private String facilityName;
        /**
         * 店铺推荐产品（三个）
         */
        private List<ProductsBean> products;
        /**
         * 主营业务 关键词
         */
        private String keywords;
        private int viewType;

        public String getStoreId() {
            return storeId;
        }

        public void setStoreId(String storeId) {
            this.storeId = storeId;
        }

        public StoreListBean() {
        }

        public StoreListBean(int viewType) {
            this.viewType = viewType;
        }

        public String getStoreName() {
            return storeName;
        }

        public void setStoreName(String storeName) {
            this.storeName = storeName;
        }


        public String getStoryImageUri() {
            return storyImageUri;
        }

        public void setStoryImageUri(String storyImageUri) {
            this.storyImageUri = storyImageUri;
        }



        public String getFacilityId() {
            return facilityId;
        }

        public void setFacilityId(String facilityId) {
            this.facilityId = facilityId;
        }

        public String getFacilityName() {
            return facilityName;
        }

        public void setFacilityName(String facilityName) {
            this.facilityName = facilityName;
        }

        public List<ProductsBean> getProducts() {
            return products;
        }

        public void setProducts(List<ProductsBean> products) {
            this.products = products;
        }

        public int getViewType() {
            return viewType;
        }

        public String getKeywords() {
            return keywords;
        }

        public void setKeywords(String keywords) {
            this.keywords = keywords;
        }

        public void setViewType(int viewType) {
            this.viewType = viewType;
        }

        @Override
        public String getItemSameId() {
            return null;
        }

        @Override
        public Bundle getChangePayload(ICECDiffData other) {
            return null;
        }

        public static class ProductsBean implements Serializable {

            /**
             * 商品id
             */
            private String productId;
            /**
             * 商品名称
             */
            private String productName;
            /**
             * 商品头图 url
             */
            private String imageUri;
            /**
             * 商品价格
             */
            private String price;

            public String getProductId() {
                return productId;
            }

            public void setProductId(String productId) {
                this.productId = productId;
            }

            public String getProductName() {
                return productName;
            }

            public void setProductName(String productName) {
                this.productName = productName;
            }


            public String getImageUri() {
                return imageUri;
            }

            public void setImageUri(String imageUri) {
                this.imageUri = imageUri;
            }

            public String getPrice() {
                return price;
            }

            public void setPrice(String price) {
                this.price = price;
            }
        }
    }
}
