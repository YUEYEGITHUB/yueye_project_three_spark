#!/bin/sh

if [ $# = 0 ]; then
    echo " please input user id ..."
    exit
fi

bin=`dirname "$0"`
userid="$1"
/bin/hostname $userid-1
export bin=`cd "$bin"; pwd`
export prefix=$userid
export remote_ip=10.116.61.247
sh $bin/change_DX_conf.sh
sh $bin/init_local_hosts.sh
sh $bin/config_ssh.sh
sh $bin/config-hostname.sh

wget http://$remote_ip/hosts -O /etc/hosts.new
mv /etc/hosts.new /etc/hosts
i=0
for host in `cat $bin/h.slaves`; do
  i=`expr $i + 1`;
  echo $host $userid-$i >> /etc/hosts
  ssh $host wget http://$remote_ip/cloudera-cdh5-myself.repo -O /etc/yum.repos.d/cloudera-cdh5-myself.repo
  /usr/sbin/ntpdate 133.100.11.8
  ssh $host wget http://$remote_ip/ntpsync -O /etc/cron.hourly/ntpsync
  chmod +x /etc/cron.hourly/ntpsync
  ssh $host 'echo "export PATH=\$PATH:/usr/java/jdk1.7.0_45-cloudera/bin" >> /etc/profile'
done

for host in `cat $bin/h.slaves`; do
  rsync /etc/hosts $host:/etc/
done
