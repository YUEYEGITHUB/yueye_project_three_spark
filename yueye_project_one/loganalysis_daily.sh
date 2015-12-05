#!/bin/bash
day=`date --date="$1" +%Y%m%d`
month=`date --date="$1" +%Y%m`

echo $day
echo $month
#每天为日表增加分区，并将每天的数据导入日表
echo "正在创建Hive日表分区，并导入数据..."
hive -e "ALTER TABLE loganalysis_day ADD  IF NOT EXISTS PARTITION(day='${day}');" 
hive -e "LOAD DATA INPATH '/kafka/${day}' INTO TABLE loganalysis_day PARTITION(day=${day});"

#如果月表分区不存在，则增加分区，将每天的数据导入月表
echo "正在导入Hive月表数据..."
hive -e "ALTER TABLE loganalysis_month ADD IF NOT EXISTS PARTITION(month='${month}');"
hive -e "insert into table loganalysis_month partition (month='${month}') select time,userid,query,pagerank,clickrank,site from loganalysis_day where day='${day}';"

#创建mysql日表
echo "正在创建mysql日表和月表"
mysql -uroot -p989711 -e " use loganalysis;
	CREATE TABLE IF NOT EXISTS loganalysis_${day}(
		time varchar(8),#访问时间
		userid varchar(36),#用户ID
		query text,#查询词
		pagerank int,#该URL在返回结果中的排名
		clickrank int,#用户点击的顺序号
		site text#用户点击的URL
	);"

#创建mysql月表
mysql -uroot -p989711 -e "use loganalysis;
	CREATE TABLE IF NOT EXISTS loganalysis_${month}(
		time varchar(8),#访问时间
		userid varchar(36),#用户ID
		query text,#查询词
		pagerank int,#该URL在返回结果中的排名
		clickrank int,#用户点击的顺序号
		site text#用户点击的URL
	);"
#Hive导入mysql，日表
echo "正在从Hive向mysql导入数据..."
sqoop export --connect jdbc:mysql://hadoop1:3306/loganalysis?characterEncoding=utf-8 --username root --password 989711 --table loganalysis_${day} --fields-terminated-by '\t' --export-dir /user/hive/warehouse/loganalysis_day/day=${day}  

#Hive导入mysql，月表
sqoop export --connect jdbc:mysql://hadoop1:3306/loganalysis?characterEncoding=utf-8 --username root --password 989711 --table loganalysis_${month} --fields-terminated-by '\t' --export-dir /user/hive/warehouse/loganalysis_month/month=${month}  


