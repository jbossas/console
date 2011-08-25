Prerequisites:
-------------

In order to work on the console. You a need running JBoss 7
instance on your local host. You can download it here:

    http://www.jboss.org:80/jbossas/downloads.html

You can run JBoss in either the 'domain' or 'standalone' scenario.



Running in hosted mode:
----------------------

1.) Make sure JBoss 7 is started

2.) Make sure you build the top level module first.

3.) cd 'gui'

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



Executing the Integration tests:
-------------------------------

1.) Start AS7 in domain mode
2.) Build the 'gui' module:
    cd 'gui'
    mvn compile
3.) Run the smoke tests:
    cd testsuite/smoke
    mvn clean -Dsmoke test

or simply run 
  mvn clean -Dsmoke test 
from the root directory to combine the last two steps.

EAP Build Profile
-----------------

To run a customized EAP build (L&F) follow these steps:

- Create a dedicated version number (i.e. 1.0.0.EAP.CR2)
- Rebuild with the EAP profile turned on: mvn -Peap clean install


Problems?
---------
Please post any questions to the jboss as 7 mailing list:
jboss-as7-dev@lists.jboss.org

Have fun.

