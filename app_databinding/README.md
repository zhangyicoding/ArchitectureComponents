# <center>DataBinding</center>

用于实现MVVM模式（Model-View-ViewModel），可以在xml布局文件中将数据绑定给UI控件，并实现简易的业务逻辑。可以理解为：

- UI控件：从【xml布局】产生，进入【java代码】中使用
- 数据：从【java代码】产生，进入【xml布局】使用

### 一、如何使用
#### 1、环境搭建
在module/build.gradle中添加dataBinding库，如下：

```java
android {
    ...
    
    dataBinding {
        enabled = true
    }
}
```
#### 2、升级xml布局——&lt;layout&gt;标签
```java
<layout xmlns:android="http://schemas.android.com/apk/res/android">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">
    </LinearLayout>
</layout>
```
#### 3、绑定xml布局到指定的Activity/Fragment
##### Activity：

```java
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    }
```
##### Fragment：
```java
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentMainBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        return binding.getRoot();
    }
```
binding，即绑定对象，可以理解为数据进入xml布局的桥梁。数据可以为基本数据类型、对象、集合(按照Google官网书写编译不通过，汗(⊙﹏⊙)b)，这里主要演示传递对象给xml布局，entity中的属性要么是public修饰，要么private配合getter/setter，二者等效，如下：

```java
public class User {

    private String username;
    private String avatar;
    private String vipInfo;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getVipInfo() {
        return vipInfo;
    }

    public void setVipInfo(String vipInfo) {
        this.vipInfo = vipInfo;
    }
}
```

#### 4、将数据传递至xml布局
在&lt;layout&gt;标签中声明要接受的数据类型及变量名，如下：

```java
<layout xmlns:android="http://schemas.android.com/apk/res/android">

    <data>
        <variable
            name="user"
            type="com.estyle.databinding.entity.User" />
    </data>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">
    </LinearLayout>
</layout>
```

&lt;data&gt;标签中还可以使用&lt;import&gt;标签导包，这样在type属性中不必填写全路径了。</br>
编译器在编译时会解析&lt;variable&gt;中的name属性，并在生成的XXXBinding.class中创建setXXX方法，即可实现将java中的数据传入xml布局。

```java
	binding.setUser(user);
```
#### 5、数据绑定到UI
xml中接收到了数据，接下来就可以绑定的到UI控件对应的属性上了，属性值格式为"@{}"，例如：

```java
<layout xmlns:android="http://schemas.android.com/apk/res/android">

    <data>
        <variable
            name="user"
            type="com.estyle.databinding.entity.User" />
    </data>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">
        
        <TextView
            android:id="@+id/username_text_view"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@{user.username}" />
    </LinearLayout>
</layout>
```

如果数据无法与UI控件属性对应咋办，例如图片url地址，如何给ImageView展示？这里介绍两种高可扩展性的操作：

- 1、自定义逻辑处理数据，同时自定义属性去接收数据。【这样符合Databinding书写规范】

```java
/**
 * 自定义属性处理类
 * 处理属性的方法需要@BindingAdapter，且必须是public static修饰，编译器在编译时会自动寻找该方法
 */
public class MyBindingAdapter {

    // 该注解中声明任意数量的属性
    @BindingAdapter(value = {"url", "circle"}, requireAll = false)// requireAll表示是否所有属性都必须使用
    public static void loadImageFromNetwork(ImageView view, String url, boolean isCircle) {
        RequestBuilder<Drawable> requestBuilder = Glide.with(view.getContext())
                .load(url);

        if (isCircle) {
            requestBuilder.apply(RequestOptions.circleCropTransform());
        }

        requestBuilder.into(view);
    }
}
```
接下来可以在xml布局中给ImageView使用自定义属性了：

```java
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">

    <data>
        <variable
            name="user"
            type="com.estyle.databinding.entity.User" />
    </data>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">
            
        <ImageView
            android:id="@+id/avatar_image_view"
            android:layout_width="100dp"
            android:layout_height="100dp"
            app:circle="@{true}"
            app:url="@{user.avatar}" />
    </LinearLayout>
</layout>
```

