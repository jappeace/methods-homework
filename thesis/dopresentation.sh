#! /bin/bash

# there is a bug where code blocks have this line ending: &#57344;&#57345;&#57345;
# we delete that with sed
# we can't write directly into the same file we're reading from.
# therefore we work around it like this:
# do the operation
cat presentation.html | sed "s/&#57344;&#57345;&#57345;//g" > presentation.html.result
# take the pipe result and overwrite original
cp presentation.html.result presentation.html
# remove pipe result
rm presentation.html.result

# this opens a new tab in firefox
firefox presentation.html
