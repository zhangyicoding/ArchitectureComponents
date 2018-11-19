package com.estyle.viewmodel.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.estyle.viewmodel.repository.MainRepository;

import java.util.List;

/**
 * Presenter层
 * 数据模型，是对视图相关数据和逻辑的抽象
 * 这里产生数据
 */
public class MainViewModel extends AndroidViewModel {

    private MutableLiveData<List<MainTab>> mTabList;

    private MainRepository mRepository;

    public MainViewModel(@NonNull Application application) {
        super(application);

        mTabList = new MutableLiveData<>();
        mRepository = new MainRepository();
    }

    public MutableLiveData<List<MainTab>> getTabList() {
        return mTabList;
    }

    public void initTabs() {
        mTabList.setValue(mRepository.getTabs());
    }

    public static class MainTab {

        private String title;
        private int code;
        private boolean isSelected;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }
}
