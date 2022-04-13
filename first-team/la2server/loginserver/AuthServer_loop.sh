#!/bin/bash

while :;
do
	java -server -Dfile.encoding=UTF-8 -Xms64m -Xmx64m -cp config/xml:../serverslibs/*: l2ft.loginserver.AuthServer > log/stdout.log 2>&1

	[ $? -ne 2 ] && break
	sleep 10;
done
