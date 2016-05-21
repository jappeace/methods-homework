#! /bin/bash
for file in `ls results/*.plot`; do gnuplot -e "set terminal pngcairo size 800,400 enhanced font 'Verdana,10'; set output '"$file".png" $file; done
