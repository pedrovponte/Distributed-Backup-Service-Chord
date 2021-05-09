#bin/bash

argc=$#

if ((argc < 2))
then
  echo "Usage: $0 <peer_ap> <new_size>"
  exit 1
fi


peer_ap=$1
new_size=$2

cd build/
java Application "${peer_ap}" RECLAIM "${new_size}"