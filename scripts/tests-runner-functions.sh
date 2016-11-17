#!/bin/bash
##
##  Tigase XMPP/Jabber Test Suite
##  Copyright (C) 2004-2009 "Artur Hefczyc" <artur.hefczyc@tigase.org>
##
##  This program is free software: you can redistribute it and/or modify
##  it under the terms of the GNU General Public License as published by
##  the Free Software Foundation, either version 3 of the License.
##
##  This program is distributed in the hope that it will be useful,
##  but WITHOUT ANY WARRANTY; without even the implied warranty of
##  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
##  GNU General Public License for more details.
##
##  You should have received a copy of the GNU General Public License
##  along with this program. Look for COPYING file in the top folder.
##  If not, see http://www.gnu.org/licenses/.
##
##  $Rev: $
##  Last modified by $Author: $
##  $Date: $
##

# This file contains functions definition used
# in all other scripts.

#_classpath="jars/tigase-testsuite.jar:libs/tigase-utils.jar:libs/tigase-xmltools.jar"
_classpath="jars\tigase-testsuite.jar;jars\tigase-utils.jar;jars\tigase-xmltools.jar"
_properties="-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -Dcom.sun.management.jmxremote"
#_options=" -agentlib:yjpagent -server -Xmx400M"
_options=" -server -Xmx600M"
JAVA_HOME="C:/Java/jdk1.8.0_91"
SKIP_SERVER_START="true"
SKIP_DB_RELOAD="true"

function db_reload_mysql() {

	[[ -z ${1} ]] && local _src_dir="${server_dir}" || local _src_dir=${1}
	[[ -z ${2} ]] && local _db_name="${db_name}" || local _db_name=${2}
	[[ -z ${3} ]] && local _db_user="${db_user}" || local _db_user=${3}
	[[ -z ${4} ]] && local _db_pass="${db_pass}" || local _db_pass=${4}
	[[ ! -z ${root_user} ]] && local _root_user="${root_user}" || local _root_user=${3}
	[[ ! -z ${root_pass} ]] && local _root_pass="${root_pass}" || local _root_pass=${4}

	mysqladmin -u ${_root_user} -p${_root_pass} -f drop ${_db_name}

	tts_dir=`pwd`
	cd ${_src_dir}

	./scripts/db-create-mysql.sh -y ${_db_user} ${_db_pass} ${_db_name} ${_root_user} ${_root_pass} localhost

	# load PubSub 3.2.0 schema
	java -cp "jars/*" tigase.util.DBSchemaLoader -logLevel ALL -dbType mysql -dbName ${_db_name} -dbHostname localhost -dbUser ${_db_user} -dbPass ${_db_pass} -rootUser ${_root_user} -rootPass ${_root_pass} -file database/mysql-pubsub-schema-3.2.0.sql
	
	cd ${tts_dir}

}

function db_reload_mongodb() {

	[[ -z ${2} ]] && local _db_name="${db_name}" || local _db_name=${2}

	mongo ${_db_name} --eval "printjson(db.dropDatabase())"

}

function db_reload_pgsql() {

	[[ -z ${1} ]] && local _src_dir="${server_dir}" || local _src_dir=${1}
	[[ -z ${2} ]] && local _db_name="${db_name}" || local _db_name=${2}
	[[ -z ${3} ]] && local _db_user="${db_user}" || local _db_user=${3}
	[[ -z ${4} ]] && local _db_pass="${db_pass}" || local _db_pass=${4}
	[[ ! -z ${root_user} ]] && local _root_user="${root_user}" || local _root_user=${3}
	[[ ! -z ${root_pass} ]] && local _root_pass="${root_pass}" || local _root_pass=${4}

	dropdb -U ${_db_user} ${_db_name}

	tts_dir=`pwd`
	cd ${_src_dir}

	java -cp "jars/*" tigase.util.DBSchemaLoader -logLevel ALL -dbType postgresql -dbName ${_db_name} -dbHostname localhost -dbUser ${_db_user} -dbPass ${_db_pass} -rootUser ${_root_user} -rootPass ${_root_pass}

	# load PubSub 3.2.0 schema
	java -cp "jars/*" tigase.util.DBSchemaLoader -logLevel ALL -dbType postgresql -dbName ${_db_name} -dbHostname localhost -dbUser ${_db_user} -dbPass ${_db_pass} -rootUser ${_root_user} -rootPass ${_root_pass} -file database/postgresql-pubsub-schema-3.2.0.sql

	# apply permissions to pubsub schema
	java -cp "jars/*" tigase.util.DBSchemaLoader -logLevel ALL -dbType postgresql -dbName ${_db_name} -dbHostname localhost -dbUser ${_db_user} -dbPass ${_db_pass} -rootUser ${_root_user} -rootPass ${_root_pass} -file database/postgresql-installer-post.sql

	cd ${tts_dir}

}

