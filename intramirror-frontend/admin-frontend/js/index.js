

function getBrand() {
    var list = [];
    list.push("Coach");
    list.push("Claude Montana");
    list.push("Drome");
    list.push("Lover");
    list.push("Maharishi");
    list.push("Moscot");
    list.push("Zanone");
    return list
}


function initSelectItems(elemId, tmplId, listData) {
    $('#' + elemId).material_select();

    // clear contents
    var $selectDropdown = $('#' + elemId).empty().html('');

    $('#' + tmplId).tmpl({list: listData}).appendTo('#' + elemId);
    $('#' + elemId).material_select();

    // trigger event
    $selectDropdown.trigger('contentChanged');

    $('#' + elemId).on('contentChanged', function() {
        // re-initialize (update)
        $(this).material_select();
    });
}