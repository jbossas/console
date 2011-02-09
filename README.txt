Running in hosted mode:
----------------------

1.) Make sure you build the top level module first.

2.) cd 'standalone'

Start the GWT shell with 

	mvn gwt:<run|debug>

When the hosted browser is started, it's enough to hit the 'refresh' button to recompile
and verify changes. You can get the OOPHM Plugin, required for attaching your browser to the 
hosted mode execution here: http://gwt.google.com/samples/MissingPlugin/MissingPlugin.html

NOTE: Really quick turnaround through 

	mvn -Dhosted gwt:<run|debug>

Running in web mode:
-------------------

mvn package 

Produces a war file in target/*-console.war,
which can be deployed to a running jboss instance.

Problems?
---------
Please post any questions to the jboss as 7 mailing list:
jboss-as7-dev@lists.jboss.org

Have fun.

