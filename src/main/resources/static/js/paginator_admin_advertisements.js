$(document).ready(function() {
    changePageAndSize();
});

function changePageAndSize() {
    $('#pageSizeSelect').change(function(evt) {
        window.location.replace("/admin/advertisements/?pageSize=" + this.value + "&page=1");
    });
}
