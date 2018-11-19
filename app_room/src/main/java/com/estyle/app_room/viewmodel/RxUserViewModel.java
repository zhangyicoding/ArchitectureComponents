package com.estyle.app_room.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.estyle.app_room.MyApplication;
import com.estyle.app_room.database.dao.RxUserDao;
import com.estyle.app_room.entity.UserEntity;
import com.estyle.app_room.zhangyi.ZYLog;


import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zhangyi on 2018/11/11.
 */
public class RxUserViewModel extends ViewModel {

    private MutableLiveData<List<UserEntity>> mList = new MutableLiveData<>();

    public MutableLiveData<List<UserEntity>> getList() {
        return mList;
    }

    private RxUserDao mUserDao;

    public RxUserViewModel() {
        mUserDao = MyApplication.getInstance()
                .getDatabase()
                .rxUserDao();
    }

    public void saveUser(final String account, final String password) {
        Disposable disposable = Flowable.just(account, password)
                .subscribeOn(Schedulers.io())
                .toList()
                .map(new Function<List<String>, UserEntity>() {
                    @Override
                    public UserEntity apply(List<String> strings) throws Exception {
                        UserEntity user = new UserEntity();
                        user.setAccount(account);
                        user.setPassword(password);
                        return user;
                    }
                })
                .map(new Function<UserEntity, Long>() {
                    @Override
                    public Long apply(UserEntity user) throws Exception {
                        return mUserDao.insertUser(user);
                    }
                })
                .subscribe(new Consumer<Long>() {
                               @Override
                               public void accept(Long id) throws Exception {
                                   ZYLog.e("rx insert id: " + id);
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                ZYLog.e("rx insert error: " + throwable.getMessage());
                            }
                        });
    }


    public void deleteUser(final String account) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(mUserDao.deleteUserByAccount(account));
            }
        })
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer count) throws Exception {
                        ZYLog.e("delete count: " + count);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ZYLog.e("delete error: " + throwable.getMessage());
                    }
                });
    }

    public void updatePassword(final String account, final String newPassowrd) {
    }

    public void showUser(String account) {
        mUserDao.queryUserByAccount(account)
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<UserEntity>() {
                               @Override
                               public void accept(UserEntity user) throws Exception {

                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                ZYLog.e("show user error: " + throwable.getMessage());
                            }
                        });
    }

    public void showUsers() {
        Disposable disposable = mUserDao.queryUsers()
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<UserEntity>>() {
                               @Override
                               public void accept(List<UserEntity> list) throws Exception {
                                   mList.postValue(list);
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                ZYLog.e("query all error: " + throwable.getMessage());
                            }
                        });
    }
}