package com.example.mock_api.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AdBatch {

    @SerializedName("AdBatchList")
    private List<AdBatchListBean> adBatchList;

    @NoArgsConstructor
    @Data
    public static class AdBatchListBean {
        private String position;
        private List<ListBean> list;

        @NoArgsConstructor
        @Data
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
