#!/bin/bash
bin=`dirname "$0"`
export bin=`cd "$bin"; pwd`
export datadir=$bin/../data
export data=$datadir/SogouQ.reduced

export generateddir=$datadir/generated
rm -rf $generateddir
mkdir $generateddir
echo genrateddir is $generateddir
i=1
while(($i<100))
do
  echo generate the $i file.
  sleep 8
  surfix=`date +%s`
  echo "cat $data > $generateddir/SogouQ.reduced.$surfix"
  cat $data > $generateddir/SogouQ.reduced.$surfix
  i=$(($i+1))
done

