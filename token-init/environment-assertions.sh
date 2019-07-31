#!/bin/bash

function assertFilePresent {
    if [ ! -f $1 ]; then
        echo "$2 not found at: $1"
        exit 1
    fi
}

function assertCommandAvailable {
    if ! which "$1" > /dev/null; then
        echo "Command not available: $1"
        exit 1
    fi
}

function asksure() {
    while read -r -n 1 -s answer; do
      if [[ $answer = [YyNn] ]]; then
        [[ $answer = [Yy] ]] && retval=0
        [[ $answer = [Nn] ]] && retval=1
        break
      fi
    done
    return $retval
}
