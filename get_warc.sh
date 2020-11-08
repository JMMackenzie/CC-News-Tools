#!/bin/bash

WARC_PATH="https://cloudstor.aarnet.edu.au/plus/s/M8BvXxe6faLZ4uE/download?path=%2Fwarc&files"

mkdir warc

# 1. Get the checksums file
wget "$WARC_PATH=checksums.txt" -O checksums.txt

# 2. For each file, download it
while IFS=" " read -r checksum filename 
do

  wget "$WARC_PATH=$filename" -O warc/$filename

  # Could compare checksum here if desired

done < checksums.txt
