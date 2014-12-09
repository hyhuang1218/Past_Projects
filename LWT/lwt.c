#include "lwt_thread.h"

link_list *ready_q; 		//ready queue
link_list *thr_wait_q; 		//thread waiting queue
link_list *sleep_q;         //sleep queue
uchar *pre_sp;              //initial stack pointer for main
thread_t *main_thr;
thread_t *running_thr;

int quant;                  //signal frequency, ualarm
int temp;                   //return value of sigjmp
int mainFinished = 0; 		//1 if main thread is finished
int onlySleep = 0;            //1 if no ready thread and have sleeping thread
int requiredThr;            //the required # of threads
int idgen = 0;                //increasing thread id
int thread_count;           //count the created threads
int sleepingCount = 0; 		//count the sleeping threads

int flag;               //flag for timing
long rs = 0;              //time span between context switch
long minSleep = 0;		//minimum sleeping time in all sleeping threads
struct timeval tv_begin, tv_end; //system time to calculate time span

//**************scheduler, context switch******************//
void scheduler(int sig) {
	printf("\n************Context Switch***********\n");
	
    if(flag) {//get time
		gettimeofday(&tv_begin, NULL);
        flag--;
	}
	else
	{
		gettimeofday(&tv_end, NULL);
        flag++;
	}
	//calculate time span from last switch to this switch in micro secs
	rs = abs(1000000 * (tv_end.tv_sec - tv_begin.tv_sec) + tv_end.tv_usec - tv_begin.tv_usec);
	if(sleepingCount) { //check sleeping threads
		updateSleep();
		while(onlySleep){ //no ready thread, skip switching
			signal(SIGALRM, SIG_IGN); //ignore the sigalrm
			printf("Process is sleeping!\n");
			usleep(minSleep); //sleep for the minimum sleeping time in all sleeping threads in us
			rs = minSleep;
			updateSleep(); 
			signal(SIGALRM, scheduler); //reinstall the sigalrm
			ualarm(quant, quant);
		}
		//restore the time
		gettimeofday(&tv_begin, NULL);
		gettimeofday(&tv_end, NULL);
		
	}
    
    if(running_thr && running_thr->name != "Main") {//save context
		if(sig){ //sig!=0:ualarm signal, add running thread to ready queue
			running_thr->state = 1;
			addThr(ready_q, running_thr);
			printf("Add a thr to ready q:%s%d\n", ready_q->last->name, ready_q->last->thr_id);
		}
		printf("Save context for %s%d\n", running_thr->name, running_thr->thr_id); 
		
		temp = sigsetjmp (running_thr->env, 1); //save context
	}
	
    if(!mainFinished) {//Main thread doesn't complete, continue creating thread
		continueMain();	
    }
    
	if(running_thr && temp) {// temp!=0:called by longjmp, returned to the saved context
		return;
	}

	if(!ready_q->first) {//no other ready thread
		printf("Empty ready q\n");
		if(!sleep_q->first) {
			lwt_exit(); //also no sleeping thread, exit the process
		}
		else { //continue sleeping
			onlySleep = 1;
			running_thr = nil;
			scheduler(0);		
		}		
	} else { //jmp to next ready thread
		running_thr=ready_q->first;
		printf("Switching ....\n");
		printf("\n>>>>>>>>>>>>>>Current running thread: %s%d<<<<<<<<<<<<<<<<<<\n",running_thr->name,running_thr->thr_id);		
		delThr(ready_q,ready_q->first);				
		siglongjmp (running_thr->env, 1);
	}	
}
//*****************initiate the main thread **************************//
int lwt_init(int x, int y){
	//initiate thread list
	thr_wait_q = (link_list *)malloc(sizeof(link_list));
	ready_q = (link_list *)malloc(sizeof(link_list));
	sleep_q = (link_list *)malloc(sizeof(link_list));
	//initiate main thread, allocate the stack
	main_thr = thr_alloc("Main", 0);
	
	//read,write sp
	asm ("movq %%rsp, %0;" :"=r"(pre_sp) );
	asm ("movq %0, %%rsp;"
	: 
	:"r"(main_thr->sp) 
	:"%rsp" 
	);
	running_thr = main_thr;
	quant = x;
	requiredThr = y;
	//install signal
	signal(SIGALRM, scheduler);
	ualarm(quant, quant);

	if(!sigsetjmp (main_thr->env,1)){
		printf("Save context for main thread!\n");
		scheduler(1);
	}
	else{
		running_thr = main_thr;
		return main_thr->thr_id;
	}
}
//create a new thread
int lwt_create(void (*fn)(void*), int argc, void *arg, char *name, int isChild) {
	thread_t *thr = thr_alloc(name, isChild);
	thr->fn = fn;
	thr->argc = argc;
	thr->arg = arg;
	if(isChild) {
		thr->waiting_t = running_thr;
		thrd_wait(1, running_thr);	
        if(!sigsetjmp (running_thr->env, 1)) {
			printf("Save Context for %s %d\n", running_thr->name, running_thr->thr_id);
        } else {
			return 0;
        }
	}
	//default as the running thread which will be excuted imediately
	running_thr = thr;
	//move sp
	asm ("movq %0, %%rsp;"
	: 
	:"r"(running_thr->sp) 
	:"%rsp" 
	);
	thr->fn(thr->arg); //execute the routine
    
	return thr->thr_id;
}

