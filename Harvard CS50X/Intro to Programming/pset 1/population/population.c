#include <cs50.h>
#include <stdio.h>

int main(void)
{
    // TODO: Prompt for start size
    int startpoint;
    do
    {
        startpoint = get_int("Enter the Starting Population: ");
    }
    while (startpoint < 9);

    // TODO: Prompt for end size
    int endpoint;
    do
    {
        endpoint = get_int("Enter the Ending Population: ");
    }
    while (startpoint > endpoint);

    // TODO: Calculate number of years until we reach threshold
    int totalyears = 0;
    while (startpoint < endpoint)
    {
        startpoint = startpoint + (startpoint / 3) - (startpoint / 4);
        totalyears++;
    }

    printf("Years: %i\n", totalyears);
}
