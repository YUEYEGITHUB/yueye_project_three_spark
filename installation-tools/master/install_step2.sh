#!/bin/sh

cd /root/master
sh init_hdfs.sh
sh start_yarn.sh
sh start_hbase.sh