thread_t *thr_alloc(char *name, int isChild) { //allocation for thread
	
	thread_t *thr;
	thr = (thread_t *)malloc(sizeof(thread_t));
	thr->thr_id = ++idgen;
	thr->name = name;
	thr->state = 1;
	thr->wait_count = 0;
	thr->sp = (uchar *)(malloc(REQUIRED_STACK) + REQUIRED_STACK - 64);
	printf("\n****************create thread, name:%s  id:%d**********\n", thr->name, idgen);
	if(!isChild) {
		thread_count++;
	}
	return thr;
}

void lwt_sleep(double sleepTime){ //sleep for seconds
	running_thr->sleepTime = (long)(sleepTime * 1000000);
	printf("%s%d need to sleep for %ld micro secs\n", running_thr->name, running_thr->thr_id, running_thr->sleepTime );
	running_thr->state = 4;
	addThr(sleep_q, running_thr);
	sleepingCount++;

	scheduler(0);	//context switch
	printf("return from sleeping\n"); //longjmp to here
}

void updateSleep(){
	printf("time passed:%ld micro sec\n", rs);
	thread_t *current = sleep_q->first;
	thread_t *next;
	minSleep = current->sleepTime - rs;
	
	while(current){
		current->sleepTime = current->sleepTime-rs;
		if(current->sleepTime <= minSleep){
			minSleep = current->sleepTime;
		}//find the minimum sleeping time
		printf("remain sleeping micro sec for %s%d:%ld\n", current->name, current->thr_id, current->sleepTime );
		next = current->next;
		if(current->sleepTime <= 0){  //finish sleeping
			lwt_wake(current);
		}
		current=next;
	}
}

void lwt_wake(thread_t *t){
	printf("%s%d wake up\n", t->name, t->thr_id);
	delThr(sleep_q, t);
	t->state = 1;
	addThr(ready_q, t);	//add to ready queue
	sleepingCount--;
	onlySleep = 0;  //process wake up
}

