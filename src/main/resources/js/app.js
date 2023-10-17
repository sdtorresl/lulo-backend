import 'htmx.org';
import flatpickr from 'flatpickr';
import 'css/app.css';

document.querySelectorAll('.js-dropdown').forEach(($item) => {
    $item.addEventListener('click', (event) => {
        document.querySelectorAll('.js-dropdown').forEach(($dropdown) => {
            if (event.currentTarget === $dropdown ||
                    ($dropdown.getAttribute('data-dropdown-single') !== 'true' && $dropdown.ariaExpanded === 'true')) {
                $dropdown.ariaExpanded = $dropdown.ariaExpanded !== 'true';
                $dropdown.nextElementSibling.classList.toggle('hidden');
            }
        });
        return false;
    });
});

document.querySelectorAll('.js-datepicker, .js-timepicker, .js-datetimepicker').forEach(($item) => {
    const flatpickrConfig = {
        allowInput: true,
        time_24hr: true,
        enableSeconds: true
    };
    if ($item.classList.contains('js-datepicker')) {
        flatpickrConfig.dateFormat = 'Y-m-d';
    } else if ($item.classList.contains('js-timepicker')) {
        flatpickrConfig.enableTime = true;
        flatpickrConfig.noCalendar = true;
        flatpickrConfig.dateFormat = 'H:i:S';
    } else { // datetimepicker
        flatpickrConfig.enableTime = true;
        flatpickrConfig.altInput = true;
        flatpickrConfig.altFormat = 'Y-m-d H:i:S';
        flatpickrConfig.dateFormat = 'Y-m-dTH:i:S';
    }
    flatpickr($item, flatpickrConfig);
});
