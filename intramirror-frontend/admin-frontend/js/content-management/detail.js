var uploadDropzone;

String.prototype.gblen = function() {  
  var len = 0;  
  for (var i=0; i<this.length; i++) {  
    if (this.charCodeAt(i)>127 || this.charCodeAt(i)==94) {  
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

function saveBlock() {
    
    let param = {};

    param.block = {};
    param.block.blockId = $('#select-block-name').val();
    if (param.block.blockId == null || param.block.blockId == -1) {
        Materialize.toast('Please select block', 3000);
        return;
    }

    param.tag = {}
    param.tag.tagId = $('#select-product-tag').val();
    if (param.tag.tagId == null || param.tag.tagId == -1) {
        Materialize.toast('Please select product tag', 3000);
        return;
    }

    param.block.title = $('#text-title').val();
    if (param.block.title === '') {
        Materialize.toast('Please type title', 3000);
        return;
    }

    if (param.block.title.gblen() > 22) {
        Materialize.toast('Title must be less 22 character', 3000);
        return;
    }

    param.block.titleEnglish = $('#text-english-name').val();
    if (param.block.titleEnglish === '') {
        Materialize.toast('Please type title in english', 3000);
        return;
    }

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
    if (param.block.coverImg == '') {
        Materialize.toast('Please upload block image', 3000);
        return;
    }

    param.block.bgColor = $('#backgroup-color').val();
    param.block.sortOrder = $('#text-sort-order').val();
    if (param.block.sortOrder == '') {
        Materialize.toast('Please type sort order', 3000);
        return;
    }

    if (param.block.sortOrder <= 0) {
        Materialize.toast('Sort order must be large then 0', 3000);
        return;
    }


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
        url: requestURL.getProductsByTag.url + "?tagId=" + curTagId,
        data: {},
        dataType: 'json',
        beforeSend: function(request) {
            request.setRequestHeader("token", token);
        },
        success: function(result) {

            let leftProductList = [];
            let rightProductList = [];

            for (var i = 0; i < result.data.length; i++) {
                let obj = JSON.parse(result.data[i].cover_img);
                if (obj.length > 0) {
                    result.data[i].image = obj[0];
                } else {
                    result.data[i].image = '../images/noimage.gif';
                }

                result.data[i].im_discount = (result.data[i].im_discount*10).toFixed(1);

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
        }, error: function(code, exception) {

            if (code.status == 401) {
                window.location.href = '../../../login'
            } else {
                toashWithCloseBtn('Get product list failed.' + code.responseJSON.message);
            }
        }
    });
}

function initTagSelect(blockId, curTagId) {
    if (typeof(curTagId) == 'undefined' || curTagId == '') {
        curTagId = "-1";
    } else {
        initProductList(curTagId);
    }


    $('#select-product-tag').empty(); 
    $.ajax({
        type: requestURL.getUnBindTags.method,
        contentType: "application/json",
        url: requestURL.getUnBindTags.url + "?blockId=" + blockId,
        data: {},
        dataType: 'json',
        beforeSend: function(request) {
            request.setRequestHeader("token", token);
        },
        success: function(result) {
            
            $('#tmpl-product-tags-select').tmpl({list: result.data, curTagId: curTagId}).appendTo('#select-product-tag');
            $('#select-product-tag').material_select();
            $('#select-product-tag').attr('data-origin-product-id', curTagId);
            
        }, error: function(code, exception) {

            if (code.status == 401) {
                window.location.href = '../../../login'
            } else {
                toashWithCloseBtn('Get product tags failed.' + code.responseJSON.message);
            }
        }
    });
}

function initBlockDetailInfo(data) {

    $('#text-title').val(data.title);
    $('#text-english-name').val(data.title_english);
    $('#text-sub-title').val(data.subtitle);
    $('#text-sort-order').val(data.sort_order);
    $('#backgroup-color').val(data.bg_color);
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

    $.ajax({
        type: requestURL.getBlockDetail.method,
        contentType: "application/json",
        url: requestURL.getBlockDetail.url + "?blockId=" + blockId,
        data: {},
        dataType: 'json',
        beforeSend: function(request) {
            request.setRequestHeader("token", token);
        },
        success: function(result) {
            initBlockDetailInfo(result.data);
            initTagSelect(blockId, result.data.tag_id);
            
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
            });
        }
    });
}


function loadExistingImage(url) {
    uploadDropzone.removeAllFiles(true);
    $('.dz-preview.dz-complete.dz-image-preview').remove();

    var mockFile = {
        name: "cover_image.png",
        size: 0,
        dataURL: url
    };

    uploadDropzone.emit("addedfile", mockFile);
    uploadDropzone.emit("thumbnail", mockFile, url + '?x-oss-process=image/resize,m_fill,w_140,h_160/quality,Q_100/auto-orient,0/format,jpg');
    uploadDropzone.emit("complete", mockFile);
}


function initEvent() {
    $('#save-btn').click(function () {
        saveBlock();
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
}