# <center> Paging </center>

来自Google的用于实现分页加载的库，包括网络请求和数据库分页加载。特点是将加载数据的逻辑完全和UI分离，分页加载也不必等滑到底部再上拉一下才加载数据，因为它可以滑动一定距离后预加载下一页，让用户感觉数据展示很流畅，类似京东的效果。</br>
作为JetPack全家桶之一，与数据包装类LiveData配合效果更佳。如果是加载数据库的数据，则可以与Room深度整合。

网上到处都是和数据库框架Room联合实现分页查询数据库的文章，这里我将演示使用Paging + Retrofit实现网络数据查询。

## 一、准备工作

这里先介绍一些必要的类：

- PagedList

这是一个List的实现类，<b>我们可以把它当成List使用。</b>

- PagedListAdapter

分页加载肯定离不开各种Adapter和各种对应的UI控件。<b>这里我们只能使用RecyclerView</b>，因为这个Adapter是RecyclerView.Adapter的子类，也是PagedList对应的适配器。

- DataSource

数据源，这里将成为产生数据的地方，无论来自网络还是数据库。

- DataSource.Factory

这是生产DataSource的工厂类了。


重点在于DataSource。他是一个抽象类，我们将会接触到DataSource提供的几个操作，包括先初始化一波数据、加载上一页(如果可以的话)、加载下一页数据等。

Google提供了3个DataSource的实现类：

- PageKeyedDataSource，我们用这个
- ItemKeyedDataSource
- PositionalDataSource

每一种DataSource都需要指定分页item的数量，但PageKeyedDataSource还可以指定每个分页关键词，例如第几页，这个“几”就是加载分页的关键词，这个最适合网络数据分页加载。

第二种叫ItemKeyedDataSource，它也有关键词，但指的是加载某一页成功后最后一个item提供的关键词key，我们根据这个key作为下一页数据的起点，再拼接一页数据。例如，查询数据库时，以item的主键_id作为key，当第一页10个item加载完成后，key就是第10个item的_id，下次我们以key为起点加载下一页数据，即从第11条开始加载10个。

第三种PositionalDataSource，我把它理解为第二种的简化版。第二种可以任意指定item的某个属性作为key，第三种则只能以item的位置作为key，根据位置作为下一页的起点继续拼接数据。所以我认为，第二种和第三种更适合分页加载数据库。

既然PageKeyedDataSource适合分页加载网络api提供的数据，那我们就来看看本Demo中提供的网络url的结构：

```java
http://www.qubaobei.com/ios/cf/dish_list.php?stage_id=1&limit=10&page=1
```

倒着看，第一个参数page代表第几页，它将成为加载分页的关键词key。它前面的limit参数代表了每页item的数量，其余参数不值得分析了。

## 二、代码实现

导包就不介绍了，参考Demo或者Google官网都可以。

### 1、Paging + LiveData

我们先创建一个展示数据的容器，直接使用LiveData进行包装了:

```java
        LiveData<PagedList<DishEntity.DataEntity>> mList = new LivePagedListBuilder<>(
                mDataSourceFactory,
                new PagedList.Config.Builder()
                        .setInitialLoadSizeHint(10)// 加载初始页的item数量
                        .setPageSize(10)// 加载之后每页的item数量
                        .setPrefetchDistance(2)// 预加载距离，单位没有明确说明，可能是item的数量
                        .build()
        ).build();
```

LivePagedListBuilder包含两个参数，第一个就是DataSource工厂对象，第二个是配置对象Config，对配置做了一些设定，不再赘述了。
DataSource工厂对象就是new出来的，那我们看看它的代码：

```java
    public static class Factory extends DataSource.Factory<Integer, DishEntity.DataEntity> {

        @Override
        public DataSource<Integer, DishEntity.DataEntity> create() {
            DishDataSource dataSource = new DishDataSource();
            return dataSource;
        }
    }
```

