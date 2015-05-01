#!/bin/sh

#hostname=`/bin/hostname`
#prefix=`echo $hostname | awk -F "-" '{print $1}'`
export prefix=$1
sed -i "s/DX3/$prefix/g" $bin/*
