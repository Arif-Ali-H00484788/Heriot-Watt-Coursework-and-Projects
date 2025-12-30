def get_int(prompt):
    while True:
        try:
            num = int(input(prompt))
            if 1 <= num <= 8:
                return num
            else:
                print("Height must be between 1 and 8, inclusive.")
        except ValueError:
            print("That's not a valid number!")


height = get_int("Enter the height of the half-pyramid: ")

for i in range(height):
    print(" " * (height - i - 1) + "#" * (i + 1))
