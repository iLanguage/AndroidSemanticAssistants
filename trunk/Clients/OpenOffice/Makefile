include ../../Makefile.variables

FINDBUGS_FILES=\
-auxclasspath $(GATE)/gate/bin/gate.jar\
-auxclasspath $(GATE)/gate/lib/log4j-1.2.14.jar\
-auxclasspath $(GATE)/gate/lib/commons-lang-2.4.jar\
-auxclasspath $(PROTEGEX_CLASSPATH)\
-auxclasspath $(JUNIT)\
-auxclasspath $(JENA)\
-auxclasspath /usr/local/durmtools/OpenOffice/ure/share/java/unoloader.jar\
-auxclasspath /usr/local/durmtools/OpenOffice/ure/share/java/juh.jar\
-auxclasspath /usr/local/durmtools/OpenOffice/ure/share/java/ridl.jar\
-auxclasspath /usr/local/durmtools/OpenOffice/ure/share/java/jurt.jar\
-auxclasspath /usr/local/durmtools/OpenOffice/ure/share/java/java_uno.jar\
-auxclasspath /usr/local/durmtools/OpenOffice/basis-link/program/classes/unoil.jar\
-auxclasspath /usr/local/durmtools/OpenOffice/basis-link/sdk/classes/com/sun/star/lib/loader/*.class\
../../CSAL/bin/info/semanticsoftware/semassist/csal/result/*.class\
../../CSAL/bin/info/semanticsoftware/semassist/csal/*.class\
../../CSAL/bin/info/semanticsoftware/semassist/server/*.class\
bin/info/semanticsoftware/semassist/client/*/*.class\
bin/info/semanticsoftware/semassist/client/*/*/*.class\
bin/*.class\
dist/ProtocolHandlerAddon_java.uno.jar

all:
.PHONY : all

run:
	$(ANT) dist

findbugs: FORCE
	echo $(FINDBUGS_FILES) > out.txt
	$(FINDBUGS) -textui -xml:withMessages $(FINDBUGS_FILES) > findbugsXml.SA.xml

clean:
#	cd build && rm -rf *

docs:

FORCE:
