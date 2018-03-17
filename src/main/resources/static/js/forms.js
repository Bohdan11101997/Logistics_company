function validate(input) {
    if (input.value !== document.getElementById("password").value) {
        input.setCustomValidity("Passwords don't match");
    } else {
        input.setCustomValidity("");
    }
}

function clearErrorStyles(input) {
    var parent = input.parentElement;
    parent.classList.remove("has-error");
    var children = parent.children;
    var removalNeeded = false;
    for (var i = 0; i < children.length; i++) {
        if (children[i].classList.contains('help-block')) removalNeeded = true;
        if (removalNeeded) {
            parent.removeChild(children[i]);
        }
    }
}


function createMultiSelect(selector, selectAll) {
    var el = $(selector);
    var elContainer = null;

    $(document).ready(function () {
        el.multiselect({
            numberDisplayed: 3,
            delimiterText: ", ",
            allSelectedText: false,
            onChange: function (option, checked, select) {
                clearErrorStyles(elContainer);
                var values = [];
                el.find("option").each(function () {
                    values.push(this.selected);
                });
                var noneSelected = values.every(function (val) {
                    return val === false;
                });
                if (noneSelected) {
                    el.multiselect("select", option.val());
                }
            },
            onInitialized: function (select, container) {
                elContainer = container[0];
                setTimeout(function () {
                    var values = [];
                    if (selectAll) {
                        el.find("option").each(function () {
                            values.push(this.value);
                        });
                        el.multiselect("select", values);
                    } else {
                        el.find("option").each(function () {
                            values.push(this.selected);
                        });
                        var noneSelected = values.every(function (val) {
                            return val === false;
                        });
                        if (noneSelected) {
                            var firstValue = el.find("option").first().val();
                            el.multiselect("select", firstValue);
                        }
                    }
                }, 0);
            }
        });
    });
}

function onSearchDateClick(searchDate) {
    clearErrorStyles(searchDate);
    var type = searchDate.id === 'from-date' ? 'from' : 'to';
    if (type === 'from') {
        var fromDate = new Date(searchDate.value);
        var to = document.getElementById('to-date');
        var toDate = new Date(to.value);
        if (fromDate > toDate) {
            searchDate.setCustomValidity('From date must be earlier than To')
        } else {
            searchDate.setCustomValidity('');
            to.setCustomValidity('');
        }
    } else {
        var toDate = new Date(searchDate.value);
        var from = document.getElementById('from-date');
        var fromDate = new Date(from.value);
        if (fromDate > toDate) {
            searchDate.setCustomValidity('From date must be earlier than To')
        } else {
            searchDate.setCustomValidity('');
            from.setCustomValidity('');
        }
    }
}

function createDatePicker(selector, onChangeDate) {
    $(selector).datepicker({
        todayHighlight: true,
        autoclose: true,
        format: "yyyy-mm-dd"
    }).on('changeDate', onChangeDate);
}