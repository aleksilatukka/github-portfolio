#include <stdio.h>
#include <stdlib.h>
#include <time.h>

void print_separator() {
    printf("========================================\n");
}

int main() {
    int n, guess, user_score = 0, comp_score = 0;
    char input;

    print_separator();
    printf("Welcome to the Number Guesser Game! :)\n");
    print_separator();

    while (1) {

        // Get input from user
        printf("Do you want to play? (Y/N): ");
        scanf(" %c", &input);

        // Check input
        if (input != 'Y' && input != 'y') {
            if (input == 'N' || input == 'n') {
                printf("Thank you for playing!\n");
            } else {
                printf("Invalid input. Please enter Y or N.\n");
                continue;
            }
            break;
        }

        // Create random number.
        srand(time(NULL));
        n = rand() % 100 + 1;

        // Game logic
        for (int i = 1; i <= 10; i++) {
            printf("\nGuess the number between 1-100 (Attempts left: %d): ", 11 - i);
            if (scanf("%d", &guess) != 1) {
                printf("Invalid input. Please enter a number.\n");
                while (getchar() != '\n');
                i--;
                continue;
            }
            if (i == 10) {
                printf("You're out of guesses. The correct number was %d.\n", n);
                comp_score++;
                break;
            }
            if (guess < n) {
                printf("You need to guess a bigger number.\n");
            } else if (guess > n) {
                printf("You need to guess a smaller number.\n");
            } else {
                printf("Congratulations, you guessed right!\n");
                user_score++;
                break;
            }
        }

        printf("\nScore: User %d - Computer %d\n", user_score, comp_score);
    }

    return 0;
}
