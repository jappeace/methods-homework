
## What does ';' do in MATLAB? What does '=' do in MATLAB? What happens after a
'%' in MATLAB? What does the function imagesc do?

; end of statement / supress output
= assignment off value
% comment
imagesc create an image off the matrix


## Can you think of an experiment for which you would need two monitors?

Testing if the two monitors work. Also testing two eyes with different inputs, and
see what is remembered.

##Why would it be useful to put an image into video memory before sending it to the screen?

This technique is called backbuffering. Its used to prevent flickering of the screen.
If you don't do it a part of the image is sometimes rendered while its already shown
causing part of the screen to be empty which causes a small flickering. I know
because I've written games that used to suffer this problem.

# A simple experiment
## What will be the percentage correct of our threshold?

33.3% because we use 3-1 staircase?

##What values can get a vertical orientation
180,0

##What does the abbreviation ISI mean?
In Seconds Interval

##Why do you need a fixation cross?
To make sure the subject looks at the right location?

##How many inputs does the function fixationcross.m need?
4

##Why is the text between ''
Because its a string, if you don't do that the interpreter will crash
because its not sure if you're talking about variables.

If you don't want to use '' for strings I recommend some bash.


##How can you stop the program from drawing a fixation cross on top of the text.
Clear the screen. Flip some gray into it.

#The staircase
## Why doe you change the step size?
Because You want get closer at how good the subject is at the experiment.

## Why is the reversal method useful?
Because it allows the user to recover from a silly mistake but it also allows
the program to detect when a cap has been reached on difficulty.

## What does == mean?
Compare left side to ride side

## What can you do with an if-statement?
if the statement behind the if is true than the program will go inside the
block, if not it won't. If an else is present it will go there instead.

# Multiple staircases
## Why do you want to run more than one staircase of the same condition?
To compare it against something

## Why is it a good thing to present staircases randomly interleaved?
It makes it more difficult for subjects to cheat if they're familiar with the setup

## What did you just do?
Select the elements of 1 array with another

## What variable do we use to select the data?
criterion

## what do the hold commands do?
hold on always writing over the previous graph, hold off disables this again.

## Did the staircases converge well?
No mainly because I didn't want to do the experiment and started rigging it
based on output.

## Do you see any difference between the thresholds?
Oblique had horrible performance compared to the rest.

## Google it, does your experiment confirm this?
Well yes, definitely, even while I tried to rig it to exit the experiments
as quickly as possible, I had serious issues doing this with the oblique lines.

# Moving & coloured stimuli
