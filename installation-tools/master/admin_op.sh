#!/bin/sh

bin=`dirname "$0"`
export bin=`cd "$bin"; pwd`

sh $bin/change_DX_conf.sh

for host in `cat /root/master/h.slaves`; do
  ssh $host wget http://10.110.64.132/hosts -O /etc/hosts.new
  ssh $host mv /etc/hosts.new /etc/hosts
  ssh $host wget http://DX2/cloudera-cdh5.repo -O /etc/yum.repos.d/cloudera-cdh5.repo
  /usr/sbin/ntpdate 133.100.11.8
  ssh $host wget http://DX2/ntpsync -O /etc/cron.hourly/ntpsync
  chmod +x /etc/cron.hourly/ntpsync
  ssh $host 'echo "export PATH=\$PATH:/usr/java/jdk1.7.0_45-cloudera/bin" >> /etc/profile'
done
