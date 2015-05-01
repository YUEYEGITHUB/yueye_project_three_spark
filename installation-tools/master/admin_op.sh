#!/bin/sh

if [ $# = 0 ]; then
    echo " please input user id ..."
    exit
fi

bin=`dirname "$0"`
userid="$1"
/bin/hostname $userid-1
export bin=`cd "$bin"; pwd`
prefix=$userid
export remote_ip=112.74.102.117
sh $bin/change_DX_conf.sh $prefix
sh $bin/init_local_hosts.sh
sh $bin/config_ssh.sh
sh $bin/config-hostname.sh

for host in `cat $bin/h.slaves`; do
  ssh $host wget http://$remote_ip/hosts -O /etc/hosts.new
  ssh $host mv /etc/hosts.new /etc/hosts
  ssh $host wget http://DX2/cloudera-cdh5.repo -O /etc/yum.repos.d/cloudera-cdh5.repo
  /usr/sbin/ntpdate 133.100.11.8
  ssh $host wget http://DX2/ntpsync -O /etc/cron.hourly/ntpsync
  chmod +x /etc/cron.hourly/ntpsync
  ssh $host 'echo "export PATH=\$PATH:/usr/java/jdk1.7.0_45-cloudera/bin" >> /etc/profile'
done
