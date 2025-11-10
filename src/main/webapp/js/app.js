document.addEventListener('DOMContentLoaded', function() {
    initRegistrationForm();
    initExpensesPage();
    initExpenseForm();
    initAutoCloseAlerts();
    initFormValidation();
});

function initRegistrationForm() {
    const registrationForm = document.getElementById('registrationForm');
    if (!registrationForm) return;

        const password = document.getElementById('password');
        const confirmPassword = document.getElementById('confirmPassword');
        const passwordError = document.getElementById('passwordError');

        const validatePasswords = () => {
            if (password.value !== confirmPassword.value) {
            confirmPassword.classList.add('is-invalid');
                passwordError.style.display = 'block';
                return false;
            } else {
                confirmPassword.classList.remove('is-invalid');
                passwordError.style.display = 'none';
                return true;
            }
        };

        registrationForm.addEventListener('submit', function(event) {
            if (!validatePasswords()) {
                event.preventDefault();
            }
        });

        password.addEventListener('input', validatePasswords);
        confirmPassword.addEventListener('input', validatePasswords);
    }

function initExpensesPage() {
    const expensesTable = document.getElementById('expensesTable');
    if (!expensesTable) return;

    calculateTotalExpenses();
    initExpensesFilter();
    initExpensesSort();
    initCategoryStats();
}

function calculateTotalExpenses() {
    const expensesTable = document.getElementById('expensesTable');
    if (!expensesTable) return;

    const rows = expensesTable.querySelectorAll('tbody tr:not(#totalRow)');
    let total = 0;

    rows.forEach(row => {
        if (row.style.display !== 'none') {
            const amountCell = row.cells[1];
            if (amountCell) {
                const amountText = amountCell.textContent.trim();
                const amount = parseFloat(amountText.replace(/[^\d.,]/g, '').replace(',', '.'));
                if (!isNaN(amount)) {
                    total += amount;
                }
            }
        }
    });

    const tbody = expensesTable.querySelector('tbody');
    if (!tbody) return;

    let totalRow = document.getElementById('totalRow');
    
    if (total > 0) {
        if (!totalRow) {
            totalRow = document.createElement('tr');
            totalRow.id = 'totalRow';
            totalRow.className = 'table-info fw-bold';
            totalRow.innerHTML = `
                <td colspan="2" class="text-end">Итого:</td>
                <td>${formatCurrency(total)}</td>
                <td colspan="3"></td>
            `;
            tbody.appendChild(totalRow);
        } else {
            const totalCell = totalRow.cells[2];
            if (totalCell) {
                totalCell.textContent = formatCurrency(total);
            }
        }
    } else if (totalRow) {
        totalRow.remove();
    }
}

function formatCurrency(amount) {
    return new Intl.NumberFormat('ru-RU', {
        style: 'currency',
        currency: 'RUB',
        minimumFractionDigits: 2
    }).format(amount);
}

function initExpensesFilter() {
    const expensesTable = document.getElementById('expensesTable');
    if (!expensesTable) return;

    const header = expensesTable.closest('.container') || document.querySelector('main');
    if (!header) return;

    const searchInput = document.createElement('input');
    searchInput.type = 'text';
    searchInput.className = 'form-control mb-3';
    searchInput.placeholder = 'Поиск по описанию, категории или пользователю...';
    searchInput.id = 'expenseSearch';

    const tableContainer = expensesTable.parentElement;
    tableContainer.insertBefore(searchInput, expensesTable);

    searchInput.addEventListener('input', function() {
        const searchTerm = this.value.toLowerCase().trim();
        const rows = expensesTable.querySelectorAll('tbody tr:not(#totalRow)');

        rows.forEach(row => {
            const description = row.cells[3]?.textContent.toLowerCase() || '';
            const category = row.cells[2]?.textContent.toLowerCase() || '';
            const user = row.cells[4]?.textContent.toLowerCase() || '';

            if (description.includes(searchTerm) || 
                category.includes(searchTerm) || 
                user.includes(searchTerm)) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });

        const totalRow = document.getElementById('totalRow');
        if (totalRow) {
            totalRow.style.display = searchTerm ? 'none' : '';
        }

        calculateTotalExpenses();
    });
}

function initExpensesSort() {
    const expensesTable = document.getElementById('expensesTable');
    if (!expensesTable) return;

    const headers = expensesTable.querySelectorAll('thead th');
    headers.forEach((header, index) => {
        if (index < 5) {
            header.style.cursor = 'pointer';
            header.style.userSelect = 'none';
            header.innerHTML += ' <span class="sort-indicator">↕</span>';

            header.addEventListener('click', function() {
                sortTable(expensesTable, index);
            });
        }
    });
}

