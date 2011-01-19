# Semantic Assistants Documentation -- Makefile
#
# This file is part of the Semantic Assistants architecture.
#
# Copyright (C) 2009, 2010, 2011 Semantic Software Lab, http://www.semanticsoftware.info
#
# The Semantic Assistants architecture is free software: you can
# redistribute and/or modify it under the terms of the GNU Affero General
# Public License as published by the Free Software Foundation, either
# version 3 of the License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
# Copyright (c) 2009, 2010, 2011 Semantic Software Lab, http://www.semanticsoftware.info

LATEX=pdflatex
BIBTEX=bibtex
LATEX2HTML=htlatex
LATEX2HTMLOPTIONS="xhtml,1,frames,info,jpeg" "" "-d./semassist/"

all: semassist.pdf 

semassist.pdf: semassist.tex semassist.bib semassist_overview.tex semassist_install.tex semassist_server.tex semassist_clients.tex semassist_services.tex semassist_development.tex semassist_troubleshooting.tex pictures/abstraction.jpg pictures/arch_impl.pdf pictures/highlighting.jpg pictures/oolist.jpg pictures/oomenu.jpg pictures/ooparams.jpg pictures/ooresult_clip.jpg pictures/seqInvoke1.pdf pictures/sidenotes.jpg 
	$(LATEX) semassist.tex
	$(BIBTEX) semassist
	$(LATEX) semassist.tex
	$(LATEX) semassist.tex
	$(LATEX) semassist.tex

clean: FORCE
	-rm -f *.aux *.log *.out *.bbl *.toc *.blg *~
	-rm -f semassist.pdf

FORCE:
