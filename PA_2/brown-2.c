#include <stdio.h>
#include <pthread.h>
#include <sys/types.h>
#include <unistd.h>
#include <stdlib.h>

pthread_t thread;
pthread_cond_t c1, p1;
pthread_mutex_t mutex;

struct node {
	int num;

	struct node *next;
	struct node *prev;
};

int size = 0;
struct node *head = NULL;
struct node *tail = NULL;
struct node *current = NULL;

//create a new node.  Input parameter x is either 0 or 1 to determine if
//the number contained in the node is odd or even
struct node* createNode(int x){
	int data;
	if (x == 0){
		data = ((rand() % 20)) * 2;
		//printf("%d\n", data);
	}else{
		data = ((rand() % 20)) * 2 + 1;
		//printf("%d\n", data);
	}
	struct node* newNode = (struct node*)malloc(sizeof(struct node));
	newNode->num = data;
	newNode->next = NULL;
	newNode->prev = NULL;

	return newNode;
}

//Print out the contents of the list
void printList(){
	struct node* temp = head;
	printf("(List size = %d) ", size);
	while(temp != NULL){
		printf("%d ",temp->num);
		temp = temp->next;
	}
	printf("\n");
}

//Add a node to the end of the list.  Takes in either 0 or 1 to determine if 
//the number in the node is odd or even.
void add(int x){
	current = head;
	struct node* newNode = createNode(x);
	if (head == NULL){
		head = newNode;
		size++;
	}else{
		while(current->next != NULL){
			current = current->next;
		}
		current->next = newNode;
		newNode->prev = current;
		size++;
	}
	tail = newNode;
}
//Delete a node from the front of the list
void delete(){
	current = head;
	if (head == NULL){
		printf("The list is empty.");
		return;
	}
	else if (head->next == NULL){
		head = NULL;
		size = 0;
	}else{
		head->next->prev = NULL;
		head = head->next;
		size--;
	}
}

//add node containing an odd interger < 40 to end of list
void producer1(void *ptr){
	while(1){
		if (size >= 20){
			printf("PRODUCER1: Buffer Full.\n");
			sleep(5);
			//exit(0);
		}
		pthread_mutex_lock(&mutex);
		/**/while (size >= 20) pthread_cond_wait(&p1, &mutex);
		add(1);
		printf("Producer1 added a node\n");
		printList();
		//sleep(1);
		pthread_cond_broadcast(&c1);
		pthread_mutex_unlock(&mutex);
		sleep(1);
	}
}
//add node containing an even interger < 40 to end of list
void producer2(void *ptr){
	while(1){
		if (size >= 20){
			printf("PRODUCER2: Buffer Full.\n");
			sleep(5);
			//exit(0);
		}
		pthread_mutex_lock(&mutex);
		/**/while (size >= 20 ) pthread_cond_wait(&p1, &mutex);
		add(0);
		printf("Producer2 added a node\n");
		printList();
		//sleep(1);
		pthread_cond_broadcast(&c1);
		pthread_mutex_unlock(&mutex);
		sleep(1);
	}

}
//delete node from beginning of list if it is odd, otherwise wait.
void consumer1(void *ptr){
	while(1){
		if (size == 0 ){
			printf("CONSUMER1: Buffer is empty.\n");
			sleep(5);
			//exit(0);
		}else{
			pthread_mutex_lock(&mutex);
			while (head->num % 2 == 0) pthread_cond_wait(&c1, &mutex);
			delete();
			printf("Consumer1 removed a node\n");
			printList();
			//sleep(1);
			pthread_cond_broadcast(&p1);
			pthread_mutex_unlock(&mutex);
			sleep(1);
		}

	}

}
//delete node from beginning of list if it is even, otherwise wait.
void consumer2(void *ptr){
	while(1){
		if (size == 0 ){
			printf("CONSUMER2: Buffer is empty.\n");
			sleep(5);
			//exit(0);
		}else{
			pthread_mutex_lock(&mutex);
			while (head->num % 2 != 0) pthread_cond_wait(&c1, &mutex);
			delete();
			printf("Consumer2 removed a node\n");
			printList();
			//sleep(1);
			pthread_cond_broadcast(&p1);
			pthread_mutex_unlock(&mutex);
			sleep (1);
		}
	}

}

main() {
	head = NULL;
	srand(time(NULL));
	pthread_mutex_init(&mutex, 0);
	pthread_cond_init(&c1, 0);
	pthread_cond_init(&p1, 0);
	for (int i = 0; i < 3; i++){
		int x = (rand() % 2);
		add(x);
	}
	printf("Initial List: ");
	printList();
	pthread_t prod1, prod2, con1, con2;
	pthread_mutex_init(&mutex, 0);
	pthread_cond_init(&c1, 0);
	pthread_cond_init(&p1, 0);
	pthread_create(&con1, 0, (void *)consumer1, 0);
	pthread_create(&con2, 0, (void *)consumer2, 0);
	pthread_create(&prod1, 0, (void *)producer1, 0);
	pthread_create(&prod2, 0, (void *)producer2, 0);
	pthread_join(prod2, 0);
	pthread_join(prod1, 0);
	pthread_join(con2, 0);
	pthread_join(con1, 0);
	pthread_cond_destroy(&c1);
	pthread_cond_destroy(&p1);
	pthread_mutex_destroy(&mutex);

}
