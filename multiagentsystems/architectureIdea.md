# Metal architecture
Input is provided by sockets, uncontrolled
Layers are processes.
Use DBus for IPC. <-- so this includes networking

A single process is in controll of motion, it decides it
by having some built in priorities for requesting processes,
but they can also ask nicely which can increase priority.