我把它写成了DataSource的内部类。它有两个泛型，第一个泛型代表每个分页的关键词key，我们要指定第几页，所以关键词是Integer类型。第二个泛型指每个item的数据类型。</br>
工厂类的唯一作用就是通过create方法创建一个DataSource实例。

那么我们就来看看DataSource的代码：

```java
public class DishDataSource extends PageKeyedDataSource<Integer, DishEntity.DataEntity> {

    /**
     * 加载初始页
     *
     * @param params   加载初始页的参数，包括初始页item数量等信息
     * @param callback 加载初始页的回调。数据加载成功后，通过该回调把结果传给数据容器
     */
    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, DishEntity.DataEntity> callback) {
        loadData(params.requestedLoadSize, 1, callback, null);
    }

    /**
     * 加载上一页
     *
     * @param params   加载上一页的参数，包括上一页item数量、上一页的关键词key等信息
     * @param callback 加载上一页的回调。数据加载成功后，通过该回调把结果传给数据容器
     */
    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, DishEntity.DataEntity> callback) {
    }

    /**
     * 加载下一页
     *
     * @param params   加载下一页的参数，包括下一页item数量、下一页的关键词key等信息
     * @param callback 加载下一页的回调。数据加载成功后，通过该回调把结果传给数据容器
     */
    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull LoadCallback<Integer, DishEntity.DataEntity> callback) {
        loadData(params.requestedLoadSize, params.key, null, callback);
    }

    /**
     * 加载网络数据
     *
     * @param limit           加载页item的数量
     * @param page            加载页的页码
     * @param initialCallback 初始页使用的callback，没有则传null
     * @param afterCallback   分页使用的callback，没有则传null
     */
    private void loadData(int limit, final int page, final LoadInitialCallback<Integer, DishEntity.DataEntity> initialCallback, final LoadCallback<Integer, DishEntity.DataEntity> afterCallback) {
        MyApplication.getInstance()
                .getRetrofit()
                .create(DishService.class)
                .getDish(limit, page)// 分页item数量，分页页码
                .enqueue(new Callback<DishEntity>() {
                    @Override
                    public void onResponse(Call<DishEntity> call, Response<DishEntity> response) {
                        List<DishEntity.DataEntity> list = response.body().getData();

                        if (initialCallback != null) {
                            // 初始页数据加载完毕后，通过回调将数据传至容器
                            // 如果没有上一页，则传null
                            // 设置初始页之后的分页固定为第二页
                            initialCallback.onResult(list, null, 2);
                        } else {
                            // 分页数据加载完毕后，通过回调将数据传至容器
                            // 设置之后的分页固定为第N+1页
                            afterCallback.onResult(list, page + 1);
                        }
                    }

                    @Override
                    public void onFailure(Call<DishEntity> call, Throwable t) {
                    }
                });
    }

    public static class Factory extends DataSource.Factory<Integer, DishEntity.DataEntity> {

        @Override
        public DataSource<Integer, DishEntity.DataEntity> create() {
            DishDataSource dataSource = new DishDataSource();
            return dataSource;
        }
    }
}
```

数据在DataSource里产生并通过回调传递到容器LiveData&lt;PagedList&gt;中，接下来需要展示在UI上了。

```java
        mList.observe(this, new Observer<PagedList<DishEntity.DataEntity>>() {
            @Override
            public void onChanged(@Nullable PagedList<DishEntity.DataEntity> list) {
                mDishAdapter.submitList(list);
            }
        });
```

别忘了mList.observe这个神操作是LiveData自带的观察者模式的技能，通知adapter接收数据，其中adapter的submitList()方法是适配器自带的，不是我们自己写的，那我们就来看看适配器的代码吧，一些解释在代码后面：