function sortTable(table, columnIndex) {
    const tbody = table.querySelector('tbody');
    const rows = Array.from(tbody.querySelectorAll('tr:not(#totalRow)'));
    const totalRow = document.getElementById('totalRow');
    
    if (totalRow) {
        totalRow.remove();
    }

    const isAscending = table.dataset.sortOrder !== 'asc' || table.dataset.sortColumn != columnIndex;
    table.dataset.sortOrder = isAscending ? 'asc' : 'desc';
    table.dataset.sortColumn = columnIndex;

    rows.sort((a, b) => {
        const aText = a.cells[columnIndex]?.textContent.trim() || '';
        const bText = b.cells[columnIndex]?.textContent.trim() || '';

        let comparison = 0;
        if (columnIndex === 1) {
            const aNum = parseFloat(aText.replace(/[^\d.,]/g, '').replace(',', '.'));
            const bNum = parseFloat(bText.replace(/[^\d.,]/g, '').replace(',', '.'));
            comparison = aNum - bNum;
        } else if (columnIndex === 0) {
            comparison = new Date(aText) - new Date(bText);
        } else {
            comparison = aText.localeCompare(bText, 'ru');
        }

        return isAscending ? comparison : -comparison;
    });

    rows.forEach(row => tbody.appendChild(row));
    if (totalRow) {
        tbody.appendChild(totalRow);
    }

    updateSortIndicators(table, columnIndex, isAscending);
    calculateTotalExpenses();
}

function updateSortIndicators(table, columnIndex, isAscending) {
    const headers = table.querySelectorAll('thead th');
    headers.forEach((header, index) => {
        const indicator = header.querySelector('.sort-indicator');
        if (indicator) {
            if (index === columnIndex) {
                indicator.textContent = isAscending ? ' ↑' : ' ↓';
            } else {
                indicator.textContent = ' ↕';
            }
        }
    });
}

function initCategoryStats() {
    const expensesTable = document.getElementById('expensesTable');
    if (!expensesTable) return;

    const categoryStats = {};
    const rows = expensesTable.querySelectorAll('tbody tr:not(#totalRow)');

    rows.forEach(row => {
        const category = row.cells[2]?.textContent.trim() || '';
        const amountText = row.cells[1]?.textContent.trim() || '';
        const amount = parseFloat(amountText.replace(/[^\d.,]/g, '').replace(',', '.'));

        if (!isNaN(amount)) {
            categoryStats[category] = (categoryStats[category] || 0) + amount;
        }
    });

    if (Object.keys(categoryStats).length > 0) {
        const statsContainer = document.createElement('div');
        statsContainer.className = 'alert alert-info mt-3';
        statsContainer.innerHTML = '<strong>Расходы по категориям:</strong><br>';

        const sortedCategories = Object.entries(categoryStats)
            .sort((a, b) => b[1] - a[1])
            .slice(0, 5);

        sortedCategories.forEach(([category, amount]) => {
            const percentage = ((amount / Object.values(categoryStats).reduce((a, b) => a + b, 0)) * 100).toFixed(1);
            statsContainer.innerHTML += `${category}: ${formatCurrency(amount)} (${percentage}%)<br>`;
        });

        expensesTable.parentElement.appendChild(statsContainer);
    }
}

function initExpenseForm() {
    const addExpenseForm = document.getElementById('addExpenseForm');
    const editExpenseForm = document.querySelector('form[action*="/expenses/edit"]');

    [addExpenseForm, editExpenseForm].forEach(form => {
        if (!form) return;

        const amountInput = form.querySelector('input[name="amount"]');
        const descriptionInput = form.querySelector('input[name="description"]');

        if (amountInput) {
            amountInput.addEventListener('input', function() {
                const value = parseFloat(this.value);
                if (value < 0) {
                    this.setCustomValidity('Сумма не может быть отрицательной');
                    this.classList.add('is-invalid');
                } else if (value === 0) {
                    this.setCustomValidity('Сумма должна быть больше нуля');
                    this.classList.add('is-invalid');
                } else {
                    this.setCustomValidity('');
                    this.classList.remove('is-invalid');
                }
            });

            amountInput.addEventListener('blur', function() {
                if (this.value && parseFloat(this.value) > 0) {
                    this.value = parseFloat(this.value).toFixed(2);
                }
            });
        }

        if (descriptionInput) {
            const charCounter = document.createElement('small');
            charCounter.className = 'text-muted';
            charCounter.id = 'descriptionCounter';
            descriptionInput.parentElement.appendChild(charCounter);

            const updateCounter = () => {
                const length = descriptionInput.value.length;
                charCounter.textContent = `${length}/255 символов`;
                if (length > 255) {
                    charCounter.classList.add('text-danger');
                    descriptionInput.setCustomValidity('Описание не может превышать 255 символов');
                    descriptionInput.classList.add('is-invalid');
                } else {
                    charCounter.classList.remove('text-danger');
                    descriptionInput.setCustomValidity('');
                    descriptionInput.classList.remove('is-invalid');
                }
            };

            descriptionInput.addEventListener('input', updateCounter);
            updateCounter();
        }

        form.addEventListener('submit', function(event) {
            if (amountInput && parseFloat(amountInput.value) <= 0) {
                event.preventDefault();
                alert('Сумма должна быть больше нуля');
                return false;
            }
        });
    });
}

function initAutoCloseAlerts() {
    const alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
    alerts.forEach(alert => {
        setTimeout(() => {
            if (alert && !alert.classList.contains('show')) {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            }
        }, 5000);
    });
}

function initFormValidation() {
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });
}
