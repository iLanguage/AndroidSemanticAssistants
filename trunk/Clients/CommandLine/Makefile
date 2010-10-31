include ../../../../Makefile.variables

all:
.PHONY : all

run:
	$(ANT) dist

findbugs: FORCE
	$(FINDBUGS) -textui -xml:withMessages -auxclasspath $(GATE)/gate/bin/gate.jar -auxclasspath $(GATE)/gate/lib/log4j-1.2.14.jar -auxclasspath $(PROTEGEX_CLASSPATH) -auxclasspath $(JUNIT) -auxclasspath $(JENA) -auxclasspath ../../CSAL/bin/info/semanticsoftware/semassist/server/*.class bin/info/semanticsoftware/semassist/client/*/*.class > findbugsXml.SA.xml

clean:
#	cd build && rm -rf *

docs:

FORCE:
