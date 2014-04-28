!/usr/local/bin/zsh

# MySQL Server has gone away problem - see: http://stackoverflow.com/questions/12425287/mysql-server-has-gone-away-when-importing-large-sql-file
# see more at: http://www.mediawiki.org/wiki/Manual:MWDumper

export USER=root
export PASS=root
export DB=wikipedia

export EXPORT_PROCESSES=4
export IMPORT_PROCESSES=4
export DUMP_FILE=Wikipedia_English_Official_Offline_Edition_version_20130805_Xprt.xml

cd mwdumper
mvn package
cd ..

java -jar mwdumper/target/mwdumper-1.16.jar --format=sql:1.5 $DUMP_FILE > dumper.out
split -l 12804 dumper.out

mysqladmin create wikipedia --default-character-set=utf8 -u $USER --password=$PASS
mysql -u $USER $DB --password=$PASS < tables.sql

mysql -u $USER $DB <<HERE
ALTER TABLE page
  CHANGE page_id page_id INTEGER UNSIGNED,
  DROP INDEX name_title,
  DROP INDEX page_random,
  DROP INDEX page_len,
  DROP INDEX page_redirect_namespace_len;
ALTER TABLE revision 
  CHANGE rev_id rev_id INTEGER UNSIGNED,
  DROP INDEX rev_page_id,
  DROP INDEX rev_timestamp,
  DROP INDEX page_timestamp,
  DROP INDEX user_timestamp,
  DROP INDEX usertext_timestamp,
  DROP INDEX page_user_timestamp;
ALTER TABLE text
  CHANGE old_id old_id INTEGER UNSIGNED;
HERE

# xa* files are result of split -l
for FILE in xa*
do
	echo "Start processing file $FILE"
	cat $FILE | mysql -u $USER $DB --password=$PASS
	echo "Finised processing file $FILE"
done
