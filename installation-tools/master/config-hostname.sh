#!/bin/bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
for host in `cat $bin/h.slaves`; do
  ssh $host hostname $host
  ssh $host 'sed -i -r "s/(HOSTNAME *= *).*/\1'$host'/" /etc/sysconfig/network'
done
