#! /bin/bash

revealjs=reveal.js-3.5.0
socketio=socket.io-1.4.5.js

mkdir -p output

cat presentation_client.html | sed "s/&#57344;&#57345;&#57345;//g" > output/index.html
cat presentation.html | sed "s/&#57344;&#57345;&#57345;//g" > output/master.html

rsync -avc $revealjs output/
rsync -avc img output/


rsync -avc --delete output/ root@jappieklooster.nl:/var/www/html/presents/thesis
