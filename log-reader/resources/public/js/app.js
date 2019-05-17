function on_collapse(e) {
    const container = $(e.target).closest('.collapsable');

    if (container.hasClass('collapsed')) {
        container.removeClass('collapsed');
    } else {
        container.addClass('collapsed');
    }
}

function install_collapse_handlers() {
    $('.collapsable').on('click', on_collapse);
};

$(document).ready(function() {
    install_collapse_handlers();
});
