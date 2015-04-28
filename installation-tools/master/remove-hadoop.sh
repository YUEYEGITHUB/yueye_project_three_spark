#!/bin/sh

cd /root/master

for host in `cat h.slaves`; do
 ssh $host yum erase zookeeper hadoop hbase  -y
 rsync remove-hadoop-file.sh $host:/root
 ssh $host sh /root/remove-hadoop-file.sh
 ssh $host rm -rf /root/remove-hadoop-file.sh
done
