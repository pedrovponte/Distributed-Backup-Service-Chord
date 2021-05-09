#bin/bash

argc=$#

if ((argc < 2))
then
  echo "Usage: $0 <peer_ap> <test_file>"
  exit 1
fi

peer_ap=$1
test_file=$2

cd build/
java Application "${peer_ap}" DELETE "${test_file}"