#!/bin/bash
bin=`dirname "$0"`
export bin=`cd "$bin"; pwd`
export datadir=$bin/../data
export data=$datadir/SogouQ.reduced

export generateddir=$datadir/generated
rm -rf $generateddir/*
mkdir $generateddir
echo genrateddir is $generateddir
i=0
surfix="20150601"
while(($i<2))
do
  echo generate the $i file.
  sleep 5
  echo "cat $data > $generateddir/SogouQ.reduced.$surfix"
  cat $data > $generateddir/SogouQ.reduced.$surfix
  second=`date -d "$surfix" +%s`
  second=$(($second+86400))
  surfix=`date -d @"$second" +%Y%m%d`
  i=$(($i+1))
done

