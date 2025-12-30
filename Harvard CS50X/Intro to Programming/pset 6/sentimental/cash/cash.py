from cs50 import get_float


def min_coins(change):
    """Calculate the minimum number of coins needed to make the given amount of change."""
    coins = [25, 10, 5, 1]  # Available coin denominations
    count = 0
    for coin in coins:
        count += int(change // coin)
        change %= coin
    return count


def main():
    while True:
        change = get_float("Change owed: ")
        if change >= 0:
            break
    change = int(change * 100)  # Convert dollars to cents
    min_coins_needed = min_coins(change)
    print(min_coins_needed)


if __name__ == "__main__":
    main()
