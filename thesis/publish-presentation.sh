#! /bin/bash

revealjs=reveal.js-3.5.0

mkdir -p output


cat presentation.html | sed "s/&#57344;&#57345;&#57345;//g" > output/index.html

rsync -avc $revealjs output/
rsync -avc img output/

rsync -avc --delete output/ root@jappieklooster.nl:/var/www/html/presents/thesis
