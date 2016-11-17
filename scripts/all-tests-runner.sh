#!/bin/bash
##
##  Tigase XMPP/Jabber Test Suite
##  Copyright (C) 2004-2013, "Tigase, Inc." <office@tigase.com>
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

VERSION="2.0.0"

SETTINGS_FILE=`dirname ${0}`/tests-runner-settings.sh
[[ -f ${SETTINGS_FILE} ]] && source ${SETTINGS_FILE} \
  || {
	echo "Can't find settings file: ${SETTINGS_FILE} using defaults"
	#D:\work\OtherProjects\tigaseTestuite\tigase-server-7.0.4-b3844
	server_dir="D:\work\OtherProjects\tigaseTestuite\Tigase5.1.5"
	database="mysql"
	database_host="127.0.0.1"
	server_ip="127.0.0.1"
	server_timeout=30
	db_user="root"
	db_pass="123"
	db_name="tigasedb2"
	root_user="root"
	root_pass="123"	

	MS_MEM=100
	MX_MEM=1000
	SMALL_MS_MEM=10
	SMALL_MX_MEM=50
}
export MIN_MEM=$MS_MEM
export MAX_MEM=$MX_MEM

FUNCTIONS_FILE=`dirname ${0}`/tests-runner-functions.sh
[[ -f ${FUNCTIONS_FILE} ]] && source ${FUNCTIONS_FILE} \
  || { echo "Can't find functions file: ${FUNCTIONS_FILE}" ; exit 1 ; }


function usage() {
	echo "Run selected or all tests for Tigase server"
	echo "----"
	echo "Author: Artur Hefczyc <artur_hefczyc@vnu.co.uk>"
	echo "Version: ${VERSION}"
	echo "----"
	echo "  --help|-h	This help message"
	echo "  --func [mysql|pgsql|derby|mssql|mongodb]"
	echo "              Run all functional tests for a single database configuration"
	echo "  --lmem [mysql|pgsql|derby|mssql|mongodb]"
	echo "              Run low memory tests for a single database configuration"
	echo "  --perf [mysql|pgsql|derby|mssql|mongodb]"
	echo "              Run all performance tests for a single database configuration"
	echo "  --stab [mysql|pgsql|derby|mssql|mongodb]"
	echo "              Run all stability tests for a single database"
	echo "              configuration"
	echo "  --func-all  Run all functional tests for all database"
	echo "              configurations"
	echo "  --lmem-all  Run low memory tests for all database"
	echo "              configurations"
	echo "  --perf-all  Run all performance tests for all database"
	echo "              configurations"
	echo "  --stab-all  Run all stability tests for all database"
	echo "              configurations"
	echo "  --all-tests Run all functionality and performance tests for"
	echo "              database configurations"
	echo "  --single test_file.cot"
	echo "  --other script_file.xmpt"
	echo "----"
	echo "  Special parameters only at the beginning of the parameters list"
	echo "  --debug|-d                 Turns on debug mode"
	echo "  --skip-db-relad|-no-db     Turns off reloading database"
	echo "  --skip-server|-no-serv     Turns off Tigase server start"
	echo "  --small-mem|-sm            Run in small memory mode"
	echo "-----------"
	echo "  Other possible parameters are in following order:"
	echo "  [server-dir] [server-ip]"
}

found=1
while [ "${found}" == "1" ] ; do
	case "${1}" in
    --debug|-d)
			set -x
			shift
			;;
		--skip-db-relad|-no-db)
			export SKIP_DB_RELOAD=1
			shift
			;;
		--skip-server|-no-serv)
			export SKIP_SERVER_START=1
			shift
			;;
		--small-mem|-sm)
			export MIN_MEM=$SMALL_MS_MEM
			export MAX_MEM=$SMALL_MX_MEM
			shift
			;;
		*)
			found=0
			;;
	esac
done

case "${1}" in
	--func|--lmem|--perf|--stab)
		[[ -z ${2} ]] || database=${2}
		[[ -z ${3} ]] || server_dir=${3}
		[[ -z ${4} ]] || server_ip=${4}
		;;
	--func-all|--lmem-all|--perf-all|--stab-all|--all-tests)
		[[ -z ${2} ]] || server_dir=${2}
		[[ -z ${3} ]] || server_ip=${3}
		;;
	--single|--other)
		[[ -z ${3} ]] || server_dir=${3}
		[[ -z ${4} ]] || server_ip=${4}
		;;
	*)
		[[ -z "${1}" ]] || echo "Invalid command '$1'"
		usage
		exit 1
		;;
esac

