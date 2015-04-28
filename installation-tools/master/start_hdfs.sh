#!/bin/sh

service hadoop-hdfs-namenode start
for host in `cat h.slaves`; do
  ssh $host service hadoop-hdfs-datanode start
done