```java
public class DishAdapter extends PagedListAdapter<DishEntity.DataEntity, DishAdapter.ViewHolder> {

    public DishAdapter() {
        super(sCallback);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        return new ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;

        ViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dish, parent, false));
            mTextView = itemView.findViewById(R.id.title_tv);
        }

        private void bind(DishEntity.DataEntity dish) {
            mTextView.setText(dish.getTitle());
        }
    }

    // RecyclerView附属的比较新旧item差异的回调
    private static DiffUtil.ItemCallback<DishEntity.DataEntity> sCallback = new DiffUtil.ItemCallback<DishEntity.DataEntity>() {

        // 比较新旧item对象是否相同
        @Override
        public boolean areItemsTheSame(@NonNull DishEntity.DataEntity oldItem, @NonNull DishEntity.DataEntity newItem) {
            return oldItem == newItem;
        }

        // 比较新旧item内容是否相同
        @Override
        public boolean areContentsTheSame(@NonNull DishEntity.DataEntity oldItem, @NonNull DishEntity.DataEntity newItem) {
            return TextUtils.equals(oldItem.getTitle(), newItem.getTitle());
        }
    };
}
```

正如文章开头所说，这个适配器继承自RecyclerView.Adapter。它有两个泛型，第一个代表每个item的数据类型，因为适配器内部做了大量封装，包括要展示的数据集合，所以只要告诉它类型好。第二个泛型则是普通的ViewHolder了。</br>
不过这个适配器需要一个构造方法，并传入一个参数，叫做DiffUtil.ItemCallback，它是RecyclerView的一个辅助工具，用于检测当RecyclerView的item发生变化时，比对新旧item是否相同，通过重写areItemsTheSame和areContentsTheSame确定新旧item的对比规则。适配器的其它部分则和普通的RecyclerView.Adapter完全一致了。

通过实现上面的核心代码，我们就实现了Paging分页加载网络数据。

### 2、Paging + RxJava

这个可以有，但会让人很失望。</br>
我们知道Retrofit可以和RxJava无缝整合（HttpService中Call替换为Observable），我以为可以把RxJava当做Retrofit和Paing的桥梁将二者无缝连接起来，但我错了...Retrofit和Paging各用各的RxJava，中间有断层。Paging配合RxJava指的是可以替换PagedListAdapter对应的全部数据的容器LiveData&lt;PagedList&gt;为Observable&lt;PagedList&gt;或Flowable&lt;PagedList&gt;，DataSource中的数据不管是咋产生的，还是得用callback.onResult()将数据添加到全部数据的容器中。但Room可以和Paging无缝整合，因为他们都是JetPack全家桶成员...

下面我们实现了用RxJava的Observable<PagedList>替换全部数据的容器LiveData<PagedList>，当然，Flowable也可以。

```java
Observable<PagedList<DishEntity.DataEntity>> mList = new RxPagedListBuilder<>(
                mDataSourceFactory,
                new PagedList.Config.Builder()
                        .setInitialLoadSizeHint(10)
                        .setPageSize(10)
                        .setPrefetchDistance(2)
                        .build()
        ).buildObservable();
```

全部数据的容器mList有了，接下来用RxJava经典的方式来观察数据：

```java
    @Override
    protected void onStart() {
        super.onStart();
        mDisposable = mList.subscribe(new Consumer<PagedList<DishEntity.DataEntity>>() {
            @Override
            public void accept(PagedList<DishEntity.DataEntity> list) throws Exception {
                mDishAdapter.submitList(list);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }
```

分页加载效果与LiveData方式相同。

## 三、源码分析
分析源码的关键入口是PagedList，那么我们就从数据容器LiveData&lt;PagedList&gt;的产生开始。

```java
        LiveData<PagedList<DishEntity.DataEntity>> mList = new LivePagedListBuilder<>(/*省略参数*/).build();
```

进入LivePagedListBuilder.build()方法：

```java
@NonNull
    public LiveData<PagedList<Value>> build() {
        return create(mInitialLoadKey, mConfig, mBoundaryCallback, mDataSourceFactory,
                ArchTaskExecutor.getMainThreadExecutor(), mFetchExecutor);
    }
```

