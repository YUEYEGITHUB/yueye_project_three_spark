#!/bin/sh

bin=`dirname "$0"`
export bin=`cd "$bin"; pwd`

sh $bin/change_DX_conf.sh

for host in `cat /root/master/h.slaves`; do
  ssh $host wget http://34.0.0.16/hosts -O /etc/hosts
  ssh $host wget http://DX2/cloudera-cdh5.repo -O /etc/yum.repos.d/cloudera-cdh5.repo
  /usr/sbin/ntpdate 133.100.11.8
  ssh $host wget http://DX2/ntpsync -O /etc/cron.hourly/ntpsync
  chmod +x /etc/cron.hourly/ntpsync
done
