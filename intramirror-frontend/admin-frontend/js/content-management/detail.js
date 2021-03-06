var uploadDropzone;

String.prototype.gblen = function() {
    var len = 0;
    for (var i=0; i<this.length; i++) {
        if (this.charCodeAt(i) > 127 || this.charCodeAt(i) == 94) {
           len += 2;
        } else {
           len ++;
        }
    }
  return len;
}

function initDragger() {
    var leftUpdateOutput = function (e) {
        var list = e.length ? e : $(e.target),
            output = list.data('output');
        if (window.JSON) {
            output.val(window.JSON.stringify(list.nestable('serialize'))); //, null, 2));
        } else {
            output.val('JSON browser support required for this demo.');
        }
        adjustRightSeqNo();
    };

    var rightUpdateOutput = function (e) {

        var list = e.length ? e : $(e.target),
            output = list.data('output');

        if (window.JSON) {
            output.val(window.JSON.stringify(list.nestable('serialize'))); //, null, 2));
        } else {
            output.val('JSON browser support required for this demo.');
        }
        adjustRightSeqNo();
    };

    // activate Nestable for list 1
    $('#left-nestable').nestable({
            group: 0,
            maxDepth: 1,
            handleClass: 'dragger-able-ele'
        })
        .on('change', leftUpdateOutput);

    // activate Nestable for list 2
    $('#right-nestable').nestable({
            group: 0,
            maxDepth: 1,
            handleClass: 'dragger-able-ele'
        })
        .on('change', rightUpdateOutput);

    // output initial serialised data
    leftUpdateOutput($('#left-nestable').data('output', $('#left-nestable-output')));
    rightUpdateOutput($('#right-nestable').data('output', $('#right-nestable-output')));
}

function adjustRightSeqNo() {
    $('.right-product-list .dd-list .prod-item .sort').each(function(idx, e) {
        $(this).text('Sort: ' + (idx + 1));
        $(this).attr('data-sort', (idx + 1));
    })

    $('.left-product-list .dd-list .prod-item .sort').each(function(idx, e) {
        $(this).text('Sort: -1');
        $(this).attr('data-sort', -1);
    })
}

function saveBlock(isNew) {

    let param = {};

    param.block = {};
    param.tag = {}

    if(isNew == '1') {
        param.block.blockName = $('#text-blockName').val();
        param.block.status = 1;
        param.tag.tagName = $('#text-product-tag').val();
        param.isNew = '1';

        if (param.block.blockName == '') {
            Materialize.toast('Please type block name', 3000);
            return;
        }

    } else {
        param.isNew = '0';
        param.block.blockId = $('#text-blockName').data('block-id');
        if (param.block.blockId == null || param.block.blockId == -1) {
            Materialize.toast('Get BlockId failed', 3000);
            return;
        }

        param.tag.tagId = $('#text-product-tag').data('tag-id');
    }

    param.block.title = $('#text-title').val();
    if (param.block.title === '') {
        Materialize.toast('Please type title', 3000);
        return;
    }

    if (param.block.title.gblen() > 40) {
        Materialize.toast('Title must be less 40 character', 3000);
        return;
    }

    param.block.titleEnglish = $('#text-english-name').val();
    param.block.subtitle = $('#text-sub-title').val();
    if (param.block.subtitle == '') {
        Materialize.toast('Please type sub title', 3000);
        return;
    }
    if (param.block.subtitle.gblen() > 108) {
        Materialize.toast('Sub title must be less 108 character', 3000);
        return;
    }

    param.block.coverImg = $('#upload').attr('data-block-image');
    if (param.block.coverImg == '' || typeof(param.block.coverImg) == 'undefined') {
        Materialize.toast('Please upload block image', 3000);
        return;
    }

    param.block.bgColor = $('#backgroup-color').val();
    param.block.textColor = $('#title-color').val();
    param.block.sortOrder = parseInt($('#text-sort-order').val());

    if (isNaN(param.block.sortOrder)) {
        Materialize.toast('Please type sort order', 3000);
        return;
    }

    if (param.block.sortOrder >= 65535) {
        Materialize.toast('Sort order must be less then 65535', 3000);
        return;
    }

    if (param.block.sortOrder <= 0) {
        Materialize.toast('Sort order must be large then 0', 3000);
        return;
    }

    var editor = CKEDITOR.instances.blockContent;
    var b = new Base64();
    param.block.content = b.encode(editor.getData());


    param.sort = []

    $('.right-product-list .dd-list .prod-item .sort').each(function(idx, e) {
        let sortInfo = {}
        sortInfo.tagId = param.tag.tagId;
        sortInfo.productId = $(this).closest('.product-info-display').data('product-id');
        sortInfo.sortNum = idx + 1;
        param.sort.push(sortInfo);
    })

    $('.left-product-list .dd-list .prod-item .sort').each(function(idx, e) {
        let sortInfo = {}
        sortInfo.tagId = param.tag.tagId;
        sortInfo.productId = $(this).closest('.product-info-display').data('product-id');
        sortInfo.sortNum = -1;
        param.sort.push(sortInfo);
    })


    $.ajax({
        type: requestURL.saveBlock.method,
        contentType: "application/json",
        url: requestURL.saveBlock.url,
        data: JSON.stringify(param),
        dataType: 'json',
        beforeSend: function (request) {
            request.setRequestHeader("token", token);
        },
        success: function (result) {
            if (result.status === 1) {
                Materialize.toast('Save success', 3000);
                window.location.href = './management.html'
            } else {
                toashWithCloseBtn('Save failed, ' + result.data);
            }
        },
        error: function (code, exception) {
            if (code.status == 401) {
                window.location.href = '../../../login'
            } else {
                toashWithCloseBtn('Save failed, ' + exception);
            }
        }
    });
}


