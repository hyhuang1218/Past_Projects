#include"lwt_thread.h"
#define NUM_THREADS 5
#define PRODUCERS 3
#define CONSUMERS NUM_THREADS-PRODUCERS
#define BUFFER_SIZE 8
#define quantum 50000
#define MAX_INPUT 18
int test_int=0; //input
int test_out=0; //output
int test_wait=0;//result for child threads
int in = 0;	//index for next input
int out = 0;	//index for next output
int buffer[BUFFER_SIZE]; //int buffer

void producertest();

void producer() {//generate input to buffer	
	int nextProduced;	
	while(1){
		
		if(test_int<MAX_INPUT){		
			P(Pro_Sema);
		}
		if(test_int>=MAX_INPUT){ //complete producing
			Pro_Sema->value=0;
			printf("Finish producing required %d items!Exiting...\n",MAX_INPUT);
			
			break;	
		}	
							
		printf("Pro_Sema producer:%d\n",Pro_Sema->value);
		test_int++; 
			
		nextProduced=test_int;	
		buffer[in] = test_int;
		in = (in+1)%BUFFER_SIZE;
		
		V(Con_Sema);
		printf("Con_Sema consumer:%d\n",Con_Sema->value);		
		printf("Input:%d\n",test_int);
		if(test_int==10)  //create child threads
			lwt_create(&producertest,0,NULL, "SubProducer",1);
		
		
		sleep(1);
		if(test_int%(MAX_INPUT/3)==0&&test_int!=MAX_INPUT)
		{
			lwt_sleep(2); //thread sleep (seconds)	
		}
		
	}
	lwt_exit();		
}

void consumer(){ //get output from the buffer
		
	int nextConsumed;
	while(1){
		if(test_out<MAX_INPUT){
			
			P(Con_Sema);}
		if(test_out>=MAX_INPUT){//complete cosuming
			printf("Finish Consuming All %d items!Exiting...\n", MAX_INPUT);
			break;	
		}
		printf("Con_Sema consumer:%d\n",Con_Sema->value);
	
		nextConsumed=buffer[out];
		test_out=nextConsumed;
		out=(out+1)%BUFFER_SIZE;
		if(test_int<MAX_INPUT)
			V(Pro_Sema);
		printf("Pro_Sema producer:%d\n",Pro_Sema->value);				
		printf("Output: %d\n ",test_out);
				
		sleep(1);
	}
	lwt_exit();
}
void producertest() {	//test child threads -thrd_WAIT
		
	int i;
	if(test_wait==0)
	{
		test_wait++;
		lwt_create(&producertest,0,NULL, "SubProducer",1); //create a child thread
	}	
	for(i=0;i<=3;i++)
		printf("testwait:%d\n",i); //output
	lwt_exit();		
}

int main(int argc, char *argv[]) {
	int i=0;
			  
	int main_id=lwt_init(quantum,NUM_THREADS);
	sema_init(BUFFER_SIZE);	
	//printf("go here?\n");
	for(;i<PRODUCERS;i++){ //create producers
		lwt_create(&producer,0,NULL, "Producer",0);
		
	}
	for(;i<NUM_THREADS;i++){ //create consumers
		lwt_create(&consumer,0,NULL,"Consumer",0);
		
	}

	lwt_exit();
	return 0;
}



