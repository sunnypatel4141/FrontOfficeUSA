#!/usr/bin/env perl
use v5.16.3;
use strict;
use warnings;

##################################################
#
# Flow of this import (DEV)
# 1. create table to keep track of the things exportet via id's product, barcode, price
# 2. import the raw data in as is via sub query in to transform database
# 3. transform and insert in to live database
######