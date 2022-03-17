package com.yubin.httplibrary.model;

import java.util.List;

public class MockConfig {

    private List<Mocks> mocks;

    public List<Mocks> getMocks() {
        return mocks;
    }

    public static class Mocks {
        private String name;
        private List<Mock> mock;

        public static class Mock {
            private String api;
            private String method;
            private String mockFile;

            public String getApi() {
                return api;
            }

            public String getMethod() {
                return method;
            }

            public String getMockFile() {
                return mockFile;
            }
        }

        public String getName() {
            return name;
        }

        public List<Mock> getMock() {
            return mock;
        }
    }
}
