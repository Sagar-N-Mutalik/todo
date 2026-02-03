document.addEventListener("DOMContentLoaded", () => {
    // 1. Auto-focus the input field so you can type immediately
    const inputField = document.querySelector('input[name="title"]');
    if(inputField) {
        inputField.focus();
    }

    // 2. Add a simple animation when deleting (Optional)
    const deleteButtons = document.querySelectorAll('.delete-btn');
    deleteButtons.forEach(btn => {
        btn.addEventListener('click', (e) => {
            // We let the form submit, but you could add a confirm dialog here
            // if (!confirm("Are you sure?")) e.preventDefault();
        });
    });
});