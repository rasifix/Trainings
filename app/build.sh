#!/bin/bash

cd _attachments/ng
bundle exec rakep
cd ../..
/usr/local/bin/couchapp push
