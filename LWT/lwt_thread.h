#include<stdio.h>
#include<stdlib.h>
#include <setjmp.h>
#include <unistd.h>
#include <signal.h>
#include <errno.h>
#include <string.h>
#include <sys/time.h>

#define REQUIRED_STACK 16 * 1024
#define nil ((void*)0)

typedef struct thread_t thread_t;
typedef struct semaphore sema;
typedef struct thr_link link_list;
typedef unsigned char uchar;

//functions

int lwt_init(int sec, int count);  	//allocate and initialize the required data structure and setup the initial thread
int lwt_create(void (*fn)(void*), int argc, void *arg, char *name, int waitingflag);  //create a new thread
void scheduler(int sig);            //scheduling the threads, context switch
void P(sema *s);                    //decrease the semaphore's value to synchronize the threads.
void V(sema *s);                    //increase the semaphore's value.
void lwt_sleep(double sleepTimeSec);//let the running thread sleep for specified seconds
void sema_init(int buffersize); 	//initiate the semaphores
void lwt_exit();                    //exit the thread

void continueMain();                //continue main thread
void addThr(link_list *l, thread_t *t); //add a thread to a link list
void delThr(link_list *l,thread_t *t);	//delete a thread from a link list
void updateSleep();                     //update the sleeping time for sleeping threads
void lwt_wake(thread_t *t);             //wake up the thread
void thrd_wait(int isWaiting, thread_t *thr); 	//0-no longer wait, 1-need to wait. Let the thread await or finish waiting 
thread_t *thr_alloc(char *name, int isChild);   //initiate the structure and allocate the stack area for the thread

//variables
sema  *Con_Sema, *Pro_Sema; //semaphores for :consumer, producer

//struct
struct thread_t {
    int thr_id;
    char *name;
    int state;              //thread's state:0-exit  1-lwt_READY  2-thrd_WAIT  3-sem_WAIT  4-lwt_SLEEP
    int wait_count;         //the amount of threads of which it is waiting of the termination
    int argc;               //count of arguments
    jmp_buf env;            //context
    uchar *sp;              //stack pointer
    void	(*fn)(void*);  	//routine
    void	*arg;           //argument
    thread_t *waiting_t; 	//the thread which is waiting for its termination
    thread_t *previous;  	//the previous thread in the link list
    thread_t *next;      	//the next thread in the link list
    long sleepTime;     	//remain sleeping time
};

struct semaphore {
	int value;  
	link_list *sem_q;
};

struct thr_link {  	//link list of thread_t
	thread_t *first;
	thread_t *last;
};