function db_reload_sqlserver() {

	[[ -z ${1} ]] && local _src_dir="${server_dir}" || local _src_dir=${1}
	[[ -z ${2} ]] && local _db_name="${db_name}" || local _db_name=${2}
	[[ -z ${3} ]] && local _db_user="${db_user}" || local _db_user=${3}
	[[ -z ${4} ]] && local _db_pass="${db_pass}" || local _db_pass=${4}
	[[ ! -z ${root_user} ]] && local _root_user="${root_user}" || local _root_user=${3}
	[[ ! -z ${root_pass} ]] && local _root_pass="${root_pass}" || local _root_pass=${4}


	tts_dir=`pwd`
	cd ${_src_dir}
	# drop old database
	
	java -cp "jars/*" tigase.util.DBSchemaLoader -logLevel ALL -dbType sqlserver -dbName ${_db_name} -dbHostname sqlserverhost -dbUser ${_db_user} -dbPass ${_db_pass} -rootUser ${_root_user} -rootPass ${_root_pass}  -query "drop database ${_db_name}"

	# create new database
	java -cp "jars/*" tigase.util.DBSchemaLoader -logLevel ALL -dbType sqlserver -dbName ${_db_name} -dbHostname sqlserverhost -dbUser ${_db_user} -dbPass ${_db_pass} -rootUser ${_root_user}  -rootPass ${_root_pass}
	
	# load PubSub 3.0.0 schema	
	java -cp "jars/*" tigase.util.DBSchemaLoader -logLevel ALL -dbType sqlserver -dbName ${_db_name} -dbHostname sqlserverhost -dbUser ${_db_user} -dbPass ${_db_pass} -rootUser ${_root_user}  -rootPass ${_root_pass} -file database/sqlserver-pubsub-schema-3.0.0.sql
	
	# load PubSub 3.1.0 schema	
	java -cp "jars/*" tigase.util.DBSchemaLoader -logLevel ALL -dbType sqlserver -dbName ${_db_name} -dbHostname sqlserverhost -dbUser ${_db_user} -dbPass ${_db_pass} -rootUser ${_root_user}  -rootPass ${_root_pass} -file database/sqlserver-pubsub-schema-3.1.0.sql

	# load PubSub 3.2.0 schema
	java -cp "jars/*" tigase.util.DBSchemaLoader -logLevel ALL -dbType sqlserver -dbName ${_db_name} -dbHostname sqlserverhost -dbUser ${_db_user} -dbPass ${_db_pass} -rootUser ${_root_user}  -rootPass ${_root_pass} -file database/sqlserver-pubsub-schema-3.2.0.sql

	cd ${tts_dir}

}

function db_reload_derby() {

	[[ -z ${1} ]] && local _src_dir="${server_dir}" || local _src_dir=${1}
	[[ -z ${2} ]] && local _db_name="${db_name}" || local _db_name=${2}

	if [ -z ${_db_name} ] ; then 
		echo "No DB name set - Stopping - This would cause the attempt to delete /"
		exit 1
	fi

	rm -fr ${_db_name}/
	tts_dir=`pwd`

	cd ${_src_dir}

	java -cp "jars/*" tigase.util.DBSchemaLoader -logLevel ALL -dbType derby -dbName ${tts_dir}/${_db_name} -dbHostname localhost -dbUser ${_db_user} -dbPass ${_db_pass} -rootUser ${_root_user} -rootPass ${_root_pass}
	
	# load PubSub 3.2.0 schema
	java -cp "jars/*" tigase.util.DBSchemaLoader -logLevel ALL -dbType derby -dbName ${tts_dir}/${_db_name} -dbHostname localhost -dbUser ${_db_user} -dbPass ${_db_pass} -rootUser ${_root_user} -rootPass ${_root_pass} -file database/derby-pubsub-schema-3.2.0.sql

	cd ${tts_dir}

}

