#!/bin/sh

cd /root/master

sh install-hadoop-hbase.sh
sh config-system.sh
sh init_cluster.sh
sh start_hdfs.sh
