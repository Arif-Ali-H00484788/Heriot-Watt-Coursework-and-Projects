// Implements a dictionary's functionality

#include <ctype.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "dictionary.h"

// Represents a node in a hash table
typedef struct node
{
    char word[LENGTH + 1];
    struct node *next;
} node;

// Choose number of buckets in hash table
const unsigned int N = 26;

// Hash table
node *table[N];

// Returns true if word is in dictionary, else false
bool check(const char *word)
{
    // Convert the word to lowercase
    char lower_word[LENGTH + 1];
    for (int i = 0; i < strlen(word); i++)
    {
        lower_word[i] = tolower(word[i]);
    }
    lower_word[strlen(word)] = '\0';

    // Hash the word and search for it in the hash table
    unsigned int hash_value = hash(lower_word);
    node *current = table[hash_value];
    while (current)
    {
        if (strcmp(current->word, lower_word) == 0)
        {
            return true;
        }
        current = current->next;
    }

    return false;
}

// Hashes word to a number
unsigned int hash(const char *word)
{
    // Use a simple hash function based on the first letter of the word
    return (word[0] - 'a') % N;
}

// Loads dictionary into memory, returning true if successful, else false
bool load(const char *dictionary)
{
    // Open the dictionary file
    FILE *file = fopen(dictionary, "r");
    if (!file)
    {
        return false;
    }

    // Read each word from the dictionary file
    char word[LENGTH + 1];
    while (fscanf(file, "%s", word) == 1)
    {
        // Hash the word and store it in the hash table
        unsigned int hash_value = hash(word);
        node *new_node = malloc(sizeof(node));
        if (!new_node)
        {
            return false;
        }
        strcpy(new_node->word, word);
        new_node->next = table[hash_value];
        table[hash_value] = new_node;
    }

    // Close the dictionary file
    fclose(file);
    return true;
}

// Returns number of words in dictionary if loaded, else 0 if not yet loaded
unsigned int size(void)
{
    // Initialize the word count to 0
    unsigned int word_count = 0;

    // Iterate over each bucket in the hash table
    for (int i = 0; i < N; i++)
    {
        // Iterate over each node in the linked list
        node *current = table[i];
        while (current)
        {
            word_count++;
            current = current->next;
        }
    }

    return word_count;
}

// Unloads dictionary from memory, returning true if successful, else false
bool unload(void)
{
    // Iterate over each bucket in the hash table
    for (int i = 0; i < N; i++)
    {
        // Iterate over each node in the linked list
        node *current = table[i];
        while (current)
        {
            node *next = current->next;
            free(current);
            current = next;
        }
    }

    return true;
}
