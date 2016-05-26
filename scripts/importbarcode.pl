#!/usr/bin/perl

use v5.16.3;

use strict;
use DBI;
my $host = '192.168.11.80';
my $conn = "DBI:mysql:myepos:$host:3306";
my $un = 'myadmin';
my $pw = 'mypassword';
my $dbh = DBI->connect($conn, $un, $pw)
	or die 'Unable to establish a database connection';

print "/n Import Data Into The databse /n";
# Get things from this array in the correct order
my $file = ['maincategories.csv', 'product.csv', 'barcode.csv', 'price.csv'];
my $mc;

sub main {
	# ============The flow of this utility==================
	# N.B the files of the $file array Ref must be in this driectory
	# 1. prep the files please follow the FORMATTING OF THE COLS AND NAMES
	# 2. backup the 
	#	product, barcode, price, maincategory tables in mysqldump in this dir
	# 3. Delete from the tables in step 2
	# 4. Add all the Main categories and map the main Main Cats in that table
	# 5. Update the product table with the mapping of the maincat
	# 6. Update the Barcode table from the lookup in extmap table
	# 7. Update the product price table form the lookup in extmap table
	
	# Step 2: Backup Just in case
	foreach my $fl (@$file) {
		print "Files Does not exist $fl" unless (-e $fl);
		exit unless (-e $fl);
	}
	&backuptable;
	
	# Step 3: Insert into main category
	#&maincategories($file->[0]);
	&products($file->[1]);
	&barcodes($file->[2]);
	&price($file->[3]);
}

sub backuptable {
	say 'Backup Database';
	
	my $flag = 0;
	# mysqldump
	my $dbname = time() . 'myepos.sql.gz';
	my @output = `mysqldump -u $un -h $host -p myepos product barcode productprice maincategory | gzip > $dbname`;
	if ($output[0] =~ /fail|denied|error/ ) {
		my $hashmsg = '\#' * 10;
		print $hashmsg . ' ERROR' . $hashmsg;
		map{ print $_ . '\n';}@output;
		print 'BACKUP FAILED CANNOT CONTINUE EXITING NOW';
		print $hashmsg . $hashmsg;
		exit;
	} else {
		my $sql = 'delete from barcode';
		$dbh->do($sql);
		$sql = 'delete from productprice';
		$dbh->do($sql);
		$sql = 'delete from product';
		$dbh->do($sql);
		$sql = 'delete from subcategory';
		$dbh->do($sql);
		$sql = 'delete from maincategory';
		$dbh->do($sql);
		$flag = 1;
	}
	
	return $flag;	
}

sub barcodes {
	my $file = shift @_;
	
	open(my $fh, "< $file")
		or die 'Cannot Open file for reading';
	my $found_dest = 0;
	foreach my $line (<$fh>) {
		$line =~ s/\r//g;
		$line =~ s/\"//g;
		chomp $line;
		my @col_data = split(/,/, $line);
		if( $col_data[1] eq 'barcode') {
			$found_dest = 1;	
			next;
		} elsif($found_dest){
			my $sql = "insert into barcode(barcode, prid) " . 
			"select ?, `pr`.`id` from " .
			" `product` `pr` where `pr`.`extid`=?";
			my $sth = $dbh->prepare($sql);
			eval {
				$sth->execute($col_data[1], $col_data[0]);
			};
			if($@) {
				print "error at " . $@;
			}
		}
	}
}

sub maincategories {
	my $file = shift @_;
	
	open(my $fh, "< $file")
		or die 'Cannot Open file for reading';
	my $found_dest = 0;
	my $counter = 0;
	foreach my $line (<$fh>) {
		$line =~ s/\r//g;
		$line =~ s/\"//g;
		chomp $line;
		my @col_data = split(/,/, $line);
		if( $col_data[0] eq 'extid') {
			$found_dest = 1;	
			next;
		} 
		if($found_dest){
			# TODO: need to insert data into the main category
			my $sql = "insert into `maincategory` (`name`, `extid`) " .
				"values('" . $col_data[1] . "', '" . $col_data[0] . "')";
			$dbh->do($sql);
			$counter++;
		}
	}
	
	if ($counter == 0) {
		my $hashmsg = '\#' * 10;
		print $hashmsg . ' MAIN CATETGORY ERROR ' . $hashmsg;
		print 'Processing Failed Cannot find columnheader "name" ';
		print 'exiting now';
		print $hashmsg . $hashmsg;
		exit;
	} else {
		print 'Processed ' . $counter . ' Main Categories';
		my $rs = $dbh->selectall_arrayref('select * from maincategory');
		foreach my $row ( @$rs) {
			$mc->{$row->[4]} = $row->[0];
		}
	}
}

sub products {
	my $file = shift @_;
	
	open(my $fh, "< $file")
		or die 'Cannot Open file for reading';
	my $found_dest = 0;
	my $counter = 0;
	foreach my $line (<$fh>) {
		$line =~ s/\r//g;
		$line =~ s/\"//g;
		chomp $line;
		my @col_data = split(/,/, $line);
		if( $col_data[1] eq 'name') {
			$found_dest = 1;	
			next;
		}
		if($found_dest){
			#TODO: Implement adding the products to the table
			my $sql = "insert into `product` (`name`, `mcid`, `extid`, `qk`) " . 
				"values(?, ?, ?, ?)";
			my $sth = $dbh->prepare($sql);
			$sth->execute($col_data[1], $mc->{$col_data[2]}, $col_data[0], $col_data[5]);
			$counter++;
		}
	}
	
	if ($counter == 0) {
		my $hashmsg = '\#' * 10;
		print $hashmsg . ' PRODUCT ERROR ' . $hashmsg;
		print 'Processing Failed Cannot find columnheader "name" ';
		print 'exiting now';
		print $hashmsg . $hashmsg;
		exit;
	} else {
		print 'Processed ' . $counter . ' Main Categories';
	}
}