unzip -o ${server_dir}/jars/tigase-server.jar -d ${server_dir}/jars/tigase-server >> unzip.log
ver=`grep -m 1 "Tigase-Version:" ${server_dir}/jars/tigase-server/META-INF/MANIFEST.MF | sed -e "s/Tigase-Version: \(.*\)/\\1/" | sed 's/[[:space:]]//'`
output_dir="${ROOT_DIR}files/static/tests/${ver}"

echo "Tigase server home directory: ${server_dir}"
echo "Version: ${ver}"
echo "Output dir: ${output_dir}"

case "${1}" in
	--help|-h)
		usage
		;;
	--func)
		run_functional_test ${database} ${server_dir} ${server_ip}
		;;
	--lmem)
		export MIN_MEM=$SMALL_MS_MEM
		export MAX_MEM=$SMALL_MX_MEM
		run_low_memory_test ${database} ${server_dir} ${server_ip}
		;;
	--func-all)
		cp -f func-rep.html_tmp func-rep.html
		echo "<tr><th>${ver}</th>" >> func-rep.html
		run_functional_test derby ${server_dir} ${IPS[0]}
		sleep $(((${server_timeout} * 2)))
		run_functional_test mysql ${server_dir} ${IPS[1]}
		sleep $(((${server_timeout} * 2)))
		run_functional_test pgsql ${server_dir} ${IPS[2]}
		sleep $(((${server_timeout} * 2)))
		run_functional_test mssql ${server_dir} ${IPS[3]}
		sleep $(((${server_timeout} * 2)))
		run_functional_test mongodb ${server_dir} ${IPS[4]}
		echo "</tr>" >> func-rep.html
		;;
	--lmem-all)
		export MIN_MEM=$SMALL_MS_MEM
		export MAX_MEM=$SMALL_MX_MEM
		cp -f lmem-rep.html_tmp lmem-rep.html
		echo "<tr><th>${ver}</th>" >> func-rep.html
		run_low_memory_test derby ${server_dir} ${IPS[0]}
		sleep $(((${server_timeout} * 2)))
		run_low_memory_test mysql ${server_dir} ${IPS[1]}
		sleep $(((${server_timeout} * 2)))
		run_low_memory_test pgsql ${server_dir} ${IPS[2]}
		sleep $(((${server_timeout} * 2)))
		run_low_memory_test mssql ${server_dir} ${IPS[3]}
		sleep $(((${server_timeout} * 2)))
		run_low_memory_test mongodb ${server_dir} ${IPS[4]}
		echo "</tr>" >> lmem-rep.html
		;;
	--perf)
		run_performance_test ${database} ${server_dir} ${server_ip}
		;;
	--stab)
		run_stability_test ${database} ${server_dir} ${server_ip}
		;;
	--perf-all)
		cp -f perf-rep.html_tmp perf-rep.html
		echo "<tr><th>${ver}</th>" >> perf-rep.html
		run_performance_test derby ${server_dir} ${IPS[0]}
		sleep $(((${server_timeout} * 2)))
		run_performance_test mysql ${server_dir} ${IPS[1]}
		sleep $(((${server_timeout} * 2)))
		run_performance_test pgsql ${server_dir} ${IPS[2]}
		sleep $(((${server_timeout} * 2)))
		run_performance_test mssql ${server_dir} ${IPS[3]}
		sleep $(((${server_timeout} * 2)))
		run_performance_test mongodb ${server_dir} ${IPS[4]}
		echo "</tr>" >> perf-rep.html
		;;
	--stab-all)
		cp -f stab-rep.html_tmp stab-rep.html
		echo "<tr><th>${ver}</th>" >> stab-rep.html
		run_stability_test derby ${server_dir} ${IPS[0]}
		sleep $(((${server_timeout} * 2)))
		run_stability_test mysql ${server_dir} ${IPS[1]}
		sleep $(((${server_timeout} * 2)))
		run_stability_test pgsql ${server_dir} ${IPS[2]}
		sleep $(((${server_timeout} * 2)))
		run_stability_test mssql ${server_dir} ${IPS[3]}
		sleep $(((${server_timeout} * 2)))
		run_stability_test mongodb ${server_dir} ${IPS[4]}
		echo "</tr>" >> stab-rep.html
		;;
	--all-tests)
		${0} --func-all ${server_dir}
		sleep $(((${server_timeout} * 2)))
		${0} --perf-all ${server_dir}
		#${0} --stab-all ${server_dir}
		;;
	--single)
		run_single_test ${database} ${server_dir} ${server_ip} ${2}
		;;
	--other)
		run_other_test ${database} ${server_dir} ${server_ip} ${2}
		;;
	*)
		[[ -z "${1}" ]] || echo "Invalid command '$1'"
		usage
		exit 1
		;;
esac
