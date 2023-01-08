#!/bin/sh

yes_or_no() {
    while true; do
        printf "$* [y/n]: "
        read -r yn
        case $yn in
            [Yy]*) return 0 ;;
            [Nn]*) echo "Aborted"; exit 0 ;;
        esac
    done
}

file_descriptors_test() {
    printf ' =============== TEST 1 ===============\n'
    if [ $verbose ]; then
        cat << EOF
I'm going to check whether your executor keeps file descriptors under
control. Here's what I'm going to feed the executor:
EOF
        cat file_descriptors.in
    else
        printf "file_descriptors_test()...\n\n"
    fi

    [ -f "test1.out" ] && rm test1.out
    "$1" <file_descriptors.in >"$FIFO" &
    sleep 2
    while read -r line; do
        if [ "${line#*'signalled'}" = "$line" ]; then
            line=${line##*'pid '}
            ls -l /proc/"${line%.}/fd" >>test1.out
        else
            echo "$line"
        fi
    done <"$FIFO"

        echo
    if [ $(wc -l <test1.out) = '20' ]; then
        if [ $verbose ]; then
            printf "If all of your fds are 0s 1s and 2s, then PASSED.\n"
        else
            printf "PASSED.\n"
        fi
    else
        printf "FAILED.\n"
        cat test1.out
        return 1
    fi

    [ $verbose ] && cat test1.out
}

safety_test() {
    printf ' =============== TEST 2 ===============\n'
    if [ $verbose ]; then
        cat << EOF
All this test does is, it runs a process that dies in around 3 seconds,
and sleeps immediately for 12 seconds. I'm going to kill executor before
it wakes up. If exit status got printed out, something's wrong. Input:
EOF
        cat safety.in
    else
        printf 'safety_test()...\n'
    fi
    echo
    "$1" <safety.in >"$FIFO" &
    pid=$!

    [ -f 'test2.out' ] && rm test2.out
    tee <"$FIFO" test2.out &
    id=0
    max=6 # >10 fails
    sleep 2
    while [ "$id" -le $max ]; do
        sleep 1
        id=$((id + 1))
        printf '%d ' "$id"
    done
    printf '\nkill -s TERM\n'
    kill -s TERM "$pid"

    if [ "$(sed '/ended/! d' test2.out)" ]; then
        printf "\nFAILED!\n"
    else
        printf "\nPASSED!\n"
    fi
}

eof_test() {
    printf ' =============== TEST 3 ===============\n'
    if [ $verbose ]; then
        cat << EOF
Now, I'll just run 100 processes of three kinds, 60 of them must end,
40 will be signalled due to EOF. Pretty simple. Here's the input:
EOF
        printf '\nsleep 4000\nrun ./ninja 30x\nrun ./napper 30x\n'`
              `'run ./resident_sleeper 60x\nsleep 5500\n'
    else
        printf 'eof_test()...\n'
    fi
    echo
    
    if [ $verbose ]; then
        "$1" <eof.in | tee test3.out
    else
        "$1" <eof.in > test3.out
    fi

    if sed '/started/d; s/ \([0-9]\) / 0\1 /' test3.out | sort |
            diff correcttest3.out -; then
        printf "\nPASSED!\n"
        [ $verbose ] && cat correcttest3.out
    else
        printf "\nFAILED!\n"
        sed '/started/d; s/ \([0-9]\) / 0\1 /' test3.out | sort
    fi
}

while getopts ':hv' arg; do
    case $arg in
        h)
            printf 'Usage: ./test.sh [-h] [-v] [path to executor]\n'
            printf 'Arguments:\nh - help\nv - verbose\n'
            exit 0 ;;
        v)
            verbose=y ;;
        ?)
            printf 'Usage: ./test.sh [-h] [-v] [path to executor]\n'
            exit 1 ;;
    esac
done
shift "$((OPTIND - 1))"

executor=$(readlink -f "$1")
cd "$(dirname "$0")"
FIFO=./fifo
! [ -p './fifo' ] && { [ -f './fifo' ] && rm fifo; mkfifo fifo; }

[ -x "$executor" ] ||
    { printf "No permissions to run executor...\n"; exit 1; }
[ $verbose ] &&
    yes_or_no "Since printf() doesn't flush its output, you'll have to\n"`
             `"set setbuf(stdout, NULL) before any printf or just\n"`
             `"ignore test 1. Do you want to continue?"

trap "pkill -P $$; printf '\nCleaned up. So exciting! :)\n'; exit 0" INT
make
file_descriptors_test "$executor"
[ $verbose ] && yes_or_no "Continue?"
safety_test "$executor"
[ $verbose ] && yes_or_no "Continue?"
eof_test "$executor"
make clean
exit 0
