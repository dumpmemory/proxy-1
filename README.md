# proxy
fq
pac.txt 配置的网址不经过代理服务器。
规则  cn，qq.com 结尾的网址不经过服务器

config.txt 配置为目标服务器地址
第一行 数字1 ，client启动时会连接文件中的第二行服务器。不能写0，因为0的话会从第一行读取服务器地址。


启动命令
nohup java -Dio.netty.leakDetectionLevel=paranoid -jar RProxyServer-1.0-SNAPSHOT.jar > proxy.log 2>&1 &


-Dio.netty.leakDetectionLevel=paranoid 可以去掉