function initBlock(blockId) {
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

function initProductList(curTagId) {

    $('.left-product-list').empty();
    $('.right-product-list').empty();
    $.ajax({
        type: requestURL.getProductsByTag.method,
        contentType: "application/json",
        url: requestURL.getProductsByTag.url.replace("{tagId}",curTagId),
        data: {},
        dataType: 'json',
        beforeSend: function(request) {
            request.setRequestHeader("token", token);
        },
        success: function(result) {

            let leftProductList = [];
            let rightProductList = [];

            for (var i = 0; i < result.data.length; i++) {

                 result.data[i].image = '../images/noimage.gif';
                if (result.data[i].cover_img) {
                    let obj = JSON.parse(result.data[i].cover_img);
                    if (obj.length > 0) {
                        result.data[i].image = obj[0];
                    }
                }

                result.data[i].im_discount = (result.data[i].im_discount*100).toFixed(0);
                if (result.data[i].preview_discount) {
                    result.data[i].previewDiscount = (result.data[i].preview_discount*100).toFixed(0);
                }

                if (result.data[i].im_price) {
                    result.data[i].im_price = result.data[i].im_price.toFixed(2);
                }

                // 街拍图拿不到
                result.data[i].steet = 0;
                if (result.data[i].img_modified) {
                    result.data[i].model = 1;
                } else {
                    result.data[i].model = 0;
                }


                if (result.data[i].sort_num == -1) {

                    leftProductList.push(result.data[i]);
                } else {
                    result.data[i].sort = i + 1;
                    rightProductList.push(result.data[i]);
                }
            }

            if (leftProductList.length > 0) {
                $('#tmpl-product-list').tmpl({list: leftProductList}).appendTo('.left-product-list');
            } else {
                $('.left-product-list').append('<div class="dd-empty"></div>');
            }

            if (rightProductList.length > 0) {
                $('#tmpl-product-list').tmpl({list: rightProductList}).appendTo('.right-product-list');
            } else {
                $('.right-product-list').append('<div class="dd-empty"></div>');
            }
            adjustRightSeqNo();

            $('.dragger-able-ele .preview-pic').click(function() {
                let images = $(this).data('images');

                let itemsValue = [];
                for (var i = 0; i < images.length; i++) {
                    itemsValue.push({"src": images[i]});
                }
             
                $.magnificPopup.open({
                    items: itemsValue,
                    type: 'image',
                    fixedContentPos: true,
                    gallery: {
                        enabled: true,
                        navigateByImgClick: true,
                        preload: [0, 1]
                    },
                }, 0);
            });


        }, error: function(code, exception) {

            if (code.status == 401) {
                window.location.href = '../../../login'
            } else {
                toashWithCloseBtn('Get product list failed.' + code.responseJSON.message);
            }
        }
    });
}

function initBlockDetailInfo(data) {
    var editor = CKEDITOR.instances.blockContent;
    if (typeof(data.content) != 'undefined' && data.content != '') {
        var b = new Base64();
        editor.setData(b.decode(data.content));
    } else {
        editor.setData('');
    }


    $('#text-title').val(data.title);
    $('#text-english-name').val(data.title_english);
    $('#text-sub-title').val(data.subtitle);
    $('#text-sort-order').val(data.sort_order);
    $('#backgroup-color').val(data.bg_color);
    $('#title-color').val(data.text_color);
    $('label[for=last-modified]').text(data.modified_at);
    if (data.status == 1) {
        $('label[for=status]').text('active');
    } else {
        $('label[for=status]').text('inactive');
    }
    Materialize.updateTextFields();
    uploadDropzone.removeAllFiles(true);
    $('.dz-preview.dz-complete.dz-image-preview').remove();
    $('.needsclick.dz-message').css('display', "none");

    if (data.cover_img) {
        loadExistingImage(data.cover_img);
        $('#upload').attr('data-block-image', data.cover_img);
    } else {
        $('.needsclick.dz-message').css('display', "block");
    }
}

function getBlockDetail(blockId) {
    $('.left-product-list').empty();
    $('.right-product-list').empty();
    console.log("blockId : " + blockId);
    console.log("url : " + requestURL.getBlockDetail.url.replace("\{blockId\}",blockId));

    $.ajax({
        type: requestURL.getBlockDetail.method,
        contentType: "application/json",
        url: requestURL.getBlockDetail.url.replace("{blockId}",blockId),
        data: {},
        dataType: 'json',
        beforeSend: function(request) {
            request.setRequestHeader("token", token);
        },
        success: function(result) {
            initBlockDetailInfo(result.data);
            //名字能改，但是后台需要修改才能存 2017-12-17
            $('#text-blockName').val(result.data.block_name);
            $('#text-blockName').attr("data-block-id",result.data.block_id);
            //$('#text-blockName').focus();

            //名字不能改的方案 2017-12-17
            $('#text-blockName').attr("disabled","disabled");
            $('#lab-blockName').addClass("placehoder-label");

            $('#text-product-tag').val(result.data.tag_name);
            $('#text-product-tag').attr("data-tag-id",result.data.tag_id);
            initProductList(result.data.tag_id);
        }, error: function(code, exception) {
            if (code.status == 401) {
                window.location.href = '../../../login'
            } else {
                toashWithCloseBtn('Get block detail failed.' + code.responseJSON.message);
            }
        }
    });
}


function initUpload() {
    Dropzone.options.upload = false;
    uploadDropzone = new Dropzone("div#upload", {
        url: requestURL.uploadImage.url,
        method: requestURL.uploadImage.method,
        paramName: "file",
        maxFilesize: 5,
        maxFiles: 1,
        headers: {
            "token": token
        },
        thumbnailWidth: 140,
        thumbnailHeight: 160,
        init: function () {
            this.on("success", function success(file, response) {
                $('#upload').attr('data-block-image', response.data);
            });

            this.on("maxfilesexceeded", function (file) {
                this.removeAllFiles(true);
                this.addFile(file);
            });
            this.on("addedfile", function () {
                $('.dz-preview.dz-complete.dz-image-preview').remove();
                $('.needsclick.dz-message').css('display', "none");
            });
        }
    });
}


function loadExistingImage(url) {
    uploadDropzone.removeAllFiles(true);
    $('.dz-preview.dz-complete.dz-image-preview').remove();
    $('.needsclick.dz-message').css('display', "none");

    var mockFile = {
        name: "cover_image.png",
        size: 0,
        dataURL: url
    };

    uploadDropzone.emit("addedfile", mockFile);
    uploadDropzone.emit("thumbnail", mockFile, url + '?x-oss-process=image/resize,m_fill,w_140,h_160/quality,Q_100/auto-orient,0/format,jpg');
    uploadDropzone.emit("complete", mockFile);
}


function initEvent(isNew) {
    $('#save-btn').click(function () {
        saveBlock(isNew);
    })

    $('#cancel-btn').click(function () {
        window.location.href = './management.html';
    })

    $('#select-block-name').change(function(e) {
        let oriBlockId = $('#select-block-name').data('origin-block-id');
        if (typeof(oriBlockId) =='undefined' || oriBlockId == -1) {
            let blockId = $('#select-block-name').val();
            $('#select-block-name').attr('data-origin-block-id', blockId);
            getBlockDetail(blockId)
            return;
        }

        $('#modal2').openModal({
            dismissible: false,
            opacity: .5,
            inDuration: 300,
            outDuration: 200,
            startingTop: '4%',
            endingTop: '10%'
        });
    });

    $('#modal1 .model-yes').click(function() {
        $('#modal1').closeModal();
        let productTag = $('#select-product-tag').val();
        $('#select-product-tag').attr('data-origin-product-id', productTag);
        initProductList(productTag);
    })

    $('#modal2 .model-yes').click(function() {
        $('#modal2').closeModal();

        let blockId = $('#select-block-name').val();
        $('#select-block-name').attr('data-origin-block-id', blockId);
        getBlockDetail(blockId)
    })

    $('#modal1 .model-no').click(function() {
        let oriProductTag = $('#select-product-tag').data('origin-product-id');
        $('#select-product-tag').val(oriProductTag);
        $('#select-product-tag').material_select();

        $('#modal1').closeModal();
    })

    $('#modal2 .model-no').click(function() {
        let oriBlockId = $('#select-block-name').data('origin-block-id');
        if (typeof(oriBlockId) =='undefined') {
            oriBlockId = -1;
        }

        $('#select-block-name').val(oriBlockId);
        $('#select-block-name').material_select();
        $('#modal2').closeModal();
    })

    $('#select-product-tag').change(function(e) {
        $('#modal1').openModal({
            dismissible: false,
            opacity: .5,
            inDuration: 300,
            outDuration: 200,
            startingTop: '4%',
            endingTop: '10%'
        });
    })

    $("#text-blockName").keyup(function(){
        $("#text-product-tag").val($("#text-blockName").val());
    });
}
