clear all; commandwindow;
trialcount = 10;
stimtime = 20;

results = zeros(trialcount,3);
[w,screenrect] = openwindow();

writetexttoscreen('Focus on the cross.', -30, w, screenrect);
writetexttoscreen('Press space when the motion stops.', 0, w, screenrect);
writetexttoscreen('Press space to start.', 30, w,screenrect);
flips(w,screenrect);

waituntilspacepress;


for i = 1:trialcount
    disp(strcat('trial: ', num2str(i)));
    results(i,:) = concentricmotiontrial(stimtime, w, screenrect);
end
save('results','results');
Screen closeall;