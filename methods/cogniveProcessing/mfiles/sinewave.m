function M = sinewave(vhdim, ncycles)
stepvect = -pi:(2*pi)/(vhdim-1):pi;
x = meshgrid(stepvect);
M = 255*(1+sin(ncycles*x))*.5;
end