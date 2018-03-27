$(document).ready(function() {
    changePageAndSize();
    changePageAndButtons();
});

function changePageAndSize() {
    $('#itemsOnPageSelect').change(function(evt) {
        window.location.replace("/admin/advertisements/?itemsOnPage=" + this.value + "&currentPage=1&buttonsToShow=" + $("#buttonsToShowSelect option:selected").text());
    });
}

function changePageAndButtons() {
    $('#buttonsToShowSelect').change(function(evt) {
        window.location.replace("/admin/advertisements/?itemsOnPage=" + $("#itemsOnPageSelect option:selected").text() + "&currentPage=1&buttonsToShow=" + this.value );
    });
}