void lwt_exit(){
	int isChild=0;
	printf("\n**************start exiting %s%d****************\n", running_thr->name, running_thr->thr_id);
	while(running_thr->waiting_t) {
        isChild = 1;
		thrd_wait(0, running_thr->waiting_t);
		running_thr->waiting_t = nil;
	}
	if(!ready_q->first && !sleep_q->first) { //no ready thread and no sleeping thread
		running_thr->state = 0;
		printf("......%s%d exit successfully!\n", running_thr->name, running_thr->thr_id);
		free(running_thr);
		
		running_thr = nil;
		printf("no available ready thread, exit process!\n");
		
		asm ("movq %0, %%rsp;"
		: 
		:"r"(pre_sp) 
		:"%rsp" 
		);
        
		return; //exit process
	}
	else {	
		running_thr->state = 0;
		printf("......%s%d exit successfully!\n", running_thr->name, running_thr->thr_id);
		free(running_thr);
		
		if(!isChild) {
			thread_count--;
		}

		printf("remain threads:%d\n", thread_count);
		if(sleep_q->first&&!ready_q->first) { //no ready thread, but have sleeping threads
			running_thr=nil;		
			onlySleep=1;
			scheduler(0);
		} else { //have ready threads
			running_thr = ready_q->first;
			delThr(ready_q, ready_q->first);
			printf("\n>>>>>>>>>Current running thread: %s%d<<<<<<<<<\n", running_thr->name, running_thr->thr_id);
			siglongjmp(running_thr->env, 1);	//switching
		}			
	}
}

void thrd_wait(int isWaiting, thread_t *thr ) {
	if(isWaiting) {//need to wait
		(thr->wait_count)++;
		thr->state = 2;
		addThr(thr_wait_q, thr);
		printf("add %s %d to wait q\n",thr->name, thr->thr_id);
	}
	else {//one child thread is terminated
		(thr->wait_count)--;
		if(!thr->wait_count) {//no more child threads
			delThr(thr_wait_q, thr);
			printf("Sub threads are done, move to ready q:%s %d\n\n", thr->name, thr->thr_id);
			thr->state = 1;
			addThr(ready_q, thr);
		}
	}
}

void sema_init(int size) {
	printf("Initiate Semaphore!\n");
	Con_Sema = (sema *)malloc(sizeof(sema));
	Pro_Sema = (sema *)malloc(sizeof(sema));
	Con_Sema->value = 0; //consumer
	Pro_Sema->value = size; //producer

	Con_Sema->sem_q = (link_list *)malloc(sizeof(link_list));
	Pro_Sema->sem_q = (link_list *)malloc(sizeof(link_list));
}

void P(sema *s) {
	if(s->value>0){
		--(s->value);
	} else {
		if(s->value <= 0) {//block
			running_thr->state = 3;
			addThr(s->sem_q, running_thr);
			printf("wait in sema:%s id:%d\n", s->sem_q->last->name, s->sem_q->last->thr_id);
			scheduler(0);
			P(s);
		}				
	}		
}

void V(sema *s) {
	s->value++;
	if((s->value>0) && (s->sem_q->first)){//unblock the waiting thread
		thread_t *wait_sema = s->sem_q->first;
		delThr(s->sem_q, s->sem_q->first);
		wait_sema->state = 1;
		addThr(ready_q, wait_sema);
		printf("unblock sem wait t:%s id:%d\n", ready_q->last->name, ready_q->last->thr_id);
		
	}
}
void continueMain() {
	if((thread_count-1) < requiredThr){
		printf("continue creating\n");
		running_thr = main_thr;
		siglongjmp(main_thr->env, idgen);//no signal
	} else {//finish creating, jump to main to exit
		printf("ready to quit main:%d\n", main_thr->state);
		mainFinished = 1;
		siglongjmp(main_thr->env, idgen);
	}
}
void addThr(link_list *l, thread_t *t) {//add a thread to a link list
	if(l->last) {
		l->last->next = t;
		t->previous = l->last;
	} else {
		l->first = t;
		t->previous = nil;
	}
	l->last = t;
	t->next = nil;
}

void delThr(link_list *l, thread_t *t) {//delete a thread from a link list
    if(t->previous) {
        t->previous->next = t->next;
    } else {
		l->first = t->next;
    }
    if(t->next) {
		t->next->previous = t->previous;
    } else {
		l->last = t->previous;
    }
}

