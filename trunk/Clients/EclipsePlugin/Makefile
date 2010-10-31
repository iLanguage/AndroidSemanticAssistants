include ../../Makefile.variables

FINDBUGS_FILES=\
-auxclasspath $(GATE)/gate/bin/gate.jar\
-auxclasspath $(GATE)/gate/lib/log4j-1.2.14.jar\
-auxclasspath $(GATE)/gate/lib/commons-lang-2.4.jar\
-auxclasspath $(PROTEGEX_CLASSPATH)\
-auxclasspath $(JUNIT)\
-auxclasspath $(JENA)\
-auxclasspath lib/CSAL.jar\
plugins/*.jar

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
