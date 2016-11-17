#!/bin/bash

func_rep="func-rep.html"
perf_rep="perf-rep.html"
db_name="tigasedb"
db_user="root"
db_pass="123"
root_user="root"
root_pass="123"

TESTS=("derby" "mysql" "pgsql" "mssql")
IPS=("127.0.0.1" "192.168.72.130" "127.0.0.1" "127.0.0.1")

server_timeout=1000

server_dir="D:/work/OtherProjects/tigaseTestuite/tigase-server-7.0.4-b3844"
#database="derby"
database="mysql"
server_ip="192.168.72.130"

MS_MEM=100
MX_MEM=1000

SMALL_MS_MEM=10
SMALL_MX_MEM=50

