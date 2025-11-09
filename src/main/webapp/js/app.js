// Этот код будет выполнен, когда весь HTML-документ будет загружен и готов
document.addEventListener('DOMContentLoaded', function() {

    // Находим форму регистрации по её ID
    const registrationForm = document.getElementById('registrationForm');

    // Если форма регистрации существует на текущей странице
    if (registrationForm) {
        const password = document.getElementById('password');
        const confirmPassword = document.getElementById('confirmPassword');
        const passwordError = document.getElementById('passwordError');

        // Функция для проверки совпадения паролей
        const validatePasswords = () => {
            if (password.value !== confirmPassword.value) {
                // Если не совпадают, показываем сообщение об ошибке
                confirmPassword.classList.add('is-invalid'); // Добавляем класс Bootstrap для красной рамки
                passwordError.style.display = 'block';
                return false;
            } else {
                // Если совпадают, убираем ошибку
                confirmPassword.classList.remove('is-invalid');
                passwordError.style.display = 'none';
                return true;
            }
        };

        // Добавляем слушатель события на отправку формы
        registrationForm.addEventListener('submit', function(event) {
            // Если пароли не совпали, отменяем отправку формы
            if (!validatePasswords()) {
                event.preventDefault();
            }
        });

        // Также проверяем пароли каждый раз, когда пользователь вводит что-то в поля
        password.addEventListener('input', validatePasswords);
        confirmPassword.addEventListener('input', validatePasswords);
    }
});