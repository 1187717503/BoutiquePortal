CKEDITOR.dialog.add(
    "serverimg",
    function (editor) {
        var timestamp = Math.round(new Date().getTime() / 1000);
        var ckeditorPage = 'upload.html?timestamp='+timestamp;
        return {
            title: "上传图片",
            minWidth: 350,
            minHeight: 45,
            contents:
            [
                {
                    id: "tab1",
                    label: "",
                    title: "",
                    expand: true,
                    padding: 0,
                    elements:
                    [{
                        type: "html",
                        html: "<iframe id='img_browser' name='img_browser' src='" + ckeditorPage + "'></iframe>",
                        style: "width:100%;height:45px;padding:0;"
                    }]
                }
            ],
            onOk: function () {

                let imgUrl = localStorage.getItem('ckeditor-uploadImage');
                editor.insertHtml("<img src='"+imgUrl+"' />");
                localStorage.removeItem('ckeditor-uploadImage')
                
            },
            onCancel: function() {
                localStorage.removeItem('ckeditor-uploadImage')
            },
            onShow : function() {
                localStorage.removeItem('ckeditor-uploadImage')
            }
        };
    }
);