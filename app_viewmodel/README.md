ViewModel介绍

Google官方推出的Android Architecture Components中的一部分，用于实现MVP/MVVM架构的开发

配合使用的有
Lifecycle：生命周期组件，可以将任意类的生命周期和Activity/Fragment绑定在一起
LiveData：配合前者，可在Activity/Fragment意外销毁时保存数据，例如旋转屏幕，其实就是个带有生命周期的数据容器
ViewModel：可以实现MVP/MVVM架构，配合前两者，数据不会丢失，并且只在onStart时观察数据的变化，onDestroy时自动解除数据观察的绑定


MVP架构

View：
视图层，即Activity/Fragment，通知Presenter层产生数据或执行逻辑，实现接口接受数据并展示

Presenter：
主持层，是个独立的个体，视图和逻辑、数据在这里结合
    1、调用Model层的接口，在这里获得数据
    2、通过观察者模式的回调，将数据传给View层
    3、View层接到数据做展示操作

Model：
数据层，这里的代码可以是获取数据的接口，例如：
    1、网络数据，Retrofit的httpservice接口
    2、数据库数据，Room或其他ORM框架的dao接口


MVVM架构
View：
    同上

View-Model：
    配合databind框架，实现数据-视图双向绑定。
    1、在该层（.java/.kt文件中）产生数据并用观察者模式的回调通知View层
    2、在布局.xml文件中指定接收的数据，数据将作为UI控件的属性，接到数据后自动设置给对应属性，
       同样在属性中可以指定方法名，在满足View的操作时自动调用对应方法。
       例如，给Button的onclick属性绑定方法，Button被点击时自动执行对应的方法

Model：
    同上


我对MVP和MVVM区别的思考：
MVP做了如下分离：视图分离了数据和业务逻辑
MVVM只对视图分离了数据，但和业务逻辑绑定


我对Android Architecture Components的ViewModel的实践——构建MVP架构
P层使用ViewModel类。
    既然叫ViewModel，即视图模型，根据面向对象思想，P层就是Activity/Fragment抽象出的具有一切和视图相关数据和业务逻辑的层，
    例如：文章列表 -> List<Data>，列表分页加载的页码 -> int，是否展示EmptyView -> boolean