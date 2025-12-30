#include <cs50.h>
#include <stdio.h>

int main(void)
{
    int userheight;
    int pyrow;
    int pycol;
    int pyspace;
    do
    {
        userheight = get_int("Enter the height here: ");
    }
    while (userheight < 1 || userheight > 8);
    for (pyrow = 0; pyrow < userheight; pyrow++)
    {
        for (pyspace = 0; pyspace < userheight - pyrow - 1; pyspace++)
        {
            printf(" ");
        }
        for (pycol = 0; pycol <= pyrow; pycol++)
        {
            printf("#");
        }
        printf("  ");
        for (pycol = 0; pycol <= pyrow; pycol++)
        {
            printf("#");
        }
        printf("\n");
    }
}
