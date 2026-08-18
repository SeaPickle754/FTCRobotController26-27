#!/usr/bin/env bash
# GPLv3 license
# ================
# quit if a command fails
set -Eeuo pipefail

print_help(){
	echo "Utility for building and running a new robot program."
	echo "--usb: Build and deploy over a usb connection to the robot."
	echo "--connect: Build, attempt to establish a connection, and upload over wifi."
}

build(){
	./gradlew assembleDebug
}


#hidden option!
# I don't know if these work anywhere else
set_env_vars(){
	export ANDROID_HOME=$HOME/Android/Sdk
	export ANDROID_SDK_ROOT=$ANDROID_HOME
	export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools
}
# i think it has a static ip on it's own network.
# hopefully it doesnt change.
connect(){
	adb connect 192.168.43.1:5555 
}

disconnect(){
	adb disconnect
}

upload(){
	adb install -r TeamCode/build/outputs/apk/debug/TeamCode-debug.apk
}

declare usb=false
declare connect=false

while [[ $# -gt 0 ]]; do
    case "$1" in
        --usb)
            usb=true
            shift
            ;;
		--connect)
            connect=true
            shift
            ;;
		--set-env)
			set_env_vars
			echo "set env_vars. "
			shift
			;;
        *)
            echo "Unknown argument: $1" >&2
            exit 1
            ;;
    esac
done
# print a status message
if $usb; then
	echo "Uploading over usb."
fi
if $connect; then
	echo "Attempting to connect."
fi
if $connect && $usb; then
	echo "Error: Both of these options are mutually exclusive."
	print_help
	exit
fi

build

if $usb; then
	echo "Disconnecting any online connections..."
	disconnect
	echo "uploading..."
	upload
fi
if $connect; then
	echo "attempting disconnect..."
	disconnect
	echo "Connecting"
	connect
	echo "Uploading..."
	upload
fi

echo "Builder done. Thank you!"
