LATEX=pdflatex
BIBTEX=bibtex
LATEX2HTML=htlatex
LATEX2HTMLOPTIONS="xhtml,1,frames,info,jpeg" "" "-d./semassist/"

all: semassist.pdf 

semassist.pdf: semassist.tex semassist.bib semassist_overview.tex semassist_install.tex semassist_server.tex semassist_clients.tex semassist_development.tex semassist_troubleshooting.tex pictures/abstraction.jpg pictures/arch_impl.pdf pictures/highlighting.jpg pictures/oolist.jpg pictures/oomenu.jpg pictures/ooparams.jpg pictures/ooresult_clip.jpg pictures/seqInvoke1.pdf pictures/sidenotes.jpg 
	$(LATEX) semassist.tex
	$(BIBTEX) semassist
	$(LATEX) semassist.tex
	$(LATEX) semassist.tex
	$(LATEX) semassist.tex

clean: FORCE
	-rm -f *.aux *.log *.out *.bbl *.toc *.blg *~
	-rm -f semassist.pdf

FORCE:
