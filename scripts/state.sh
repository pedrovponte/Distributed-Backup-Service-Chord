#bin/bash

argc=$#

if ((argc < 1))
then
  echo "Usage: $0 <peer_ap>"
  exit 1
fi

peer_ap=$1

cd build/
java Application "${peer_ap}" STATE