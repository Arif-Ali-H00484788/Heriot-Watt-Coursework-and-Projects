// Part 1: Multiple Choice
const answerButtons = document.querySelectorAll('.answer-button');
const feedback1 = document.getElementById('feedback1');

answerButtons.forEach(button => {
    button.addEventListener('click', () => {
        const correctAnswer = 'Paris';
        const userAnswer = button.textContent;
        if (userAnswer === correctAnswer) {
            button.style.backgroundColor = 'green';
            feedback1.textContent = 'Correct!';
        } else {
            button.style.backgroundColor = 'red';
            feedback1.textContent = 'Incorrect';
        }
    });
});

// Part 2: Free Response
const freeResponseInput = document.getElementById('free-response');
const submitButton = document.getElementById('submit-button');
const feedback2 = document.getElementById('feedback2');

submitButton.addEventListener('click', () => {
    const correctAnswer = 'Leonardo da Vinci';
    const userAnswer = freeResponseInput.value.trim();
    if (userAnswer === correctAnswer) {
        freeResponseInput.style.borderColor = 'green';
        feedback2.textContent = 'Correct!';
    } else {
        freeResponseInput.style.borderColor = 'red';
        feedback2.textContent = 'Incorrect';
    }
});
