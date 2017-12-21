CKEDITOR.plugins.add(
    "serverimg",
    {
        requires: ["dialog"],
        init: function (editor) {
            editor.addCommand("serverimg", new CKEDITOR.dialogCommand("serverimg"));
            editor.ui.addButton(
             "serverimg",
            {
                label: "上传图片",
                command: "serverimg",
                icon: this.path + "upload.png",
                toolbar: 'insert'
            });
            CKEDITOR.dialog.add("serverimg", this.path + "dialogs/code.js");
        }
    }
);