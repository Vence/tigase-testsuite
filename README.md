##参照官方文档：
http://docs.tigase.org/tigase-server/snapshot/Development_Guide/html/#tests


***-version = "2.0.0"***
#测试步骤：
##1.添加或修改文件scripts/tests-runner-settings.sh


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



**db_name** :tigase server的数据库名<br>
**db_user**:tigase数据库用户名<br>
**db_pass**:tigase数据库密码<br>
**root_user**:tigase数据库管理员账号<br>
**root_pass**:tigase数据库管理员密码<br>
**TESTS**:待测试数据库，目前支持这四种数据库的测试<br>
**IPS**:待测试数据库的IP地址<br>
**server_dir** :正常情况下，测试之前，都需要调用tigase目录下的脚本清空tigase数据库，并启动tigase，但是这种只支持测试环境与tigase服务器在同一个机器下或可访问FTP下，否则无法访问到tigase目录。如果你不需要清空tigase数据库，这个无需配置，默认都是清空数据库的，具体看**scripts/tests-runner-functions.sh**中的代码：

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

	if [ -z "${SKIP_SERVER_START}" ] ; then
		tig_start_server ${_server_dir} "etc/tigase-${_database}.conf"
		sleep $(((${server_timeout} * 2)))
	else
		echo "Skipped Tigase server starting."
	fi

可以看出，只要**SKIP_SERVER_START** 和 **SKIP_DB_RELOAD** 不为空，就跳过清空数据库和手动启动关闭tigase的操作，可以在**scripts/tests-runner-functions.sh文件**开头声明这两个属性不为空即可，如下：

	SKIP_SERVER_START="true"
	SKIP_DB_RELOAD="true"
<br>
**database**:tigase数据库类型<br>
**server_ip**:tigase服务器地址<br>


##2.创建测试文件
如：创建 **JabberIqPrivate.test**

	send: {
	<iq type="set" id="1001">
	  <query xmlns="jabber:iq:private">
	    <exodus xmlns="exodus:prefs">
	      <defaultnick>Hamlet</defaultnick>
	    </exodus>
	  </query>
	</iq>
	}
	
	expect: {
	<iq type="result" id="1001"/>
	}


这个就是发送报文，和期待的响应报文。

##3.修改测试脚本
###3.1 修改测试类型对应的脚本
这里以single测试为例，修改对应的**single-xmpp-test.xmpt**，默认情况如下：

	-version = "2.0.0"
	
	-output-format  = html
	-output-file    = "files/single-test.html"
	-output-history = yes
	-history-format = separate-file
	-output-cols    = 7
	-title          = "XMPP Server single, common test."
	
	$(server-host) = "192.168.72.130"
	$(def-user)    = all-xmpp-test
	$(daemon-user) = all-xmpp-test_1
	$(blocking-user) = blocking-test-user_1
	$(long-list-user) = long-list-user_1
	$(ssl-tls-wait) = 5000
	$(stats-user) = admin
	$(stats-pass) = stats
	
	-serverip    = "192.168.72.130"
	-host        = $(server-host)
	-user-name   = $(stats-user)
	-user-pass   = $(stats-pass)
	-socket-wait = 5000
	-base-ns     = "jabber:client"
	-def-auth    = auth-sasl
	-def-stream  = stream-client
	-keys-file-password   = keystore
	-trusts-file-password = truststore
	-keys-file            = "certs/keystore"
	-trusts-file          = "certs/client_truststore"
	
	-debug-on-error
	
	Version@auth-sasl;iq-version: >> Get server version <<
	Configuration@command-get-config: >> Server configuration <<
	Statistics@iq-stats: >> Server statistics <<
	Common test@xmpp-session;common: >> Common test <<


根据服务器配置。自定义修改**-serverip**、 **$(server-host)**等等

###3.2 修改公共执行方法脚本
修改**scripts/tests-runner-function.sh**：

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

在调用测试方法的地方，注意JAVA_HOME和_classpath是否正确， 默认_classpath在文件头声明过：

	_classpath="jars/tigase-testsuite.jar:libs/tigase-utils.jar:libs/tigase-xmltools.jar"

检查相应的jar文件是否存在，并放在声明的文件夹内；或执行修改以上声明。

##4.开始测试
执行以下脚本：

	testsuite $ ./scripts/all-tests-runner.sh --single JabberIqPrivate.test
	
	Tigase server home directory: ../server
	Version: 2.8.5-b422
	Database:         xmldb
	Server IP:        127.0.0.1
	Extra parameters: JabberIqPrivate.test
	Starting Tigase:
	Tigase running pid=6751
	
	Running: 2.8.5-b422-xmldb test, IP 127.0.0.1...
	Script name: scripts/single-xmpp-test.xmpt
	Common test:  Common test  ...        failure!
	FAILURE,  (Received result doesnt match expected result.,
	Expected one of: [<iq id="1001" type="result"/>],
	received:
	[<iq id="1001" type="error">
	  <query xmlns="jabber:iq:private">
	    <exodus xmlns="exodus:prefs">
	      <defaultnick>Hamlet</defaultnick>
	    </exodus>
	  </query>
	  <error type="cancel">
	    <feature-not-implemented xmlns="urn:ietf:params:xml:ns:xmpp-stanzas"/>
	    <text xml:lang="en" xmlns="urn:ietf:params:xml:ns:xmpp-stanzas">
	      Feature not supported yet.</text>
	  </error>
	</iq>]),
	
	Total: 100ms
	Test time: 00:00:02
	Shutting down Tigase: 6751

##5.查看结果
结果输出目录都是在脚本**tests-runner-function.sh**中配置的。

	local _output_dir="${output_dir}/${_test_type}/${_database}"

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

刚才single测试的结果html在 **$tigase-testsuite\files\static\tests\7.0.4-b3844\sing\mysql**,结果页面如下:
![](http://7xj3in.com1.z0.glb.clouddn.com/tigase-testsuite-single%E6%B5%8B%E8%AF%95%E7%BB%93%E6%9E%9C.png)

##6.其他
其他的脚本解释请参照官网文档。。