这种方式虽然高(zhuang)端(bi)，但麻烦，我们可以选择接地气的方式：

- 2、在Java代码中获取ImageView对象并做逻辑。

```java
	Glide.with(context)
		.load(user.avatar)
		.into(binding.avatarImageView);
```

XXXBinding.class在编译时会根据UI控件的id创建小驼峰格式的变量名，同时不需要findViewById了，据官网说性能比findViewById更好。

#### 6、事件绑定到UI
事件监听器同样可以在xml中绑定，并且只支持Java8的lambda表达式，例如：

```java
<layout xmlns:android="http://schemas.android.com/apk/res/android">

    <data>

        <variable
            name="user"
            type="com.estyle.databinding.entity.User" />

        <variable
            name="presenter"
            type="com.estyle.databinding.presenter.MainPresenter" />
    </data>


    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <Button
            android:id="@+id/tip_btn"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:onClick="@{(v) -> presenter.tip(v, user.username)}" />
    </LinearLayout>
</layout>
```

由此看出tipBtn的点击事件在MainPresenter中实现：

```java
public class MainPresenter {

    public MainPresenter(ActivityMainBinding binding) {
        binding.setPresenter(this);

        initUser(binding);
    }

    private void initUser(ActivityMainBinding binding) {
        User user = new User();
        // user.setUsername("zhangsan");

        binding.setUser(user);
    }

    public void tip(View view, String username) {
        Toast.makeText(view.getContext().getApplicationContext(), username.toString(), Toast.LENGTH_SHORT).show();
    }
}
```

上面代码会产生NPE，因为user.username为null。DataBinding支持运算逻辑、三目运算逻辑、String的“+”拼接逻辑，我们可以这样升级代码防止NPE：

```java
        <Button
            android:id="@+id/tip_btn"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:onClick="@{(v) -> user.username != null ? presenter.tip(v, user.username) : void}" />
    </LinearLayout>
```

其中void表示什么都不做。

#### 7、局部刷新
我们知道，执行binding的set方法可以将数据传入xml：

```java
	binding.setUser(user);
```

这样操作会将user中的全部数据都传入xml布局，所有绑定user数据的UI控件都会刷新。
如果只想修改某个UI控件的内容，可以使用局部刷新。例如，我们只希望刷新user的vip信息。
xml布局:

```java
        <TextView
            android:id="@+id/vip_text_view"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@{user.vipInfo}" />

        <android.support.v7.widget.SwitchCompat
            android:id="@+id/vip_switch"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:onCheckedChanged="@{(v, isChecked) -> presenter.setVIP(v, isChecked)}" />
```

Presenter代码：

```java
    public void setVIP(CompoundButton view, boolean isChecked) {
        user.setVipInfo(isChecked ? "尊敬的VIP用户" : "用户");
    }
```

可以看出，我们只是对user对象做了赋值操作。那么，User.class要做如下修改：

```java
public class User extends BaseObservable {

    private ObservableField<String> vipInfo = new ObservableField<>();

	@Bindable
    public String getVipInfo() {
        return vipInfo.get();
    }

    public void setVipInfo(String vipInfo) {
        this.vipInfo.set(vipInfo);
        notifyPropertyChanged(BR.vipInfo);
    }
}
```

- 1、此时User需要继承BaseObservable类，BaseObservable类提供了观察者模式刷新数据。</br>
- 2、vipInfo由String类型升级为观察者ObservableField<String>。
观察者类型有多种，例如不需要拆箱/封箱操作的基本数据类型观察者如ObservableInt等、
集合观察者ObservableArrayList等、以及任意类型观察者ObservableField。</br>
- 3、我们需要对传统的getter/setter做改动，变更为Observable的get()和set()，其中自动生成的BR.class记录了xml布局中使用到的对象id和@Bindable修饰的属性的id，统称为fieldId。setter使用

```java
	notifyPropertyChanged(fieldId);
```

通知@Bindable修饰的对应getter修改数据，并通知xml布局中绑定了该属性的UI进行刷新。

