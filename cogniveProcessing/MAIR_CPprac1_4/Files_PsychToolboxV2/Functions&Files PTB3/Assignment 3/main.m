clear all; commandwindow;
trialcount = 10;
results = zeros(trialcount,3);
[w,screenrect] = openwindow();
for i = 1:trialcount
    disp(strcat('trial: ', num2str(i)));
    results(i,:) = concentricmotiontrial(1, w, screenrect);
end
Screen closeall;
plot(results);      