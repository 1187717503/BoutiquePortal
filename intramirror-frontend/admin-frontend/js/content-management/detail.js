function initDragger() {
    var updateOutput = function (e) {
        var list = e.length ? e : $(e.target),
            output = list.data('output');
        if (window.JSON) {
            output.val(window.JSON.stringify(list.nestable('serialize'))); //, null, 2));
        } else {
            output.val('JSON browser support required for this demo.');
        }
    };

    // activate Nestable for list 1
    $('#left-nestable').nestable({
            group: 0,
            maxDepth: 1,
            handleClass: 'dragger-able-ele'
        })
        .on('change', updateOutput);

    // activate Nestable for list 2
    $('#right-nestable').nestable({
            group: 0,
            maxDepth: 1,
            handleClass: 'dragger-able-ele'
        })
        .on('change', updateOutput);

    // output initial serialised data
    updateOutput($('#left-nestable').data('output', $('#left-nestable-output')));
    updateOutput($('#right-nestable').data('output', $('#right-nestable-output')));
}

function initBlock() {
    $.ajax({
        type: requestURL.getBlock.method,
        contentType: "application/json",
        url: requestURL.getBlock.url,
        data: {},
        dataType: 'json',
        beforeSend: function (request) {
            request.setRequestHeader("token", token);
        },
        success: function (result) {
            console.log(result.data);
            $('#select-block-name').empty();
            $('#tmpl-block-select').tmpl({
                list: result.data
            }).appendTo('#select-block-name');
            $('#select-block-name').material_select();
        },
        error: function (code, exception) {

            if (code.status == 401) {
                window.location.href = '../../../login'
            }
        }
    });
}

function initProductTags() {
    $.ajax({
        type: requestURL.getBlock.method,
        contentType: "application/json",
        url: requestURL.getBlock.url,
        data: {},
        dataType: 'json',
        beforeSend: function (request) {
            request.setRequestHeader("token", token);
        },
        success: function (result) {
            console.log(result.data);
            $('#select-block-name').empty();
            $('#tmpl-block-select').tmpl({
                list: result.data
            }).appendTo('#select-block-name');
            $('#select-block-name').material_select();
        },
        error: function (code, exception) {

            if (code.status == 401) {
                window.location.href = '../../../login'
            }
        }
    });
}

function initUpload() {
    Dropzone.options.upload = {
        url: requestURL.uploadImage.url,
        method: requestURL.uploadImage.method,
        paramName: "file", // The name that will be used to transfer the file
        maxFilesize: 5, // MB
        maxFiles: 1,
        headers: {
            "token": token
        },
        init: function () {
            this.on("success", function success(file, response) {
                console.log(response);
            });
            this.on("maxfilesexceeded", function (file) {
                this.removeAllFiles(true);
                this.addFile(file);
            });
        }
    };

}

function getProductsByTag(tagId) {

    $.ajax({
        type: requestURL.getProductsByTag.method,
        contentType: "application/json",
        url: requestURL.getProductsByTag.url + '?tagId=' + tagId,
        data: {},
        dataType: 'json',
        beforeSend: function (request) {
            request.setRequestHeader("token", token);
        },
        success: function (result) {
            console.log(result.data);

        },
        error: function (code, exception) {

            if (code.status == 401) {
                window.location.href = '../../../login'
            }
        }
    });
}

function saveAction() {
    console.log($('#backgroup-color').val());
}