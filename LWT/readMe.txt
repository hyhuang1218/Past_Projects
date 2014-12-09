@Author by Hanying Huang
@2013

Light Weight User-Level Thread Scheduling System

This is a lightweight user-level thread scheduling system without the need to modify the Linux kernel. The system allows the creation and concurrent execution of thread of control within a Linux task, in a true preemptive manner. And the LWT system is capable of creating threads and performing mini context switches to share the CPU time among the task threads, based on the round-robin scheduling algorithm.