function fs_prepare_files() {
	rm -f etc/*.xml
	#cp -f ${server_dir}/database/* database/
}

function tig_start_server() {

	[[ -z ${1} ]] && local _src_dir="../server" || local _src_dir=${1}
	[[ -z ${2} ]] && local _config_file="etc/tigase-mysql.conf" \
		|| local _config_file=${2}

	rm -f ${_src_dir}/logs/tigase.pid
	#killall java
	sleep 2
	${_src_dir}/scripts/tigase.sh start ${_config_file}

}

function tig_stop_server() {

	[[ -z ${1} ]] && local _src_dir="../server" || local _src_dir=${1}
	[[ -z ${2} ]] && local _config_file="etc/tigase-mysql.conf" \
		|| local _config_file=${2}

	${_src_dir}/scripts/tigase.sh stop ${_config_file}
	sleep 2
	#killall java

}

function ts_start() {

	[[ -z ${1} ]] && local _test_script="scripts/all-xmpp-tests.xmpt" \
		|| local _test_script=${1}
	[[ -z ${2} ]] && local _server_ip="" || local _server_ip="-serverip ${2}"
	[[ -z ${3} ]] && local _output_file="" \
		|| local _output_file="-output-file ${3}"
	[[ -z ${4} ]] && local _extra_par_1="" || local _extra_par_1=${4}
	[[ -z ${5} ]] && local _extra_par_2="" || local _extra_par_2=${5}
	[[ -z ${6} ]] && local _extra_par_3="" || local _extra_par_3=${6}

	${JAVA_HOME}/bin/java ${_options} ${_properties} -cp "${_classpath}" \
		tigase.test.TestSuite -script ${_test_script} ${_output_file} \
		${_server_ip} ${_extra_par_1} ${_extra_par_2} ${_extra_par_3}
}

function run_test() {

	[[ -z ${1} ]] && local _test_type="func" || local _test_type=${1}
	[[ -z ${2} ]] && local _database=${database} || local _database=${2}
	[[ -z ${3} ]] && local _server_dir=${server_dir} || local _server_dir=${3}
	[[ -z ${4} ]] && local _server_ip=${server_ip} || local _server_ip=${4}
	[[ -z ${5} ]] && local _extra_par="" || local _extra_par=${5}

	local _output_dir="${output_dir}/${_test_type}/${_database}"

	[[ -z ${server_timeout} ]] && server_timeout=15

	case ${_test_type} in
		func)
			local _output_file="${_output_dir}/functional-tests.html"
			local _script_file="scripts/all-xmpp-tests.xmpt"
			;;
		lmem)
			local _output_file="${_output_dir}/low-memory-tests.html"
			local _script_file="scripts/all-xmpp-small-mem.xmpt"
			;;
		perf)
			local _output_file="${_output_dir}/performance-tests.html"
			local _script_file="scripts/perform-xmpp-tests.xmpt"
			;;
		stab)
			local _output_file="${_output_dir}/stability-tests.html"
			local _script_file="scripts/stab-xmpp-tests.xmpt"
			;;
		sing)
			local _output_file="${_output_dir}/single-test.html"
			local _script_file="scripts/single-xmpp-test.xmpt"
			local _extra_params="-source-file ${_extra_par}"
			;;
		other)
			local _output_dir="${_output_dir}/"`basename ${_extra_par} .xmpt`
			local _output_file="${_output_dir}/"`basename ${_extra_par} .xmpt`".html"
			local _script_file="${_extra_par}"
			;;
		*)
			echo "Unsupported test type: '${_test_type}'"
			usage
			exit 1
			;;
	esac

	echo "Test type:        ${_test_type}"
	echo "Database:         ${_database}"
	echo "Server directory: ${_server_dir}"
	echo "Server IP:        ${_server_ip}"
	echo "Extra parameters: ${_extra_par}"


	fs_prepare_files

	if [ -z "${SKIP_DB_RELOAD}" ] ; then
	  echo "Re-creating database: ${_database}"
		case ${_database} in
			mysql)
				db_reload_mysql
				;;
			pgsql)
				db_reload_pgsql
				;;
			mssql)
				db_reload_sqlserver
				;;
			derby)
				db_reload_derby
				;;
			mongodb)
				db_reload_mongodb
				;;
			*)
				echo "Not supported database: '${database}'"
				usage
				exit 1
				;;
		esac
	else
		echo "Skipped database reloading."
	fi

	sleep 1
	if [ -z "${SKIP_SERVER_START}" ] ; then
		tig_start_server ${_server_dir} "etc/tigase-${_database}.conf"
		sleep $(((${server_timeout} * 2)))
	else
		echo "Skipped Tigase server starting."
	fi
	if [ -z "${SKIP_DB_RELOAD}" ] ; then
		ts_start scripts/add-admin.xmpt ${_server_ip}
		sleep 1
	else
		echo "Skipped adming account reloading."
	fi
	mkdir -p "${_output_dir}"
	echo -e "\nRunning: ${ver}-${_database} test, IP ${_server_ip}..."
	start_test=`date +%s`
	ts_start ${_script_file} ${_server_ip} ${_output_file} ${_extra_params}
	end_test=`date +%s`
	total_time=$((end_test-start_test))
	if [[ "$(uname -s)" == "Darwin" ]] ; then
		total_str=`date -u -r $total_time +%H:%M:%S`
	else
	        total_str=`date -u -d @$total_time +%H:%M:%S`
	fi
	echo "<td><a href=\"/${_output_file}\">${total_str}</a></td>" >> "${_test_type}-rep.html"
	echo "Test finished after: ${total_str}"
	sleep 1
	if [ -z "${SKIP_SERVER_START}" ] ; then
	        tig_stop_server ${_server_dir} "etc/tigase-${_database}.conf"
        fi

}

function run_functional_test() {

	[[ -z ${1} ]] && local _database=${database} || local _database=${1}
	[[ -z ${2} ]] && local _server_dir=${server_dir} || local _server_dir=${2}
	[[ -z ${3} ]] && local _server_ip=${server_ip} || local _server_ip=${3}

	run_test "func" ${_database} ${_server_dir} ${_server_ip}

}

function run_low_memory_test() {

	[[ -z ${1} ]] && local _database=${database} || local _database=${1}
	[[ -z ${2} ]] && local _server_dir=${server_dir} || local _server_dir=${2}
	[[ -z ${3} ]] && local _server_ip=${server_ip} || local _server_ip=${3}

	run_test "lmem" ${_database} ${_server_dir} ${_server_ip}

}

function run_performance_test() {

	[[ -z ${1} ]] && local _database=${database} || local _database=${1}
	[[ -z ${2} ]] && local _server_dir=${server_dir} || local _server_dir=${2}
	[[ -z ${3} ]] && local _server_ip=${server_ip} || local _server_ip=${3}

	run_test "perf" ${_database} ${_server_dir} ${_server_ip}

}

function run_stability_test() {

	[[ -z ${1} ]] && local _database=${database} || local _database=${1}
	[[ -z ${2} ]] && local _server_dir=${server_dir} || local _server_dir=${2}
	[[ -z ${3} ]] && local _server_ip=${server_ip} || local _server_ip=${3}

	run_test "stab" ${_database} ${_server_dir} ${_server_ip}

}

function run_single_test() {

	[[ -z ${1} ]] && local _database=${database} || local _database=${1}
	[[ -z ${2} ]] && local _server_dir=${server_dir} || local _server_dir=${2}
	[[ -z ${3} ]] && local _server_ip=${server_ip} || local _server_ip=${3}
	[[ -z ${4} ]] && local _stanza_file="" || local _stanza_file=${4}

	run_test "sing" ${_database} ${_server_dir} ${_server_ip} ${_stanza_file}

}

function run_other_test() {

	[[ -z ${1} ]] && local _database=${database} || local _database=${1}
	[[ -z ${2} ]] && local _server_dir=${server_dir} || local _server_dir=${2}
	[[ -z ${3} ]] && local _server_ip=${server_ip} || local _server_ip=${3}
	[[ -z ${4} ]] && local _script_file="scripts/load-xmpp-test.xmpt" \
		|| local _script_file=${4}

	run_test "other" ${_database} ${_server_dir} ${_server_ip} ${_script_file}

}
