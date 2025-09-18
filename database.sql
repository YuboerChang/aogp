-- #建立数据库
create database aogp character set utf8 collate utf8_general_ci;
 
-- #建立数据库的用户
create user aogp@localhost identified by '123456';
grant all privileges on aogp.* to aogp@localhost;
flush privileges;

-- 创建价格表
-- 日期
-- 字符串存储即可，不用精确时间，也符合查询数据格式。
-- 当日开盘价
-- decimal字段，方便金融类数据操作，黄金价格一般为xxx.xx，即三位整数、小数点后两位
-- 当日收盘价
-- 当日最高价
-- 当日最低价
-- 黄金种类
-- 这个暂时不做，以后需要再扩展，目前只操作Au99.99
drop table if exists gold_price;
 
create table gold_price (
    date varchar(10) primary key,
    open decimal(5,2) not null,
    close decimal(5,2) not null,
    lowest decimal(5,2) not null,
    highest decimal(5,2) not null
) character set = utf8 collate = utf8_general_ci comment = '黄金价格' row_format = dynamic;