进入create()方法：

```java
@AnyThread
    @NonNull
    private static <Key, Value> LiveData<PagedList<Value>> create(/*省略参数*/) {
        return new ComputableLiveData<PagedList<Value>>(fetchExecutor) {
           
           // ...

            @Override
            protected PagedList<Value> compute() {
                @Nullable Key initializeKey = initialLoadKey;
                
                // ...

                    mList = new PagedList.Builder<>(mDataSource, config)
                            .setNotifyExecutor(notifyExecutor)
                            .setFetchExecutor(fetchExecutor)
                            .setBoundaryCallback(boundaryCallback)
                            .setInitialKey(initializeKey)
                            .build();
                } while (mList.isDetached());
                return mList;
            }
        }.getLiveData();
    }
```

LiveData<PagedList>只能算PagedList的包装容器，这里可以看到真正PagedList的创建。进入PagedList.Builder.build()：

```java
@WorkerThread
        @NonNull
        public PagedList<Value> build() {
            
            // ...

            //noinspection unchecked
            return PagedList.create(
                    mDataSource,
                    mNotifyExecutor,
                    mFetchExecutor,
                    mBoundaryCallback,
                    mConfig,
                    mInitialKey);
        }
```

继续看PagedList.create()方法：

```java
@NonNull
    private static <K, T> PagedList<T> create(/*省略参数*/) {
        if (dataSource.isContiguous() || !config.enablePlaceholders) {
           
           // ...
           
            return new ContiguousPagedList<>(contigDataSource,
                    notifyExecutor,
                    fetchExecutor,
                    boundaryCallback,
                    config,
                    key,
                    lastLoad);
        } else {
            return new TiledPagedList<>((PositionalDataSource<T>) dataSource,
                    notifyExecutor,
                    fetchExecutor,
                    boundaryCallback,
                    config,
                    (key != null) ? (Integer) key : 0);
        }
    }
```

这里有个判断但并不重要，重要的时会返回ContiguousPagedList或TiledPagedList实例，而这两个类的构造方法中包含一个相同的关键操作：

```java
mDataSource.dispatchLoadInitial(key,
                    mConfig.initialLoadSizeHint,
                    mConfig.pageSize,
                    mConfig.enablePlaceholders,
                    mMainThreadExecutor,
                    mReceiver);
```
出现数据源，并分发加载初始数据的事件。这里的mDataSource是抽象类，我们进入一个具体数据源中，例如我们使用的PageKeyedDataSource，看看dispatchLoadInitial()方法：

```java
@Override
    final void dispatchLoadInitial(/*省略参数*/) {
        LoadInitialCallbackImpl<Key, Value> callback =
                new LoadInitialCallbackImpl<>(this, enablePlaceholders, receiver);
        loadInitial(new LoadInitialParams<Key>(initialLoadSize, enablePlaceholders), callback);

        // If initialLoad's callback is not called within the body, we force any following calls
        // to post to the UI thread. This constructor may be run on a background thread, but
        // after constructor, mutation must happen on UI thread.
        callback.mCallbackHelper.setPostExecutor(mainThreadExecutor);
    }
```

出现loadInitial()方法，这个方法就是我们自己实现的加载初始数据的方法。

<b>结论：构建LiveData&lt;PagedList&gt;实例的时候，就是加载初始数据的时机。</b>

我们在数据源的loadInitial和loadAfter都调用了callback.onResult()方法，将产生的数据加入数据容器中。以loadInitial为例，我们看看LoadInitialCallbackImpl的onResult()方法：

```java
@Override
        public void onResult(/*省略参数*/) {
            if (!mCallbackHelper.dispatchInvalidResultIfInvalid()) {
                LoadCallbackHelper.validateInitialLoadParams(data, position, totalCount);

                // setup keys before dispatching data, so guaranteed to be ready
                mDataSource.initKeys(previousPageKey, nextPageKey);

                int trailingUnloadedCount = totalCount - position - data.size();
                if (mCountingEnabled) {
                    mCallbackHelper.dispatchResultToReceiver(new PageResult<>(
                            data, position, trailingUnloadedCount, 0));
                } else {
                    mCallbackHelper.dispatchResultToReceiver(new PageResult<>(data, position));
                }
            }
        }
```

