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

function createMultiSelect(selectText, displayCount) {
    displayCount = +displayCount > 0 ? +displayCount : 2; // filter out invalid values

    var el = $('.selectpicker');

    el.selectpicker({
        selectedTextFormat: "count > " + displayCount
    });

    if (selectText) {
        el.on("loaded.bs.select", function (event) {
            el.selectpicker("deselectAll");
            var options = event.target.options;
            for (var i = 0; i < options.length; i++) {
                if (options[i].text === selectText) {
                    el.selectpicker("val", options[i].value);
                    return;
                }
            }
        });
    } else {
        el.selectpicker("selectAll");
    }

    el.on("changed.bs.select", function (event) {
        var options = event.target.options;
        for (var i = 0; i < options.length; i++) {
            if (options[i].selected) {
                return;
            }
        }
        el.selectpicker("selectAll");
    });
}

function OLD_createMultiSelect(selector, selectAll) {
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

function createDatePicker(selector, onChangeDate) {
    $(selector).datepicker({
        todayHighlight: true,
        format: "yyyy-mm-dd"
    }).on('changeDate', onChangeDate);
}