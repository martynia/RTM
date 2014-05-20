#!/bin/bash
#
# command line options:
#   -Drtm.noAbout - no About screen at startup
#   -DrtmConfig=<path>  - new WinIni style config file location
#
# There seems to be a bug in java 1.7 which stops MacOS X full screen mode from working properly
# It should not be a problem since current Macs (ML) come with java 1.6
# If you _manually_ updated java to 1.7 on your Mac, as I did, use the full path the java 1.6 
#
#export JAVA=/System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Home/bin/java
export JAVA=java
$JAVA -Xmx400m -Dsun.java2d.noddraw=true -Djava.library.path="lib" -DrtmConfig=`pwd`/ini_config.cnf -Drtm.noAbout -jar dist/rtm_3D_2009.jar

# enjoy !