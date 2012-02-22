#!/bin/sh

# western
array=( UIConstants_de.properties UIConstants_fr.properties UIConstants_es.properties UIConstants_pt_BR.properties \
        UIMessages_de.properties UIMessages_fr.properties UIMessages_es.properties UIMessages_pt_BR.properties )
for i in "${array[@]}"
do
	echo "src/main/java/org/jboss/as/console/client/core/$i"
	native2ascii -encoding iso-8859-1 src/main/java/org/jboss/as/console/client/core/$i src/main/java/org/jboss/as/console/client/core/$i
	sed -i .bak "s/\'/\&\#39\;/g" src/main/java/org/jboss/as/console/client/core/$i
done

# chinese
array=( UIConstants_zh_Hans.properties UIMessages_zh_Hans.properties )
for i in "${array[@]}"
do
	echo "src/main/java/org/jboss/as/console/client/core/$i"
	native2ascii -encoding x-MS950-HKSCS src/main/java/org/jboss/as/console/client/core/$i src/main/java/org/jboss/as/console/client/core/$i
done

# japanese
array=( UIConstants_ja.properties UIMessages_ja.properties )
for i in "${array[@]}"
do
	echo "src/main/java/org/jboss/as/console/client/core/$i"
	native2ascii -encoding windows-31j src/main/java/org/jboss/as/console/client/core/$i src/main/java/org/jboss/as/console/client/core/$i
done


