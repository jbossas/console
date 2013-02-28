

Steps to push files to translate.jboss.org:


1.) mvn -Pi18n initialize
2.) mvn -Pi18n zanata:push -Dzanata.projectType=properties


Steps to pull new translations:

1.) mvn -Pi18n zanata:pull -Dzanata.projectType=properties
2.) these will end up in target/i18n
2.1) unicide.sh (for jp, zh_Hans, etc)
3.) Merge into orig files (different stucture of base and _en.properties for constants)


