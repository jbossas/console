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


Development Profile
-------------------

Due to the increased number of permutations (additional languages) the full compile times
have increased quiet drastically. To work around this problem during development, we've added
a development build profile that restricts the languages to english and the browser permutations to safari and firefox:

mvn -Pdev clean install

Bind Address
------------

In some cases you may want to bind both the AS and the hosted mode to a specific address.
A typical scenario is running a differnt OS (i.e windows) in a virtual machine.
In order to make such a setup work you need to bind the hosted mode environment and the application server
to a specific inet address that can access from thin the vertical machine:

1) start the AS on a specific address:

    ./bin/standalone.sh -Djboss.bind.address=192.168.2.126 -Djboss.bind.address.management=192.168.2.126

2) launch hosted mode on a specific address:

    mvn clean -Dgwt.bindAddress=192.168.2.126 gwt:run

Problems?
---------
Please post any questions to the jboss as 7 mailing list:
jboss-as7-dev@lists.jboss.org

Have fun.

