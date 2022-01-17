# 从HAR（HTTP存档格式)文件中提取URL

## usage

```
mvn clean compile assembly:single

java -jar target/extract-urls-from-har-v1.0.jar -h

usage: help info
 -h,--help        The command help
 -i,--in <arg>    Location of HAR file.
 -j,--jmx         Output JMX format.
 -o,--out <arg>   Location of urls file.

```
