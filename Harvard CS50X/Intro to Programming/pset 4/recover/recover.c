#include <stdbool.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>

// Define the block size for reading the forensic image
#define BLOCK_SIZE 512

// Function to check if a block is a JPEG header
bool is_jpeg_header(uint8_t *block)
{
    // Check for the JPEG magic numbers
    return block[0] == 0xff && block[1] == 0xd8 && block[2] == 0xff;
}

int main(int argc, char *argv[])
{
    // Check for correct usage
    if (argc != 2)
    {
        fprintf(stderr, "Usage: ./recover image\n");
        return 1;
    }

    // Open the forensic image for reading
    FILE *image_file = fopen(argv[1], "r");
    if (image_file == NULL)
    {
        fprintf(stderr, "Could not open %s for reading\n", argv[1]);
        return 1;
    }

    // Initialize variables
    uint8_t block[BLOCK_SIZE];
    int image_count = 0;
    FILE *output_file = NULL;

    // Read the forensic image block by block
    while (fread(block, BLOCK_SIZE, 1, image_file) == 1)
    {
        // Check if the block is a JPEG header
        if (is_jpeg_header(block))
        {
            // Close the previous output file, if any
            if (output_file != NULL)
            {
                fclose(output_file);
            }

            // Create a new output file
            char filename[8];
            sprintf(filename, "%03i.jpg", image_count);
            output_file = fopen(filename, "w");
            if (output_file == NULL)
            {
                fprintf(stderr, "Could not open %s for writing\n", filename);
                return 1;
            }

            // Write the JPEG header to the output file
            fwrite(block, BLOCK_SIZE, 1, output_file);

            // Increment the image count
            image_count++;
        }
        else if (output_file != NULL)
        {
            // Write the block to the current output file
            fwrite(block, BLOCK_SIZE, 1, output_file);
        }
    }

    // Close the last output file, if any
    if (output_file != NULL)
    {
        fclose(output_file);
    }

    // Close the forensic image file
    fclose(image_file);

    return 0;
}
