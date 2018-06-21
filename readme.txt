用于获取莱士电表数据

MeterUtils -> 封装静态获取token的方法，封装接口调用返回数据的方法

MeterService -> 所有业务

数据获取速率可通过config.properties自定义，最少为1分钟一次