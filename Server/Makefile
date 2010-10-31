include ../../../Makefile.variables

all:
.PHONY : all

run:
	$(ANT) run

findbugs: FORCE
	$(FINDBUGS) -textui -xml:withMessages -auxclasspath $(GATE)/gate/bin/gate.jar -auxclasspath $(GATE)/gate/lib/log4j-1.2.14.jar -auxclasspath $(PROTEGEX_CLASSPATH) -auxclasspath $(JUNIT) -auxclasspath $(JENA) bin/info/semanticsoftware/semassist/server/*/*.class > findbugsXml.SA.xml

clean:
#	cd build && rm -rf *

docs:

FORCE:
