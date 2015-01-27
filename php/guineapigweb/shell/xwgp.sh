#/bin/bash

# This function prepare the Json file used for the plot only from beam1.dat
function prepare_json()
{
  echo "[" > beam1.js
  # takes the last two columns and add brackets and commas, e.g. : 3.45 0.22 -> [3.45,0.22],
  awk '{print "["$5","$6"],"}' beam1.dat >> beam1.js
  # remove odd lines to reduce the volume of data
  sed -i -n '1~2p' beam1.js
  # remove odd lines to reduce the volume of data
  sed -i -n '1~2p' beam1.js
  # remove odd lines to reduce the volume of data
  sed -i -n '1~2p' beam1.js
  # remove the last comma
  sed -i '$s/,$//' beam1.js
  echo "]" >> beam1.js
}

# This function retrieves jobs's status and the results if they are all completed
function status()
{
  # the first argument of this function $1 contains the absolute path of the simulation
  #- renames jobstatus.macro to status_getting.macro
  mv ${1}jobstatus.macro ${1}status_getting.macro
  #- sends status_getting.macro
  $XWVERSION --xwmacro ${1}status_getting.macro > ${1}res_status
  # count jobs's status
  PENDING=`cat ${1}res_status | grep -w PENDING | wc -l`;
  RUNNING=`cat ${1}res_status | grep -w RUNNING | wc -l`;
  COMPLETED=`cat ${1}res_status | grep -w COMPLETED | wc -l`;
  ERRORS=`cat ${1}res_status | grep -w ERRORS | wc -l`;
  #- create the jobs_status.txt file
  echo "Pending "$PENDING" Running "$RUNNING" Completed "$COMPLETED" Errors "$ERRORS > ${1}jobs_status.txt  
  #- if there are no more pending nor running jobs renames status_getting.macro to finished.macro and sends results.macro
  if [ $PENDING = 0 ] && [ $RUNNING = 0 ] ; then
    echo "no more Pending nor Running jobs, trying to retrieve results : "$1
    mv ${1}status_getting.macro ${1}finished.macro
    cp ${1}jobsuids ${1}results.macro
    # add --xwget --xwdownload at the beginning of each line
    sed -i 's/^/--xwget --xwdownload /' ${1}results.macro
    # change current directory
    cd $1
    # sends results.macro
    $XWVERSION --xwmacro ${1}results.macro > ${1}res_results
    # unzip results in different directories
    numzip=1
    for zipfile in `ls *.zip`
    do
      # unzip result file in the directory resN
      unzip -d res$numzip $zipfile
      cd res$numzip 
      # create symbolic links to make .dat files readable in web browsers 
      for file in $(ls *.dat); do ln -s $file $file".txt"; done
      # prepare json files for plot
      prepare_json
      #run python script for barchart
      $SVN_PATH/python/plot.py
      #continue to next zip file
      cd ..
      numzip=`expr $numzip + 1`
    done
    # the file results.retrieved allows the web interface to know the simulation is completed
    mv ${1}results.macro ${1}results.retrieved
    #- todo : make zip files available to the web interface
  else
    #- else renames status_getting.macro to jobstatus.macro
    mv ${1}status_getting.macro ${1}jobstatus.macro
  fi
}

function scansimu()
{
  # The Crontab script scans for ready.macro files in each simulation sub-directories of each user directories. If it finds one, it send it
  # browses all users directories
  echo "XWVERSION_PATH "$XWVERSION_PATH 
  echo "XWGP_PATH "$XWGP_PATH 
  for user in `ls -d $XWGP_PATH*/`
  do
    # prepare the command line with the correct configuration file
    XWVERSION=$XWVERSION_PATH" --xwconfig "$user"xtremweb.client.conf"
    # echo $XWVERSION 
    # browses all simulations directories
    for simu in `ls -d $user*/`
    do
      echo $simu
      # we check remove_my_dir first if the user click delete just after run
      if [ -f ${simu}remove_my_dir ]; then
        echo "found simulation to delete : "$simu
        mv ${simu}remove_my_dir ${simu}removing_my_dir
        if [ -f ${simu}remove.macro ]; then
          # add the condition if results.retrieved doesn't exist otherwise it is useless
          mv ${simu}remove.macro ${simu}removing.macro
          $XWVERSION --xwmacro ${simu}removing.macro > ${simu}res_remove
        fi
        rm -rf ${simu}     
      fi
      if [ -f ${simu}ready.macro ]; then
        echo "found simulation ready to run : "$simu
        # renames ready.macro to submitting.macro
        mv ${simu}ready.macro ${simu}submitting.macro
        # generates : jobsuids jobstatus.macro  remove.macro
        $XWVERSION --xwmacro ${simu}submitting.macro > ${simu}jobsuids
        cp ${simu}jobsuids ${simu}jobstatus.macro
        # add --xwget at the beginning of each line
        sed -i 's/^/--xwget /' ${simu}jobstatus.macro
        cp ${simu}jobsuids ${simu}remove.macro
        # add --xwremove at the beginning of each line
        sed -i 's/^/--xwremove /' ${simu}remove.macro
        # renames submitting.macro to submitted.macro
        mv ${simu}submitting.macro ${simu}submitted.macro
        # executes the status() function
        status $simu
      fi
      if [ -f ${simu}jobstatus.macro ]; then
        echo "found simulation to monitor : "$simu
        status $simu
      fi
    done
  done

  # the file last_exec.txt allows the web interface to know the last time this script has been executed
  /bin/date > $XWGP_PATH/last_exec.txt
}

# --xwout to change output file after xwresult. allow user to download the zip file too ?


