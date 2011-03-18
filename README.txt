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

3.) cd 'standalone'

Start the GWT shell with 

	mvn gwt:<run|debug>

When the hosted browser is started, it's enough to hit the 'refresh' button to recompile
and verify changes. You can get the OOPHM Plugin, required for attaching your browser to the 
hosted mode execution here: http://gwt.google.com/samples/MissingPlugin/MissingPlugin.html

NOTE: Really quick turnaround through 

	mvn -Dhosted gwt:<run|debug>


NOTE: Currently both the codebase for the 'standalone' and 'domain' usage scenario
	  reside within the same module. A development switch at login prompt allows you to bootstrap one or the other.
      The codebases will be separated at a later point in time. But for now it's most productive way to move forward.

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