sub price {
	my $file = shift;
	
	open (my $fh, "< $file") or Carp::croak "Cannot open file for reading";
	
	# create temp table to import this whole table
	my $sql = ' CREATE TEMPORARY TABLE `prices_tmp` (
		`Product_Qty_Id` int(55) DEFAULT NULL,
	  `CentralProductCode` varchar(55) DEFAULT NULL,
	  `Number_Of_Items` int(55) DEFAULT NULL,
	  `Price` varchar(55) DEFAULT NULL,
	  `CreatedBy` varchar(55) DEFAULT NULL,
	  `CreatedDateTime` varchar(55) DEFAULT NULL,
	  `ModifiedBy` varchar(55) DEFAULT NULL,
	  `ModifiedDateTime` varchar(55) DEFAULT NULL,
	  `Is_Deleted` varchar(55) DEFAULT NULL,
	  `WarehousePrice` varchar(55) DEFAULT NULL,
	  `MailOrderPrice` varchar(55) DEFAULT NULL,
	  `WebPrice` varchar(55) DEFAULT NULL,
	  `Br_Code` varchar(55) DEFAULT NULL
	) ENGINE=InnoDB DEFAULT CHARSET=latin1';
	
	# Create the temptable
	$dbh->do($sql);

	my $found_dest = 0;
	my $counter = 0;
	my @heading;
	
	foreach my $line (<$fh>) {
		$line =~ s/\r//g;
		$line =~ s/\"//g;
		chomp $line;
		my @col_data = split(/,/, $line);
		if (@col_data[0] eq 'Product_Qty_Id') {
			$found_dest = 1;
			next;
		}
		
		if ( $found_dest ) {
			$sql = 'insert into prices_tmp (' .  join(', ', @heading) . ') values (';
			$sql .= join(', ', @col_data) . ')';
			eval {
				$sql->do($sql);
			};
			
			say "Error at importinf price " . @$ if @$;
		}
	}
	
	# creating this so that any mismatching prid's can be removed
	$sql = 'create temporary table prices_tmp_clean like productprice';
	$dbh->do($sql)
	$sql = 'select from prices_tmp_clean where prid="0"';
	$dbh->do($sql);
	
	$sql = "insert into productprice (prid, qty, price) select p.prid, p.qty, p.price from prices_tmp_clean";
	$dbh->do($sql);
	
	say "Successfully imported the prices and cleand them up"
	
}
	
	

sub price_old {
	my $file = shift @_;
	
	open(my $fh, "< $file")
		or die 'Cannot Open file for reading';
	my $found_dest = 0;
	my $counter = 0;
	
	foreach my $line (<$fh>) {
		$line =~ s/\r//g;
		$line =~ s/\"//g;
		chomp $line;
		my @col_data = split(/,/, $line);
		if( $col_data[2] eq 'price') {
			$found_dest = 1;	
			next;
		} 
		if($found_dest){
			#TODO: Implement adding the products to the table
			my $sql = "insert into `productprice` (`price`, `qty`, `prid`) " .
				"select ?, ?, `pr`.`id` " .
				"from `product` `pr` where `pr`.`extid`=?";
			my $sth = $dbh->prepare($sql);
			$sth->execute($col_data[2], $col_data[1], $col_data[0]);
			$counter++;
		}
	}
	
	if ($counter == 0) {
		my $hashmsg = '\#' * 10;
		print $hashmsg . ' PRICE ERROR ' . $hashmsg;
		print 'Processing Failed Cannot find columnheader "name" ';
		print 'exiting now';
		print $hashmsg . $hashmsg;
		exit;
	} else {
		print 'Processed ' . $counter . ' Main Categories';
	}
}

# Some barcodes exist as extid so they need to be merged in
sub import_producut_codes {
    my $product_barcode;
    my $rs = $dbh->selectall_arrayref('select * from product where id not in (select prid from barcode)');
    foreach my $row ( @$rs) {
        # Typically the extis is the 9th row so array ref is 8
        # Add the array ref to the main table
        # Only if the barcode contains decimals
        if ( $row->[8] =~ /\d+/) {
            $product_barcode->{$row->[0]} = $row->[8]; 
        }
    }

    foreach my $val ( sort keys %{$product_barcode} ) {
        my $sql_local = "insert into barcode(barcode, prid) " .
            "select ?, ? from bacode";
        my $sth = $dbh->prepare($sql_local);
        $sth->execute($product_barcode->{$val}, $val);
    }
}



################################################
#
# Methods here are not required at the moment
# 
################################################
#sub remove_dupe_product_prices {
#    # Build the list that we want to keep
#    my $sql = "select distinct prid, count(prid), id, qty, max(price) from productprice " .
#        "where qty='1' group by prid having count(prid) > 1";
#    my $rs = $dbh->selectall_arrayref($sql);
#    foreach my $row (@$rs) {
#        &_find_rows_to_delete($row->[0], $row->[4]);
#    }
#}

#sub _find_rows_to_delete {
#    my ($prid, $price) = @_;
#    
#    # Lets remove all the unecessary ones
#	my $sql = "select * from productprice where prid='" . $prid . "'";
#	my $rs2 = $dbh->selectall_arrayref($sql);
#	foreach my $row2 (@$rs2) {
#		# Get the ID here
#		&_delete_row($row2->[0]) if( $price ne $row2->[1] );
#	}
#}


#sub _delete_row {
#    my($todel) = @_;

#    my $sql = "delete from productprice where id='" . $todel . "'";
#    $dbh->do($sql);
#}




main();
exit;

#select distinct barcode, count(BARCODE) as dupes, name, prid from searchproducts_local group by barcode having 
