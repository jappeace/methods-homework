#! /bin/bash

echo "open the viewer"
qpdfview thesis.pdf &

echo "waiting for the viewer to open"
sleep 1

echo "start building continiously"
latexmk -pdf -pdflatex="pdflatex --shell-escape %O %S" -pvc -gg -f thesis.tex