##### 最后提示：需要手动Make Module触发自动生成DataBinding相关class文件，
### 二、源码分析
- [参考1链接](https://www.jianshu.com/p/c570f1dce845) 
- [参考2链接](https://juejin.im/entry/57e48e7ba22b9d006139c60b) 

接下来代码均在编译期产生，即module/build文件夹下。
带有“@{}”符号的xml布局不会编译通过，所以编译器生成了原始的xml布局，但做了处理。DataBinding只会识别3种UI控件：根布局，有id的控件，有@{}符号的控件。这三种UI控件在生成的普通xml布局中自动添加了tag标记，例如：

- item_main.xml

```java
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"    
    android:orientation="vertical"
    android:tag="layout/activity_main_0">

    <ImageView
            android:id="@+id/avatar_image_view"
            android:layout_width="100dp"
            android:layout_height="100dp"
            android:tag="binding_1" />

        <TextView
            android:id="@+id/username_text_view"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:tag="binding_2" />

        <CheckBox
            android:id="@+id/vip_check_box"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="是否开启会员"
            android:tag="binding_3" />
</LinearLayout>
```

tag标记用于寻找各个View实例，不再使用findViewById。
我们写的xml布局中还有&lt;variable&gt;标签等数据信息。xml布局中的全部信息被自动写在item_main-layout.xml文件中：

```java
<Layout
    absoluteFilePath="/Users/zhangyi/Desktop/Projects/DataBinding/app/src/main/res/layout/item_main.xml"
    directory="layout" isMerge="false" layout="item_main" modulePackage="com.estyle.databinding">
    <Variables name="entity" declared="true" type="com.estyle.databinding.entity.MainListEntity">
        <location endLine="8" endOffset="65" startLine="6" startOffset="8" />
    </Variables>
    <Variables name="holder" declared="true"
        type="com.estyle.databinding.adapter.MainAdapter.MainViewHolder">
        <location endLine="12" endOffset="78" startLine="10" startOffset="8" />
    </Variables>
    <Targets>
        <Target tag="layout/item_main_0" view="LinearLayout">
            <Expressions />
            <location endLine="40" endOffset="18" startLine="15" startOffset="4" />
        </Target>
        <Target id="@+id/avatar_image_view" tag="binding_1" view="ImageView">
            <Expressions>
                <Expression attribute="app:is_circle" text="true">
                    <Location endLine="25" endOffset="34" startLine="25" startOffset="12" />
                    <TwoWay>false</TwoWay>
                    <ValueLocation endLine="25" endOffset="32" startLine="25" startOffset="29" />
                </Expression>
                <Expression attribute="app:url" text="entity.avatar">
                    <Location endLine="26" endOffset="37" startLine="26" startOffset="12" />
                    <TwoWay>false</TwoWay>
                    <ValueLocation endLine="26" endOffset="35" startLine="26" startOffset="23" />
                </Expression>
            </Expressions>
            <location endLine="26" endOffset="40" startLine="21" startOffset="8" />
        </Target>
        <Target id="@+id/username_text_view" tag="binding_2" view="TextView">
            <Expressions>
                <Expression attribute="android:text" text="entity.username">
                    <Location endLine="32" endOffset="44" startLine="32" startOffset="12" />
                    <TwoWay>false</TwoWay>
                    <ValueLocation endLine="32" endOffset="42" startLine="32" startOffset="28" />
                </Expression>
            </Expressions>
            <location endLine="32" endOffset="47" startLine="28" startOffset="8" />
        </Target>
        <Target id="@+id/vip_check_box" tag="binding_3" view="CheckBox">
            <Expressions>
                <Expression attribute="android:onCheckedChanged"
                    text="(view, isChecked) -&gt; holder.checkVIP(view, isChecked)">
                    <Location endLine="38" endOffset="94" startLine="38" startOffset="12" />
                    <TwoWay>false</TwoWay>
                    <ValueLocation endLine="38" endOffset="92" startLine="38" startOffset="40" />
                </Expression>
            </Expressions>
            <location endLine="39" endOffset="35" startLine="34" startOffset="8" />
        </Target>
    </Targets>
</Layout>
```

DataBindingUtil是一切入口，入口代码：

```java
	DataBindingUtil.setContentView(this, R.layout.item_main);
```

点进去：

```java
    public static <T extends ViewDataBinding> T setContentView(@NonNull Activity activity,
            int layoutId, @Nullable DataBindingComponent bindingComponent) {
        activity.setContentView(layoutId);
        View decorView = activity.getWindow().getDecorView();
        ViewGroup contentView = (ViewGroup) decorView.findViewById(android.R.id.content);
        return bindToAddedViews(bindingComponent, contentView, 0, layoutId);
    }
```

看bindToAddedViews方法：

```java
@Nullable
    private static <T extends ViewDataBinding> T bindToAddedViews(DataBindingComponent component,
            ViewGroup parent, int startChildren, int layoutId) {
            // 复制不了，看源码
            // return 两种bind(...)方法;
            }
```

继续看bind()方法：

```java
    @SuppressWarnings("unchecked")
    static <T extends ViewDataBinding> T bind(DataBindingComponent bindingComponent, View[] roots,
            int layoutId) {
        return (T) sMapper.getDataBinder(bindingComponent, roots, layoutId);
    }

    static <T extends ViewDataBinding> T bind(DataBindingComponent bindingComponent, View root,
            int layoutId) {
        return (T) sMapper.getDataBinder(bindingComponent, root, layoutId);
    }
```

看sMapper的实现类DataBindingMapperImpl.java中的getDataBinder()方法：

```java
  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View view, int layoutId) {
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = view.getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
        case  LAYOUT_ITEMMAIN: {
          if ("layout/item_main_0".equals(tag)) {
            return new ItemMainBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_main is invalid. Received: " + tag);
        }
        ...
      }
    }
    return null;
  }
```

这里返回了ItemMainBindingImpl，于是我们得到了结果：

```java
	ItemMainBinding binding = DataBindingUtil.setContentView(this, R.layout.item_main);
```

先说一下继承结构：ViewDataBinding -> ItemMainBinding -> ItemMainBindingImpl

继续看ItemMainBindingImpl的构造方法：

```java
    public ItemMainBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 4, sIncludes, sViewsWithIds));
    }
    private ItemMainBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 1
            , (android.widget.ImageView) bindings[1]
            , (android.widget.TextView) bindings[2]
            , (android.widget.CheckBox) bindings[3]
            );
        this.avatarImageView.setTag(null);
        this.mboundView0 = (android.widget.LinearLayout) bindings[0];
        this.mboundView0.setTag(null);
        this.usernameTextView.setTag(null);
        this.vipCheckBox.setTag(null);
        setRootTag(root);
        // listeners
        mCallback1 = new com.estyle.databinding.generated.callback.OnCheckedChangeListener(this, 1);
        invalidateAll();
    }
```

mapBindings方法是遍历View树的关键，它是父类ViewDataBinding的方法。之前我们说给View添加tag标记方便寻找。mapBindings方法包括一个Object[]数组叫bindings，就是View树的容器，怎么获取其中的View呢，数组的index就是自动给View添加的tag数字值。这里得出DataBinding源码学习的第一个重要知识点：

#### 遍历View树的操作mapBindings由于只执行一次即可获得全部View，性能优于findViewById。(注：findViewById每次调用都会完全遍历)

顺便引出第二个点(keng)：setRootTag(root);给根视图永久添加了一个tag，这影响了我们对根视图tag属性的使用。对于其他View，虽然编译器自动设置tag标记，当编译完毕后都重置了，不影响我们使用tag。

再说说传递数据的操作。之前说的item_main-layout.xml文件中记录了UI和数据的全部信息，例如，解析到变量名为entity的数据，便自动生成了setEntity方法：

```java
    public void setEntity(@Nullable com.estyle.databinding.entity.MainListEntity Entity) {
        updateRegistration(0, Entity);
        this.mEntity = Entity;
        synchronized(this) {
            mDirtyFlags |= 0x1L;
        }
        notifyPropertyChanged(BR.entity);
        super.requestRebind();
    }
```

notifyPropertyChanged(BR.entity);用于通知entity全部数据刷新，并通知绑定了entity的UI控件刷新。