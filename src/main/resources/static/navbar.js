const hamburger = document.querySelector('.hamburger');
const menu = document.querySelector('.menu');

hamburger.addEventListener('click', () => {
    menu.classList.toggle('active');
});

// Función para toggle del dropdown del perfil
function toggleProfileDropdown(event) {
    event.preventDefault();
    event.stopPropagation();
    
    console.log('toggleProfileDropdown called');
    const dropdown = document.getElementById('profileDropdown');
    console.log('dropdown element:', dropdown);
    
    if (dropdown) {
        dropdown.classList.toggle('show');
        console.log('dropdown classes after toggle:', dropdown.className);
        
        // Forzar estilos si es necesario
        if (dropdown.classList.contains('show')) {
            dropdown.style.display = 'block';
            dropdown.style.backgroundColor = 'white';
            dropdown.style.color = '#333';
            dropdown.style.zIndex = '1001';
            console.log('Dropdown should be visible now');
        } else {
            dropdown.style.display = 'none';
            console.log('Dropdown hidden');
        }
    } else {
        console.error('Dropdown element not found');
    }
}

// Cerrar dropdown al hacer clic fuera de él
document.addEventListener('click', function(event) {
    const dropdown = document.getElementById('profileDropdown');
    const profileIcon = document.querySelector('.profile-icon');
    
    if (dropdown && !profileIcon.contains(event.target) && !dropdown.contains(event.target)) {
        dropdown.classList.remove('show');
    }
});

// Cerrar dropdown al presionar Escape
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        const dropdown = document.getElementById('profileDropdown');
        if (dropdown) {
            dropdown.classList.remove('show');
        }
    }
});