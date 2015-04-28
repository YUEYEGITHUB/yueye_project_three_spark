ls /mnt | fgrep -v lost+found | awk '{system("rm -rf /mnt/"$1)}'
rm -rf /var/lib/zookeeper
