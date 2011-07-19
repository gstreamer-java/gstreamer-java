#!/bin/bash
# regenerate gstreamer-lib-function-list.txt

cd /usr/lib
for i in `find . -maxdepth 1 -name 'libgst*' -a -type f|sort`; do
	echo "================= $i ================="
	nm -D -C $i|grep T|cut -f3 -d' '
done
