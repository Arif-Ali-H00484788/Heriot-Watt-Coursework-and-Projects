from cs50 import get_string


def main():
    text = get_string("Text: ")

    # Count letters, words, and sentences
    letters = 0
    words = 0
    sentences = 0
    for char in text:
        if char.isalpha():
            letters += 1
        elif char == ' ':
            words += 1
        elif char in ['.', '!', '?']:
            sentences += 1

    # Adjust word count (since we counted spaces, not words)
    words += 1

    # Calculate Coleman-Liau index
    L = (letters / words) * 100
    S = (sentences / words) * 100
    index = 0.0588 * L - 0.296 * S - 15.8

    # Round index to nearest integer
    index = round(index)

    # Print result
    if index < 1:
        print("Before Grade 1")
    elif index >= 16:
        print("Grade 16+")
    else:
        print(f"Grade {index}")


if __name__ == "__main__":
    main()
