#!/bin/sh

cd /root/master
sh init_hdfs.sh
sh start_mapred1.sh
sh start_hbase.sh
