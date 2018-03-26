$(document).ready(function() {
    changePageAndSize();
    changePageAndButtons();
});

function changePageAndSize() {
    $('#pageSizeSelect').change(function(evt) {
        window.location.replace("/admin/advertisements/?pageSize=" + this.value + "&page=1&buttonsToShow=" + $("#buttonsSelect option:selected").text());
    });
}

function changePageAndButtons() {
    $('#buttonsSelect').change(function(evt) {
        window.location.replace("/admin/advertisements/?pageSize=" + $("#pageSizeSelect option:selected").text() + "&page=1&buttonsToShow=" + this.value );
    });
}
