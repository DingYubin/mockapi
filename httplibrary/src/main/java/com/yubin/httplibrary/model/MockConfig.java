package com.yubin.httplibrary.model;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class MockConfig {

    private List<Mocks> mocks;

    @NoArgsConstructor
    @Data
    public static class Mocks {
        private String name;
        private List<Mock> mock;

        @NoArgsConstructor
        @Data
        public static class Mock {
            private String api;
            private String mockFile;
        }
    }
}
