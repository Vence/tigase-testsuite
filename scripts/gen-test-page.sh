#!/bin/bash

TEST_REPORTS="mysql pgsql derby mssql mongodb"
TEST_TYPES="func lmem"
declare -A MAIN_FILE
MAIN_FILE=( [func]="functional-tests.html" [lmem]="low-memory-tests.html" )

function echoindex() {

  echo "$1" >> ${INDEX_DIR}/index.html

}

function gettesttime() {

  testtime=`grep "Total test time" $1 | sed -e "s/[^0-9]\+\([0-9]\+\)[^0-9]\+\([0-9]\+\)[^0-9]\+\([0-9]\+\)[^0-9]\+\([0-9]\+\).*/\1:\2:\3:\4/"`
  echo "${testtime}"

}

SRC_DIR="scripts/tests-page"
if [ -z "${INDEX_DIR}" ]; then
	INDEX_DIR="/home/tigase/public_html"
fi

if [ -z "${BASE_PATH}" ]; then
	BASE_PATH="http://build.tigase.org/nightlies/tests/"
fi

if [[ ${INDEX_DIR} = *night* ]]
then
	INDEX_START="index-start-nightly.txt"
else
    INDEX_START="index-start.txt"
fi
INDEX_END="index-end.txt"
REPORTS_LOC="files/static/tests"

FAILED=false
FAIL_CNT=0

ALL_REPORTS_DIRS=`ls -1 ${INDEX_DIR}/${REPORTS_LOC} | sort -r`

cat ${SRC_DIR}/${INDEX_START} > ${INDEX_DIR}/index.html

for tt in ${TEST_TYPES} ; do
FIRST=true

  cat ${SRC_DIR}/${tt}-start.txt >> ${INDEX_DIR}/index.html

  cat ${SRC_DIR}/headers-start.txt >> ${INDEX_DIR}/index.html

  for tr in ${TEST_REPORTS} ; do

    cat ${SRC_DIR}/${tr}-start.txt >> ${INDEX_DIR}/index.html

  done

  cat ${SRC_DIR}/headers-end.txt >> ${INDEX_DIR}/index.html

  for ard in ${ALL_REPORTS_DIRS} ; do

      IS_TEST=""

      for tr in ${TEST_REPORTS} ; do

        MFILE1="${INDEX_DIR}/${REPORTS_LOC}/${ard}/${tt}/${tr}/"${MAIN_FILE[${tt}]}
        
        if [ -f ${MFILE1} ] ; then
          IS_TEST="${IS_TEST}${tr}"
        fi

      done      

      if [ ! -z ${IS_TEST} ] ; then

      DATE=`grep -iE "Test start time" ${INDEX_DIR}/${REPORTS_LOC}/${ard}/${tt}/mysql/${MAIN_FILE[${tt}]} | perl -pe "s|.*?<p>Test start time.*<b>(.*?) (.*?), (.*?) (.*?)</b.*|\3 \1 \2|"`

      echoindex "<tr><th>${DATE}</th>"

      echoindex "<th>${ard}</th>"

      bgcolor="#90FF90"

      for tr in ${TEST_REPORTS} ; do

        MFILE1="${REPORTS_LOC}/${ard}/${tt}/${tr}/"${MAIN_FILE[${tt}]}

        MFILES="${MFILE1}"


		  if [ -f ${INDEX_DIR}/${MFILE1} ] ; then
			bgcolor="#90FF90"
			if grep "FAILURE" ${INDEX_DIR}/${MFILE1} > /dev/null ; then
			  bgcolor="FF9090"
			fi
			SUCCESS=`grep -c success ${INDEX_DIR}/${MFILE1}`
			FAILURE=`grep -ic failure ${INDEX_DIR}/${MFILE1} || true`
			FAILURE_LIST=`egrep -iE ".*FAILURE.*" ${INDEX_DIR}/${MFILE1} | perl -pe "s|.*?<td>(.*?)</td.*|\1,|"`
		
			if ${FIRST} ; then
				FAIL_CNT=$((${FAIL_CNT}+${FAILURE})) || true
				echo "${BASE_PATH}/${MFILE1}: ${FAILURE}"
				echo "${FAILURE_LIST}"
			fi

			ttime=`gettesttime ${INDEX_DIR}/${MFILE1}`
			echoindex "<td class=\"rtecenter\" bgcolor=\"${bgcolor}\"><a href=\"${MFILE1}\">${SUCCESS} | ${FAILURE} | $((${SUCCESS} + ${FAILURE})) | ${ttime}</a></td>"
		  else
			echoindex "<td class=\"rtecenter\"> --- </td>"
		  fi
        
      done

	#check only latest version
	if ${FIRST} ; then
	  if [[ ${FAIL_CNT} > 5 ]] || ${FAILED}  ; then 
		FAILED=true
	  fi
	  echo ${FAIL_CNT}
		FIRST=false
	fi

      echoindex "</tr>"

      fi

  done

  cat ${SRC_DIR}/table-end.txt >> ${INDEX_DIR}/index.html
  
done

cat ${SRC_DIR}/${INDEX_END} >> ${INDEX_DIR}/index.html

if  ${FAILED} ; then
	echo "ERROR: BUILD FAILED"
	exit 1
else
	echo "BUILD SUCCEEDED"
fi
