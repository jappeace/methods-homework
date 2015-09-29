clear all; commandwindow;
trialcount = 3;
stimtime = 1;

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
Screen closeall;
subplot(2,1,1);
plot(results);    
title('raw data');
xlabel('experiment number');
ylabel('time');          
means = [];
standarts = [];

for i = 1:length(results(1,:))
    means(i) = mean(results(:,i));
    standarts(i) = std(results(:,i));
end          
subplot(2,1,2);
bar(means);
title('Average duration');
hold on;

errorbar([1 2 3], means, standarts, 'o');
set(gca, 'xtick', [1 2 3]);
set(gca, 'xticklabel', {'first', 'second', 'third'});

hold off;