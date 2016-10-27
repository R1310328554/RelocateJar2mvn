# RelocateJar2mvn
Relocate the jars in local file system to local maven repositories

## 本项目的主要目的在于将 我们电脑上的分散各地的 非 maven管理的 jar ， 按照maven的 目录结构 ， 批量的 快速的 放置到本机的 maven 仓库中去。  以免直接去 maven  远程仓库下载， 因为下载通常很慢。。。  

注， 我们要做的不是将本地jar install 到本地maven中去。 我们需要处理的， 简单的说： 将电脑上非 maven管理的 jar 由maven管理。


###接下来需要处理的事情：

1 获取本机（当前使用的windows 7） 上的所有jar 列表 ， —— 除去已经在mvn repo 中的 ， 即已经使用mvn 管理的 jar 外，  
2 提供处理多线程功能， 以加快速度
3 如果某些jar 没有提供 pom 信息， 怎么办？ 需要重新到mvn 获取pom 文件， 然后下载本地， 最后移到 mvn 仓库中去
4 .. 
