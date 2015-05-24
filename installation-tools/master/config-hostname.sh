#!/bin/bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
i=0;
for host in `cat $bin/h.slaves`; do
  i=`expr $i + 1`;
  name=$prefix-$i
  ssh $host hostname $name
  ssh $host 'sed -i -r "s/(HOSTNAME *= *).*/\1'$name'/" /etc/sysconfig/network'
done
