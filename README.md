

#### 一、version-1（理解代理）
参见：https://blog.csdn.net/dfBeautifulLive/article/details/124924441
```
1、通过 MapperRegistry 创建接口的代理工厂
2、创建SqlSessionFactory，把MapperRegistry传来传去，创建 SqlSession时，也传给了 SqlSession
3、SqlSession 获取 mapper 时，通过MapperProxyFactory 创建 mapper的代理（本版本是执行sqlSession自身的selectOne方法）
4、真正执行的是sqlSession的方法
```
