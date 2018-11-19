package com.estyle.paging.entity;

import java.util.List;

/**
 * Created by zhangyi on 2018/11/5.
 */
public class DishEntity {

    private int ret;
    private List<DataEntity> data;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public List<DataEntity> getData() {
        return data;
    }

    public void setData(List<DataEntity> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DishEntity{" +
                "ret=" + ret +
                ", data=" + data +
                '}';
    }

    public static class DataEntity {

        private String title;

        @Override
        public String toString() {
            return "DataEntity{" +
                    "title='" + title + '\'' +
                    '}';
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
