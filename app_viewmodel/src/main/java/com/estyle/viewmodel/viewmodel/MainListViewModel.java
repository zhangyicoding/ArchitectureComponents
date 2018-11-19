package com.estyle.viewmodel.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.estyle.viewmodel.bean.DishBean;
import com.estyle.viewmodel.repository.MainListRepository;

import java.util.List;

public class MainListViewModel extends AndroidViewModel implements com.estyle.viewmodel.viewmodel.Callback<List<DishBean.DataBean>> {

    private MainListBean mData;
    private MutableLiveData<MainListBean> mLiveData;
    private MainListRepository mRepository;

    public MainListViewModel(@NonNull Application application) {
        super(application);
        mRepository = new MainListRepository(this);

        mData = new MainListBean();
    }

    public MutableLiveData<MainListBean> getLiveData() {
        if (mLiveData == null) {
            mLiveData = new MutableLiveData<>();
        }
        return mLiveData;
    }

    // 刷新数据
    public void refreshData() {
        mData.setPage(1);
        fetchData();
    }

    // 加载数据
    public void loadMore() {
        mData.setPage(mData.getPage() + 1);
        fetchData();
    }

    // webBtn点击事件
    private void fetchData() {
        mRepository.getMainDataFromWeb(mData.getPage());
    }

    @Override
    public void onSuccess(List<DishBean.DataBean> dataList) {
        mData.setShowTip(false);

        if (mData.getPage() == 1) {
            mData.setRefresh(true);
            mData.setDatas(dataList);
        } else {
            mData.setRefresh(false);
            mData.getDatas().addAll(dataList);
        }

        getLiveData().setValue(mData);
    }

    @Override
    public void onFailure(Throwable t) {
        mData.setRefresh(false);
        mData.setTip(t.getMessage());
        getLiveData().setValue(mData);

        if (mData.getPage() == 1) {
            mData.setRefresh(true);
        } else {
            mData.setRefresh(false);
        }
    }

    public static class MainListBean {

        private boolean isRefresh;

        public boolean isRefresh() {
            return isRefresh;
        }

        public void setRefresh(boolean refresh) {
            isRefresh = refresh;
        }

        private boolean isShowTip;
        private String tip = "";
        private int page;
        private List<DishBean.DataBean> datas;

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public boolean isShowTip() {
            return isShowTip;
        }

        public void setShowTip(boolean showTip) {
            isShowTip = showTip;
        }

        public String getTip() {
            return tip;
        }

        public void setTip(String tip) {
            this.tip = tip;
        }

        public List<DishBean.DataBean> getDatas() {
            return datas;
        }

        public void setDatas(List<DishBean.DataBean> datas) {
            this.datas = datas;
        }
    }
}
