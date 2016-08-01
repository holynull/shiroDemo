# 在Spring MVC中使用Apache Shiro安全框架

我们在这里将对一个集成了Spring MVC+Hibernate+Apache Shiro的项目进行了一个简单说明。这个项目将展示如何在Spring MVC 中使用Apache Shiro来构建我们的安全框架。

[TOC]

阅读文章前，您需要做以下准备：

- Maven 3环境
- Mysql-5.6+
- JDK1.7+
- git环境
- git.oschina.net帐号
- Apache Tomcat 7+
- 您熟练掌握的编辑工具，推荐使用InterlliJ IDEA 14+

## 开始和[项目地址](http://git.oschina.net/holynyll/ShiroTest)

我将整个教程的源码共享到git.oschina.net上，地址：[源代码地址](http://git.oschina.net/holynyll/ShiroTest).所以请你在开始之前注册一个git.oschina.net的帐号，你可以把项目fork到你的账户下，然后再clone到你的本地。下面我们将对项目进行一个简单说明。

## 安全管理框架数据结构

首先，我们在mysql数据库中创建schema，命名为shirodemo。我们在创建两个用户`shiroDemo@localhost`和`shiroDemo@%`，这里我们将用户的密码简单设置成123456。

然后，我们将项目从git服务器上clone到本地后，我们可以在项目根目录下的resources中发现`db.sql`文件。这个文件是项目的数据库结构文件，你可以将db.sql导入到数据库shirodemo中。

我们这里的权限结构设计比较简单，我们以表格的形式说明主要数据库结构：

<center>**Table:t_user**</center>

Name|Type|Length|Describ
---|---|---|---
id|int|11|用户表的主键
password|varchar|255|密码
username|varchar|255|用户名，全局唯一，shiro将使用用户名来锁定安全数据中的用户数据。

<center>**Table:t_role**</center>

Name|Type|Length|Describ
---|---|---|---
id|int|11|主键
rolename|varchar|255|角色名称，全局唯一。shiro将通过角色名来进行鉴权

<center>**Table:t_permission**</center>

Name|Type|Length|Describ
---|---|---|---
id|int|11|主键
role_id|int|11|关联role的外键
dataDomain|varchar|255|系统数据模型的域（自己定义的概念，下面我们将会介绍）
dataType|varchar|255|permission对应的系统实例的类型
operation|varchar|255|permession许可的操作，例如add，del等等

<center>**Table:t_authc_map**</center>

Name|Type|Length|Describ
---|---|---|---
id|int|11|主键
authcType|varchar|255|验证类型，枚举：anon,authc,perms,roles
url|varchar|255|系统资源的url
val|varchar|255|具体的权限字符串，例如：user:query

t_user和t_role表就不用详细介绍了，就是系统的用户表和角色表。它与t_role角色表的关系是多对多的关系，即一个用户可以有多个角色，一个角色可以包含多个用户。

那么我们介绍一下t_permission表，这个表存放的数据是角色拥有的permission（这里我们就用shiro的permission概念，不翻译了。因为翻译过来是许可，但是许可二字还不能完全阐释permission的概念）。每一个role会对应一个permission，即一对一的关系。

表t_authc_map存储的是Shiro filter需要的配置数据，这些数据组合起来，定义了访问控制（Access Controll）的规则，即定义了哪些url可以被拥有哪些permission或者拥有哪些role的用户访问。在我们这个例子中，其实这张表用处不大。当初设计这样一张表的目的是，能够动态管理访问控制的规则，但是并不能。

***
提示：
访问控制规则的数据是在Spring bean初始化时就加载给了访问控制的filter。我们试想一下，在你的webapp运行时（runtime），我们可以通过一些手段来修改系统的访问控制规则，那么势必会造成用户提交事务时的处理变得非常复杂。例如，用户正在访问一个url连接，我们通过后台修改了url的访问控制权限，这时这个用户已经提交了一次事务操作，那么怎么判断这次提交是否合法呢？要把这个问题处理清楚就很复杂。那么你可能会问，如果我为系统增加了一个模块，模块中有一些新建的url需要提供给用户访问，但是我不想重启我的应用，直接在数据库中配置完成，怎么办？我想，既然增加了模块，当然需要重新部署，那么仅通过配置数据完成部署，我感觉在现在Spring MVC下，很难实现。所以个人看法，访问控制规则数据使用配置文件还是持久化到数据库，没有什么区别。但是本文中还是会介绍如何将访问控制规则持久化到数据库中。
***

## shiroTest模块

我们可以看见项目中有一个shiroTest模块，这个模块中主要实现在单元测试时，使用的通用程序。在本例中，我们在shiroTest模块中实现一个proxool数据源，为其他模块在单元测试时提供数据库连接。

请注意，我们这里配置的数据源，仅提供给单元测试使用。而我们的webapp中将使用Spring 的JNDI数据源。为什么这么做呢？主要原因是：本例中我们使用的是Tomcat做为中间件，但是实际项目的生产环境，可能使用商业中间件，例如Weblogic等等。那么我们在迁移过程中，就不用考虑中间件使用的是什么数据源，只去调用中间件JNDI上绑定的数据源名称就可以了。而且这些商业中间件一般都有很好的数据源管理功能。如果我们使用独立的数据源，那么数据源就脱离的中间件的管理，岂不是功能浪费？

我们在test中，实现一个测试用例，这个测试用例主要测试数据源的连接：

```
public void testApp() throws SQLException {
        ApplicationContext cxt = new ClassPathXmlApplicationContext(
                "classpath*:conf/*-beans.xml");
        DataSource ds= (DataSource) cxt.getBean("ds-default");
        Connection con=ds.getConnection();
        con.close();
        assertTrue(true);
}
```

我们在shiroTest项目根目录下运行`mvn test`,测试一下。

## base模块

base模块主要实现的是整个项目中，各个模块公用的程序。其中包含了：

- Hibernate Session Factory
- Ehcache
- POJO Class
- BaseDao 所有dao的父类
- Hibernte 事务管理

## authmgr模块

authmgr模块实现了如下功能

- 登录
- 登出
- 查询访问控制规则数据
- 实现自定义Realm
- 实现Shiro的SecurityManager

### authmgr模块业务接口

我们来看一下接口com.ultimatech.shirodemo.authmgr.service.IAuthService

<center>*com.ultimatech.shirodemo.authmgr.service.IAuthService*</center>

```
public interface IAuthService {
    /**
     * 用户登录接口
     * @param userName 登录用户名
     * @param password 密码
     * @throws AuthenticationException
     */
    void logIn(String userName, String password) throws AuthenticationException;

    /**
     * 用户登出系统
     */
    void logOut();

    /**
     * 获得数据库中存储的访问控制数据
     * @return
     */
    List<AuthcMap> getFilterChainDefinitions();
}
```

### 自定义实现Realm

我们来看一下我们的Realm是如何实现：

<center>*com.ultimatech.shirodemo.authmgr.realm.MyRealm*</center>

```
......
@Component("myRealm")
public class MyRealm extends AuthorizingRealm {

    @Autowired
    public MyRealm(@Qualifier("shiroEncacheManager") CacheManager cacheManager) {
        super(cacheManager);
    }

    @Autowired
    private IAuthDao dao;

   ......

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //获取登录时输入的用户名
        String loginName = (String) principalCollection.fromRealm(getName()).iterator().next();
        //到数据库查是否有此对象
        User user = this.getDao().findByName(loginName);
        if (user != null) {
            //权限信息对象info,用来存放查出的用户的所有的角色（role）及权限（permission）
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            //用户的角色集合
            info.setRoles(user.getRolesName());
            //用户的角色对应的所有权限，如果只使用角色定义访问权限，下面的四行可以不要
            List<Role> roleList = user.getRoleList();
            for (Role role : roleList) {
                info.addStringPermissions(role.getPermissionsString());
            }
            return info;
        }
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //UsernamePasswordToken对象用来存放提交的登录信息
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        //查出是否有此用户
        User user = this.getDao().findByName(token.getUsername());
        if (user != null) {
            //若存在，将此用户存放到登录认证info中
            return new SimpleAuthenticationInfo(user.getUsername(), user.getPassword(), getName());
        }
        return null;
    }
}

```

### Shiro的SecurityManager

我们在Spring 容器中声明一个名叫securityManager的bean。在resources/conf/authmgr-beans.xml中,我们看见如下代码：

```
<bean id="securityManager" class="org.apache.shiro.web.mgt.DefaultWebSecurityManager">
    <!-- ref对应我们写的realm  myRealm -->
    <property name="realm" ref="myRealm"/>
    <!-- 使用下面配置的缓存管理器 -->
    <property name="cacheManager" ref="shiroEncacheManager"/>
</bean>
<bean id="lifecycleBeanPostProcessor" class="org.apache.shiro.spring.LifecycleBeanPostProcessor"/>
```

由于我们很多模块都会用到共享缓存，所以以上`<property name="cacheManager" ref="shiroEncacheManager"/>`中的`shiroEncacheManager`被定义在base模块中。

我们可以去base模块的目录下找到resources/conf/base-beans.xml，找到如下代码：

```
<bean id="shiroEncacheManager" class="org.apache.shiro.cache.ehcache.EhCacheManager">
    <property name="cacheManager" ref="ehCacheManager"/>
</bean>

<bean id="ehCacheManager" class="org.springframework.cache.ehcache.EhCacheManagerFactoryBean">
    <property name="configLocation" value="classpath:ehcache.xml"></property>
    <property name="shared" value="true"/>
</bean>
```

## shiroWebapp模块

shiroWebapp模块是本例中的web应用。主要集成了Spring MVC框架、Hibernate框架，以及我们的安全框架Apache Shiro。

我们使用Shiro Filter来进行访问控制，那么在web.xml文件中进行了如下配置：


```
<filter>
    <filter-name>shiroFilter</filter-name>
    <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
</filter>
<filter-mapping>
    <filter-name>shiroFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

我们使用Spring的DelegatingFilterProxy来创建Shiro Filter。`<filter-name>shiroFilter</filter-name>`这个参数要与Spring中Shiro Filter Bean的名字保持一致。在shiroWebapp下的resources/conf下的web-beans.xml文件中，我们可以看见Shiro Filter的配置：

```
<bean id="filterChainDefinitions" class="com.ultimatech.shirodemo.web.filter.ShiroFilterChainDefinitions">
    <property name="filterChainDefinitions">
        <value>
            /html/**=anon
            /js/**=anon
            /css/**=anon
            /images/**=anon
            /authc/login=anon
            /login=anon
            <!--/user=perms[user:del]-->
            /user/add=roles[manager]
            /user/del/**=roles[admin]
            /user/edit/**=roles[manager]
            <!--/** = authc-->
        </value>
    </property>
</bean>

<!-- 配置shiro的过滤器工厂类，id- shiroFilter要和我们在web.xml中配置的过滤器一致 -->
<bean id="shiroFilter" class="org.apache.shiro.spring.web.ShiroFilterFactoryBean">
    <!-- 调用我们配置的权限管理器 -->
    <property name="securityManager" ref="securityManager"/>
    <!-- 配置我们的登录请求地址 -->
    <property name="loginUrl" value="/"/>
    <!-- 配置我们在登录页登录成功后的跳转地址，如果你访问的是非/login地址，则跳到您访问的地址 -->
    <property name="successUrl" value="/user"/>
    <!-- 如果您请求的资源不再您的权限范围，则跳转到/403请求地址 -->
    <property name="unauthorizedUrl" value="/html/403.html"/>
    <!-- 权限配置 -->
    <property name="filterChainDefinitionMap" ref="filterChainDefinitions" />
</bean>
```

### 访问控制数据

我们看见上面的`filterChainDefinitions`中，我们自定义了一个FacotryBean，这个bean主要实现将配置文件中的访问控制数据和数据库中的访问控制数据整合在一起。（虽然我们之前已经说了，这两种方式没什么区别。）

<center>*com.ultimatech.shirodemo.web.filter.ShiroFilterChainDefinitions*</center>

```
public class ShiroFilterChainDefinitions implements FactoryBean<Ini.Section> {

    @Autowired
    private IAuthService authService;

    ......

    public static final String PREMISSION_STRING = "perms[{0}]";

    public static final String ROLE_STRING = "roles[{0}]";

    public Ini.Section getObject() throws Exception {
        List<AuthcMap> list = this.getAuthService().getFilterChainDefinitions();
        Ini ini = new Ini();
        ini.load(this.getFilterChainDefinitions());
        Ini.Section section = ini.getSection(Ini.DEFAULT_SECTION_NAME);
        for (AuthcMap map : list) {
            String s = null;
            switch (AuthcType.valueOf(map.getAuthcType())) {
                case roles:
                    s = MessageFormat.format(ROLE_STRING, map.getVal());
                    break;
                case perms:
                    s = MessageFormat.format(PREMISSION_STRING, map.getVal());
                    break;
                case authc:
                    s = AuthcType.authc.name();
                case anon:
                    s = AuthcType.anon.name();
                default:
                    s = AuthcType.authc.name();
            }
            section.put(map.getUrl(), s);
        }
        return section;
    }

   ......
}

```

关于访问控制数据，我们要注意Shiro Filter在执行访问控制时，是按访问控制数据的顺序来逐个验证的，而我们将数据库中的访问控制数据追加到配置文件的后面。例如上面的配置：

```
<bean id="filterChainDefinitions" class="com.ultimatech.shirodemo.web.filter.ShiroFilterChainDefinitions">
    <property name="filterChainDefinitions">
        <value>
            /html/**=anon
            /js/**=anon
            /css/**=anon
            /images/**=anon
            /authc/login=anon
            /login=anon
            /user/add=roles[manager]
            /user/del/**=roles[admin]
            /user/edit/**=roles[manager]
        </value>
    </property>
</bean>
```

追加上我们在数据库中的访问配置数据：

id|authcType|url|val
---|---|---|---
1  | perms|/user|user:query
2  |authc |/**  |&nbsp;

那么全部访问控制数据应该是：

```
/html/**=anon
/js/**=anon
/css/**=anon
/images/**=anon
/authc/login=anon
/login=anon
/user/add=roles[manager]
/user/del/**=roles[admin]
/user/edit/**=roles[manager]
/user=perms[user:query]
/**=authc
```

这里我们要强调一下，如果把基于角色和基于permission的控制放在`/**=authc`之后的控制是不会起作用的。例如：

```
/html/**=anon
/js/**=anon
/css/**=anon
/images/**=anon
/authc/login=anon
/login=anon
/user/add=roles[manager]
/user/del/**=roles[admin]
/user/edit/**=roles[manager]
/**=authc
/user=perms[user:query]
```

这时如果用户rose拥有`user:del`的permission，在他登录系统以后也是可以访问`/user`路径的。其实我们想实现只有拥有`user:query`的用户才能访问`/user`，而rose拥有的是`user:del`。我们设置了`/user=perms[user:query]`，而rose并没有`user:query`这样的permission，为什么rose还能访问`/user`呢？这是因为，Shior Filter先使用访问控制规则`/**=authc`对rose的权限进行了验证，那么rose是一个已知身份的用户，所以他可以访问所有url，除了`/**=authc`之前设置的规则限制不能访问的url。

是不是很混乱，一部分访问控制规则在配置文件中，一部分又在数据库中，而且访问控制还有顺序要求，一旦我们忽略任意一部分访问控制数据，我们的设置就很难达到我们预期的效果。所以，将访问控制数据分开并不是一个好的实践。

我们这里实现了使用数据库配置访问控制数据，仅仅是为了开阔一下思路，并不推荐同时使用数据库配置和配置文件配置。

### 关于permission语法

我们可以参考[Understanding Permissions in Apache Shiro](http://shiro.apache.org/permissions.html)。你可能会发现好长的一篇文章啊！

那么下面我就我个人的理解，简单对permission语法说明一下。

在我们设计系统时，我们经过一系列的分析过程，会得到我们要实现的系统中存在哪些实体。例如，系统中存在用户（user），工作流（workflow）等实体。我们对这些实体进行抽象化，构成我们系统的基础模型。那么实体除了数据属性，例如，用户名（username），流程名称（flowname）等，还具备一些功能（function）或者叫做方法（method）的特征。我们使用OOP的思想来设计系统，那么我们抽象出来的实体就是我们所说的实体类，这些实体类代表了很多实体对象，例如，user类中，实际包含了tom,jack和rose，这些用户的一个子集就组成了一个数据域。那么我们的permission就是由系统实体和功能，以及一个数据域组成，格式就像这样：实体:功能:数据域。

例如：
perms[user:query:\*]——表示允许查询（query）用户（user实体类）所有（\*）对象的许可。
perms[user:query,add,del,update]——表示允许查询（query）、添加（add）、删除（del）和更新（update）用户（user）所有对象的许可。
perms[user:query:jack,rose]——表示允许查询（query）用户（user）中jack和rose数据的许可。
perms[workflow:approve:order]——表示允许操作工作流程(workflow)中一个名叫order的流程的审批许可。

## 试验

我们掌握了项目中集成Apache Shiro的方法后，将项目在IDEA中run或者debug起来。使用tom、jack和rose用户登录系统中，看看会有什么现象。当然用户、角色和permission数据都在db.sql文件中，导入数据库时已经一起导入了。你可以修改这些数据，以及他们之间的关系来体验Shiro的安全框架。

## 总结

希望通过这个说明，能够让你了解在本项目中是如何集成Apache Shiro安全框架的。

在接下来的计划中，我们将实现在集群环境中使用Apache Shiro。