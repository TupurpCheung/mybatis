#### git使用
```
从远端克隆代码： git clone url
拉取代码： git pull
查看文件状态： git status
文件修改添加到暂存区： git add 
提交修改到本地仓库： git commit -m ''
推送本地仓库变更到远端仓库： git push
查看本地分支列表： git branch
查看远端分支列表： git branch -r
从远端拉取指定分支到本地：git checkout -b 分支名 origin/分支名
合并分支：
    先切换到目标分支： git checkout 目标分支（代码将被合并到此分支）
    将源分支代码合并到当前分支： git merge 代码源分支
从指定分支新建分支： git checkout -b 新分支 旧分支
    相当于（创建分支+切换分支）git branch 分支名 + git checkout 分支名
本地新建的分支推送到远端（远端不存在本地的这个分支）： git push -u origin 分支名
查看本地分支与远端分支的绑定关系： git branch -vv
```

#### 一、version-1（理解代理）
参见：https://blog.csdn.net/dfBeautifulLive/article/details/124924441
```
1、通过 MapperRegistry 创建接口的代理工厂
2、创建SqlSessionFactory，把MapperRegistry传来传去，创建 SqlSession时，也传给了 SqlSession
3、SqlSession 获取 mapper 时，通过MapperProxyFactory 创建 mapper的代理（本版本是执行sqlSession自身的selectOne方法）
4、真正执行的是sqlSession的方法
```


#### 二、version-2（自定义xsd，读取xml配置）

#### 三、version-3（引入H2内存数据库，真实读取数据库）


#### 四、version-4（数据源池化处理实现）
参见：https://blog.csdn.net/dfBeautifulLive/article/details/127361278