我们看看吧数据分发至接收器的方法mCallbackHelper.dispatchResultToReceiver()：

```java
void dispatchResultToReceiver(final @NonNull PageResult<T> result) {
            Executor executor;
            synchronized (mSignalLock) {
                if (mHasSignalled) {
                    throw new IllegalStateException(
                            "callback.onResult already called, cannot call again.");
                }
                mHasSignalled = true;
                executor = mPostExecutor;
            }

            if (executor != null) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        mReceiver.onPageResult(mResultType, result);
                    }
                });
            } else {
                mReceiver.onPageResult(mResultType, result);
            }
        }
```

mReceiver是PageResult.Receiver<T>类型的对象。看看mReceiver.onPageResult()的一个具体实现方法：

```java
@AnyThread
        @Override
        public void onPageResult(@PageResult.ResultType int resultType,
                @NonNull PageResult<V> pageResult) {
            
            // ...
            
                mStorage.appendPage(page, ContiguousPagedList.this);
                
            // ...
```

既然是将产生的数据添加到全部数据容器中，我们找到一个拼接数据的关键方法mStorage.appendPage()：

```java
void appendPage(@NonNull List<T> page, @NonNull Callback callback) {
        
        // ...
        
        callback.onPageAppended(mLeadingNullCount + mStorageCount - count,
                changedCount, addedCount);
    }
```

继续跟着关键词onPageAppended()方法追踪：

```java
@MainThread
    @Override
    public void onPageAppended(int endPosition, int changedCount, int addedCount) {
        // consider whether to post more work, now that a page is fully appended

        mAppendItemsRequested = mAppendItemsRequested - changedCount - addedCount;
        mAppendWorkerRunning = false;
        if (mAppendItemsRequested > 0) {
            // not done appending, keep going
            scheduleAppend();
        }

        // finally dispatch callbacks, after append may have already been scheduled
        notifyChanged(endPosition, changedCount);
        notifyInserted(endPosition + changedCount, addedCount);
    }
```

这里已经进入主线程了，而且出现两个眼熟的方法notifyChanged和notifyInserted，你会不会联想到RecyclerView.Adapter？分别看这两个方法。

notifyChanged：

```java
    void notifyChanged(int position, int count) {
        if (count != 0) {
            for (int i = mCallbacks.size() - 1; i >= 0; i--) {
                Callback callback = mCallbacks.get(i).get();

                if (callback != null) {
                    callback.onChanged(position, count);
                }
            }
        }
    }
```

notifyInserted：

```java
	void notifyInserted(int position, int count) {
        if (count != 0) {
            for (int i = mCallbacks.size() - 1; i >= 0; i--) {
                Callback callback = mCallbacks.get(i).get();
                if (callback != null) {
                    callback.onInserted(position, count);
                }
            }
        }
    }
```

两个方法的具体操作逻辑都交给了Callback，它的方法实现在AsyncPagedListDiffer类中，然后它又将具体逻辑交给了mUpdateCallback，UpdateCallback的具体实现：

```java
public void onInserted(int position, int count) {
        this.mAdapter.notifyItemRangeInserted(position, count);
    }

    public void onRemoved(int position, int count) {
        this.mAdapter.notifyItemRangeRemoved(position, count);
    }

    public void onMoved(int fromPosition, int toPosition) {
        this.mAdapter.notifyItemMoved(fromPosition, toPosition);
    }

    public void onChanged(int position, int count, Object payload) {
        this.mAdapter.notifyItemRangeChanged(position, count, payload);
    }
```

出现mAdpater，到此我们使用适配器对UI进行了刷新。