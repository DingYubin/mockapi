package com.example.mock_api;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

@lombok.NoArgsConstructor
@lombok.Data
public class CECAdBatchListBean implements Serializable {

    @SerializedName("AdBatchList")
    private List<AdBatchListBean> adBatchList;

    @lombok.NoArgsConstructor
    @lombok.Data
    public static class AdBatchListBean {
        private String position;
        private List<ListBean> list;

        @lombok.NoArgsConstructor
        @lombok.Data
        public static class ListBean {
            private String id;
            private String name;
            private String positionCode;
            private Integer positionNum;
            private String positionAction;
            private String advType;
            private Integer positionSequence;
            private String displayType;
            private String status;
            private String sponsorType;
            private String displayContentlink;
            private String picLink;
        }
    }
}
